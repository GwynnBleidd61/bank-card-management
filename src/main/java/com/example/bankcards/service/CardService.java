package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequestDto;
import com.example.bankcards.dto.CardResponseDto;

public interface CardService {

    CardResponseDto createCard(CardCreateRequestDto dto);

    String maskCard(String decryptedNumber);

    String encryptCardNumber(String rawNumber);
}
