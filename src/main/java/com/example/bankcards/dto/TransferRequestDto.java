package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequestDto(

        @NotNull(message = "From card id is required")
        Long fromCardId,

        @NotNull(message = "To card id is required")
        Long toCardId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0")
        BigDecimal amount,

        String description
) { }
