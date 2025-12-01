package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.dto.TransferResponseDto;

public interface TransferService {

    TransferResponseDto transferBetweenUserCards(Long userId, TransferRequestDto request);
}
