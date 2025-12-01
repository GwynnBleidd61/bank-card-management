package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.CardStatusUpdateRequestDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CardService {

    CardResponseDto createCard(CardCreateRequestDto dto);

    String maskCard(String decryptedNumber);

    String encryptCardNumber(String rawNumber);

    CardResponseDto updateStatus(Long cardId, CardStatusUpdateRequestDto dto);

    Page<CardResponseDto> getAllCards(Pageable pageable);

    Page<CardResponseDto> getUserCards(Long userId, Pageable pageable);

    void deleteCard(Long cardId);

    void requestCardBlock(Long userId, Long cardId);

}
