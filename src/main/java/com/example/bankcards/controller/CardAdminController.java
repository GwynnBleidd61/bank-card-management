package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
public class CardAdminController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponseDto> createCard(@Valid @RequestBody CardCreateRequestDto request) {
        CardResponseDto response = cardService.createCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
