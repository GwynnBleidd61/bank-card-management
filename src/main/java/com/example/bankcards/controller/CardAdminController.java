package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequestDto;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.CardStatusUpdateRequestDto;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


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

    @PatchMapping("/{id}/status")
    public ResponseEntity<CardResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody CardStatusUpdateRequestDto request
    ) {
        CardResponseDto response = cardService.updateStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<CardResponseDto>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CardResponseDto> result = cardService.getAllCards(pageable);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/user")
    public ResponseEntity<Page<CardResponseDto>> getUserCardsForAdmin(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CardResponseDto> result = cardService.getUserCards(userId, pageable);
        return ResponseEntity.ok(result);
    }
}
