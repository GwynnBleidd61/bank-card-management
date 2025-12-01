package com.example.bankcards.service;

public interface CryptoService {

    String encrypt(String plainText);

    String decrypt(String cipherText);
}
