package com.example.bankcards.service.impl;

import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.dto.AuthResponse;
import com.example.bankcards.security.CustomUserDetailsService;
import com.example.bankcards.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    private AuthenticationManager authenticationManager;
    private CustomUserDetailsService userDetailsService;
    private JwtService jwtService;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        userDetailsService = mock(CustomUserDetailsService.class);
        jwtService = mock(JwtService.class);

        authService = new AuthServiceImpl(authenticationManager, userDetailsService, jwtService);
    }

    @Test
    void login_authenticatesAndGeneratesToken() {
        AuthRequest request = new AuthRequest("user@test", "pwd");
        UserDetails userDetails = mock(UserDetails.class);

        when(userDetailsService.loadUserByUsername("user@test")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("token123");

        AuthResponse response = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername("user@test");
        verify(jwtService).generateToken(userDetails);

        assertEquals("token123", response.token());
    }
}
