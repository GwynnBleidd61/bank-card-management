package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardCreateRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.CardStatusUpdateRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardTransactionRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CryptoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceImplTest {

    private CardRepository cardRepository;
    private UserRepository userRepository;
    private CardTransactionRepository transactionRepository;
    private CryptoService cryptoService;

    private CardServiceImpl cardService;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        userRepository = mock(UserRepository.class);
        transactionRepository = mock(CardTransactionRepository.class);
        cryptoService = mock(CryptoService.class);

        cardService = new CardServiceImpl(cardRepository, userRepository, transactionRepository, cryptoService);
    }

    @Test
    void createCard_savesEncryptedCardAndReturnsMaskedDto() {
        Long userId = 5L;
        User user = new User();
        user.setId(userId);

        CardCreateRequestDto request = new CardCreateRequestDto(
                userId,
                "1234123412341234",
                "John Doe",
                LocalDate.of(2030, 12, 31),
                new BigDecimal("10.00")
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cryptoService.encrypt(request.rawCardNumber())).thenReturn("encrypted-number");
        when(cryptoService.decrypt("encrypted-number")).thenReturn(request.rawCardNumber());
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card card = invocation.getArgument(0);
            card.setId(100L);
            return card;
        });

        CardResponseDto response = cardService.createCard(request);

        assertEquals(100L, response.id());
        assertEquals("**** **** **** 1234", response.maskedCardNumber());
        assertEquals(CardStatus.ACTIVE, response.status());
        assertEquals(new BigDecimal("10.00"), response.balance());

        ArgumentCaptor<Card> cardCaptor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(cardCaptor.capture());
        Card savedCard = cardCaptor.getValue();
        assertEquals(user, savedCard.getUser());
        assertEquals("encrypted-number", savedCard.getCardNumberEncrypted());
        assertEquals(CardStatus.ACTIVE, savedCard.getStatus());
    }

    @Test
    void createCard_throwsWhenUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        CardCreateRequestDto request = new CardCreateRequestDto(
                1L,
                "1111",
                "User",
                LocalDate.now(),
                null
        );

        assertThrows(EntityNotFoundException.class, () -> cardService.createCard(request));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void updateStatus_updatesCardStatus() {
        Card card = new Card();
        card.setId(9L);
        card.setStatus(CardStatus.ACTIVE);
        card.setCardNumberEncrypted("encrypted-number");

        when(cardRepository.findById(9L)).thenReturn(Optional.of(card));
        when(cryptoService.decrypt("encrypted-number")).thenReturn("1234123412341234");
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CardStatusUpdateRequestDto request = new CardStatusUpdateRequestDto(CardStatus.BLOCKED);

        CardResponseDto result = cardService.updateStatus(9L, request);

        assertEquals(CardStatus.BLOCKED, result.status());
        verify(cardRepository).save(card);
    }

    @Test
    void deleteCard_throwsWhenTransactionsExist() {
        Card card = new Card();
        when(cardRepository.findById(2L)).thenReturn(Optional.of(card));
        when(transactionRepository.existsByFromCard(card)).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cardService.deleteCard(2L)
        );

        assertEquals("Cannot delete card with existing transactions", exception.getMessage());
        verify(cardRepository, never()).delete(any());
    }

    @Test
    void requestCardBlock_blocksActiveCard() {
        User user = new User();
        user.setId(1L);
        Card card = new Card();
        card.setId(2L);
        card.setUser(user);
        card.setStatus(CardStatus.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(card));

        cardService.requestCardBlock(1L, 2L);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void requestCardBlock_throwsWhenAlreadyBlocked() {
        User user = new User();
        user.setId(1L);
        Card card = new Card();
        card.setStatus(CardStatus.BLOCKED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(card));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cardService.requestCardBlock(1L, 2L)
        );

        assertEquals("Card is already blocked", exception.getMessage());
        verify(cardRepository, never()).save(any());
    }

    @Test
    void maskCard_handlesShortNumbers() {
        assertEquals("****", cardService.maskCard("123"));
    }
}