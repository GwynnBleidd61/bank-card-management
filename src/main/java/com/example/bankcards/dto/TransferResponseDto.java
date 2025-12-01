package com.example.bankcards.dto;

import com.example.bankcards.entity.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponseDto(
        Long transactionId,
        Long fromCardId,
        Long toCardId,
        BigDecimal amount,
        TransactionStatus status,
        LocalDateTime createdAt,
        String description
) {
}
