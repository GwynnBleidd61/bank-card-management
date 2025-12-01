package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferResponseDto;
import com.example.bankcards.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/transactions")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final TransferService transferService;

    @GetMapping
    public Page<TransferResponseDto> getAllTransactions(Pageable pageable) {
        return transferService.getAllTransactions(pageable);
    }
}
