package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardCreateRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.bankcards.dto.CardStatusUpdateRequestDto;


@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Override
    public CardResponseDto createCard(CardCreateRequestDto dto) {

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String encrypted = encryptCardNumber(dto.rawCardNumber());

        Card card = new Card();
        card.setUser(user);
        card.setCardNumberEncrypted(encrypted);
        card.setCardholderName(dto.cardholderName());
        card.setExpiryDate(dto.expiryDate());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(dto.initialBalance() != null ? dto.initialBalance() : BigDecimal.ZERO);
        card.setCreatedAt(LocalDateTime.now());

        Card saved = cardRepository.save(card);

        return toDto(saved);
    }

    @Override
    public String maskCard(String decryptedNumber) {
        if (decryptedNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + decryptedNumber.substring(decryptedNumber.length() - 4);
    }

    @Override
    public String encryptCardNumber(String rawNumber) {
        return "ENC(" + rawNumber + ")"; // временная заглушка
    }

    @Override
    public CardResponseDto updateStatus(Long cardId, CardStatusUpdateRequestDto dto) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));

        card.setStatus(dto.status());
        card.setUpdatedAt(LocalDateTime.now());

        Card saved = cardRepository.save(card);
        return toDto(saved);
    }

    @Override
    public Page<CardResponseDto> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(this::toDto);
    }


    private CardResponseDto toDto(Card card) {
        // decryptedNumber пока имитируем: берем последние 4 цифры из зашифрованного
        String fakeDecrypted = card.getCardNumberEncrypted()
                .replace("ENC(", "")
                .replace(")", "");

        return new CardResponseDto(
                card.getId(),
                maskCard(fakeDecrypted),
                card.getCardholderName(),
                card.getExpiryDate(),
                card.getStatus(),
                card.getBalance()
        );
    }

}
