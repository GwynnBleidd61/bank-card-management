package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/cards")
@RequiredArgsConstructor
public class UserCardController {

    private final CardService cardService;

    @GetMapping
    public Page<CardResponseDto> getUserCards(@RequestParam Long userId, Pageable pageable) {
        return cardService.getUserCards(userId, pageable);
    }
}
