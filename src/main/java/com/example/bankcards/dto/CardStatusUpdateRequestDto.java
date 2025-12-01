package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.NotNull;

public record CardStatusUpdateRequestDto(

        @NotNull(message = "Status is required")
        CardStatus status
) { }
