package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.dto.TransferResponseDto;
import com.example.bankcards.entity.TransactionStatus;
import com.example.bankcards.exception.GlobalExceptionHandler;
import com.example.bankcards.security.CustomUserDetailsService;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserCardController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService cardService;

    @MockBean
    private TransferService transferService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private UserService userService;

    private static final String BASE_URL = "/api/user/cards";

    @Test
    void getUserCards_returnsOkAndCallsService() throws Exception {
        Long userId = 3L;
        Page<CardResponseDto> page = new PageImpl<>(List.of());
        when(cardService.getUserCards(eq(userId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(cardService, times(1)).getUserCards(eq(userId), any(Pageable.class));
    }

    @Test
    void transferBetweenCards_validRequest_returnsCreated() throws Exception {
        Long userId = 1L;
        TransferRequestDto requestDto = new TransferRequestDto(10L, 20L, new BigDecimal("50.00"), "test");
        TransferResponseDto responseDto = new TransferResponseDto(
                100L,
                10L,
                20L,
                new BigDecimal("50.00"),
                TransactionStatus.SUCCESS,
                LocalDateTime.of(2024, 1, 1, 12, 0),
                "test"
        );

        when(transferService.transferBetweenUserCards(eq(userId), any(TransferRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post(BASE_URL + "/transfer")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transactionId").value(100L))
                .andExpect(jsonPath("$.status").value(TransactionStatus.SUCCESS.name()));

        ArgumentCaptor<TransferRequestDto> captor = ArgumentCaptor.forClass(TransferRequestDto.class);
        verify(transferService, times(1)).transferBetweenUserCards(eq(userId), captor.capture());
        assertThat(captor.getValue().amount()).isEqualByComparingTo("50.00");
    }

    @Test
    void transferBetweenCards_invalidRequest_returnsBadRequest() throws Exception {
        Long userId = 1L;
        // missing amount violates validation
        TransferRequestDto invalid = new TransferRequestDto(10L, 20L, null, "test");

        mockMvc.perform(post(BASE_URL + "/transfer")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void requestBlockCard_returnsNoContentAndCallsService() throws Exception {
        Long userId = 2L;
        Long cardId = 15L;

        mockMvc.perform(post(BASE_URL + "/" + cardId + "/block")
                        .param("userId", userId.toString()))
                .andExpect(status().isNoContent());

        verify(cardService, times(1)).requestCardBlock(userId, cardId);
    }
}
