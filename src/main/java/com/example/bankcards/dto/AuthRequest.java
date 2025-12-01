package com.example.bankcards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank(message = "Username is required")
        @Email(message = "Username must be a valid email")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 4, max = 100)
        String password
) {}

