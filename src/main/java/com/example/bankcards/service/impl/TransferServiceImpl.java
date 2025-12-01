package com.example.bankcards.service.impl;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.dto.TransferResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.CardTransaction;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardTransactionRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.TransferService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final CardTransactionRepository transactionRepository;

    @Override
    @Transactional
    public TransferResponseDto transferBetweenUserCards(Long userId, TransferRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (request.fromCardId().equals(request.toCardId())) {
            throw new BusinessException("Cannot transfer to the same card");
        }

        Card fromCard = cardRepository.findByIdAndUser(request.fromCardId(), user)
                .orElseThrow(() -> new BusinessException("Source card not found or does not belong to user"));

        Card toCard = cardRepository.findByIdAndUser(request.toCardId(), user)
                .orElseThrow(() -> new BusinessException("Target card not found or does not belong to user"));

        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new BusinessException("Both cards must be ACTIVE");
        }

        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be positive");
        }

        if (fromCard.getBalance().compareTo(request.amount()) < 0) {
            throw new BusinessException("Insufficient funds on source card");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(request.amount()));
        toCard.setBalance(toCard.getBalance().add(request.amount()));
        fromCard.setUpdatedAt(LocalDateTime.now());
        toCard.setUpdatedAt(LocalDateTime.now());

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        CardTransaction tx = new CardTransaction();
        tx.setFromCard(fromCard);
        tx.setToCard(toCard);
        tx.setAmount(request.amount());
        tx.setStatus(TransactionStatus.SUCCESS);
        tx.setCreatedAt(LocalDateTime.now());
        tx.setDescription(request.description());

        CardTransaction savedTx = transactionRepository.save(tx);

        return new TransferResponseDto(
                savedTx.getId(),
                fromCard.getId(),
                toCard.getId(),
                savedTx.getAmount(),
                savedTx.getStatus(),
                savedTx.getCreatedAt(),
                savedTx.getDescription()
        );
    }

    @Override
    public Page<TransferResponseDto> getUserTransactions(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return transactionRepository.findByUser(user, pageable)
                .map(tx -> new TransferResponseDto(
                        tx.getId(),
                        tx.getFromCard().getId(),
                        tx.getToCard().getId(),
                        tx.getAmount(),
                        tx.getStatus(),
                        tx.getCreatedAt(),
                        tx.getDescription()
                ));
    }

}
