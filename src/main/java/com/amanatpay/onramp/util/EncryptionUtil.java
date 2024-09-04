package com.amanatpay.onramp.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

    private final TextEncryptor encryptor;

    public EncryptionUtil(@Value("${encryption.password}") String password, @Value("${encryption.salt}") String salt) {
        this.encryptor = Encryptors.text(password, salt);
    }

    public String encrypt(String data) {
        return encryptor.encrypt(data);
    }

    public String decrypt(String encryptedData) {
        return encryptor.decrypt(encryptedData);
    }
}