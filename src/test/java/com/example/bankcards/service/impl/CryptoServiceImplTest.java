package com.example.bankcards.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class CryptoServiceImplTest {

    private CryptoServiceImpl cryptoService;

    @BeforeEach
    void setUp() throws Exception {
        cryptoService = new CryptoServiceImpl();
        Field secretKeyString = CryptoServiceImpl.class.getDeclaredField("secretKeyString");
        secretKeyString.setAccessible(true);
        secretKeyString.set(cryptoService, "12345678901234567890123456789012");
        cryptoService.initKey();
    }

    @Test
    void encryptAndDecrypt_roundTripsValue() {
        String text = "Sensitive data";

        String cipher = cryptoService.encrypt(text);
        assertNotEquals(text, cipher);

        String decrypted = cryptoService.decrypt(cipher);
        assertEquals(text, decrypted);
    }

    @Test
    void initKey_throwsForShortKey() throws Exception {
        CryptoServiceImpl shortKeyService = new CryptoServiceImpl();
        Field secretKeyString = CryptoServiceImpl.class.getDeclaredField("secretKeyString");
        secretKeyString.setAccessible(true);
        secretKeyString.set(shortKeyService, "too-short-key");

        IllegalStateException exception = assertThrows(IllegalStateException.class, shortKeyService::initKey);
        assertEquals("Secret key must be 32 bytes for AES-256", exception.getMessage());
    }
}
