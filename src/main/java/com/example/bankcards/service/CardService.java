package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.CardStatusUpdateRequestDto;


public interface CardService {

    CardResponseDto createCard(CardCreateRequestDto dto);

    String maskCard(String decryptedNumber);

    String encryptCardNumber(String rawNumber);

    CardResponseDto updateStatus(Long cardId, CardStatusUpdateRequestDto dto);
}
