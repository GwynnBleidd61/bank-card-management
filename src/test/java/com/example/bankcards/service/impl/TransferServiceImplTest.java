package com.example.bankcards.service.impl;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.dto.TransferResponseDto;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardTransactionRepository;
import com.example.bankcards.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceImplTest {

    private UserRepository userRepository;
    private CardRepository cardRepository;
    private CardTransactionRepository transactionRepository;

    private TransferServiceImpl transferService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        cardRepository = mock(CardRepository.class);
        transactionRepository = mock(CardTransactionRepository.class);

        transferService = new TransferServiceImpl(userRepository, cardRepository, transactionRepository);
    }

    @Test
    void transferBetweenUserCards_success() {
        Long userId = 1L;
        Long fromCardId = 10L;
        Long toCardId = 20L;
        BigDecimal amount = new BigDecimal("100.00");

        User user = new User();
        user.setId(userId);
        user.setUsername("user@test");
        user.setEnabled(true);

        Card fromCard = new Card();
        fromCard.setId(fromCardId);
        fromCard.setUser(user);
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setBalance(new BigDecimal("500.00"));

        Card toCard = new Card();
        toCard.setId(toCardId);
        toCard.setUser(user);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setBalance(new BigDecimal("50.00"));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.findByIdAndUser(fromCardId, user)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUser(toCardId, user)).thenReturn(Optional.of(toCard));

        // при сохранении транзакции вернём объект с id
        when(transactionRepository.save(any(CardTransaction.class)))
                .thenAnswer(invocation -> {
                    CardTransaction tx = invocation.getArgument(0);
                    tx.setId(100L);
                    tx.setCreatedAt(LocalDateTime.now());
                    tx.setStatus(TransactionStatus.SUCCESS);
                    return tx;
                });

        TransferRequestDto request = new TransferRequestDto(
                fromCardId,
                toCardId,
                amount,
                "Test transfer"
        );

        TransferResponseDto response = transferService.transferBetweenUserCards(userId, request);

        // Проверяем балансы
        assertEquals(new BigDecimal("400.00"), fromCard.getBalance());
        assertEquals(new BigDecimal("150.00"), toCard.getBalance());

        // Проверяем ответ
        assertNotNull(response);
        assertEquals(100L, response.transactionId());
        assertEquals(fromCardId, response.fromCardId());
        assertEquals(toCardId, response.toCardId());
        assertEquals(amount, response.amount());
        assertEquals(TransactionStatus.SUCCESS, response.status());
        assertEquals("Test transfer", response.description());

        // Проверяем, что были сохранены обе карты и транзакция
        verify(cardRepository, times(1)).save(fromCard);
        verify(cardRepository, times(1)).save(toCard);
        verify(transactionRepository, times(1)).save(any(CardTransaction.class));
    }
}
