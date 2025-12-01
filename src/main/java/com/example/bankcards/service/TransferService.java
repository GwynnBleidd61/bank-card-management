package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.dto.TransferResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface TransferService {

    TransferResponseDto transferBetweenUserCards(Long userId, TransferRequestDto request);

    Page<TransferResponseDto> getUserTransactions(Long userId, Pageable pageable);

    Page<TransferResponseDto> getAllTransactions(Pageable pageable);


}
