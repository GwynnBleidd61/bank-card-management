package com.example.bankcards.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardCreateRequestDto(

        @NotNull
        Long userId,

        @NotBlank
        String rawCardNumber,

        @NotBlank
        String cardholderName,

        @NotNull
        @Future
        LocalDate expiryDate,

        @NotNull
        @PositiveOrZero
        BigDecimal initialBalance
) {
}
