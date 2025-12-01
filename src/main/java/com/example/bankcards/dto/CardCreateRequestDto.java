package com.example.bankcards.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CardCreateRequestDto(

        @NotNull(message = "User id is required")
        Long userId,

        @NotBlank(message = "Card number is required")
        @Pattern(regexp = "^[0-9]{12,19}$", message = "Card number must contain 12–19 digits")
        String rawCardNumber,

        @NotBlank(message = "Cardholder name is required")
        @Size(max = 100, message = "Cardholder name is too long")
        String cardholderName,

        @NotNull(message = "Expiry date is required")
        @Future(message = "Expiry date must be in the future")
        LocalDate expiryDate,

        @NotNull(message = "Initial balance is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Initial balance cannot be negative")
        BigDecimal initialBalance
) { }
