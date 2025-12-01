package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferResponseDto;
import com.example.bankcards.exception.GlobalExceptionHandler;
import com.example.bankcards.security.CustomUserDetailsService;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserTransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private UserService userService;

    private static final String BASE_URL = "/api/user/transactions";

    @Test
    void getUserTransactions_returnsOkAndCallsService() throws Exception {
        Long userId = 7L;
        Page<TransferResponseDto> emptyPage = new PageImpl<>(List.of());
        when(transferService.getUserTransactions(eq(userId), any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get(BASE_URL)
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(transferService, times(1)).getUserTransactions(eq(userId), any(Pageable.class));
    }
}
