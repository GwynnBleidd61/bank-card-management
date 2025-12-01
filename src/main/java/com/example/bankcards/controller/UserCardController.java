package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.service.CardService;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.dto.TransferResponseDto;
import com.example.bankcards.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/user/cards")
@RequiredArgsConstructor
public class UserCardController {

    private final CardService cardService;
    private final TransferService transferService;


    @GetMapping
    public Page<CardResponseDto> getUserCards(@RequestParam Long userId, Pageable pageable) {
        return cardService.getUserCards(userId, pageable);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponseDto> transferBetweenUserCards(
            @RequestParam Long userId,
            @Valid @RequestBody TransferRequestDto request
    ) {
        TransferResponseDto response = transferService.transferBetweenUserCards(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{cardId}/block")
    public ResponseEntity<Void> requestBlockCard(
            @RequestParam Long userId,
            @PathVariable Long cardId
    ) {
        cardService.requestCardBlock(userId, cardId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204
    }


}
