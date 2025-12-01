package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequestDto;
import com.example.bankcards.dto.CardStatusUpdateRequestDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.security.CustomUserDetailsService;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CardAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // бизнес-сервис
    @MockBean
    private CardService cardService;

    // security-зависимости, чтобы контекст поднялся
    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    // чтобы удовлетворить BankCardsApplication.initAdmin(UserService userService)
    @MockBean
    private UserService userService;

    private static final String BASE_URL = "/api/admin/cards";

    @Test
    void createCard_validRequest_returnsCreatedAndCallsService() throws Exception {
        CardCreateRequestDto requestDto = new CardCreateRequestDto(
                1L,
                "123456789012",          // 12 цифр
                "Test User",
                LocalDate.now().plusYears(1),
                new BigDecimal("100.00")
        );

        // тело ответа нам не важно, поэтому можем вернуть null
        when(cardService.createCard(any(CardCreateRequestDto.class))).thenReturn(null);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        ArgumentCaptor<CardCreateRequestDto> captor =
                ArgumentCaptor.forClass(CardCreateRequestDto.class);
        verify(cardService, times(1)).createCard(captor.capture());

        CardCreateRequestDto passed = captor.getValue();
        assertThat(passed.userId()).isEqualTo(1L);
        assertThat(passed.rawCardNumber()).isEqualTo("123456789012");
        assertThat(passed.cardholderName()).isEqualTo("Test User");
    }

    @Test
    void createCard_invalidRequest_returnsBadRequest() throws Exception {
        // нарушаем валидацию: короткий номер карты и expiryDate в прошлом
        CardCreateRequestDto invalid = new CardCreateRequestDto(
                1L,
                "123",                    // слишком мало цифр
                "Test User",
                LocalDate.now().minusDays(1),
                new BigDecimal("100.00")
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_validRequest_returnsOkAndCallsService() throws Exception {
        Long cardId = 10L;
        CardStatusUpdateRequestDto requestDto =
                new CardStatusUpdateRequestDto(CardStatus.BLOCKED);

        when(cardService.updateStatus(eq(cardId), any(CardStatusUpdateRequestDto.class)))
                .thenReturn(null);

        mockMvc.perform(patch(BASE_URL + "/" + cardId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(cardService, times(1))
                .updateStatus(eq(cardId), any(CardStatusUpdateRequestDto.class));
    }

    @Test
    void getAllCards_returnsOkAndCallsService() throws Exception {
        when(cardService.getAllCards(any(Pageable.class)))
                .thenReturn(Page.empty());

        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(cardService, times(1)).getAllCards(any(Pageable.class));
    }

    @Test
    void deleteCard_returnsNoContentAndCallsService() throws Exception {
        Long cardId = 5L;

        mockMvc.perform(delete(BASE_URL + "/" + cardId))
                .andExpect(status().isNoContent());

        verify(cardService, times(1)).deleteCard(cardId);
    }

    @Test
    void getUserCardsForAdmin_returnsOkAndCallsService() throws Exception {
        Long userId = 2L;
        when(cardService.getUserCards(eq(userId), any(Pageable.class)))
                .thenReturn(Page.empty());

        mockMvc.perform(get(BASE_URL + "/user")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(cardService, times(1))
                .getUserCards(eq(userId), any(Pageable.class));
    }
}
