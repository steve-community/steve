/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "steve.gateway", name = "enabled", havingValue = "true")
public class TokenEncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int PBKDF2_ITERATIONS = 310000;
    private static final int KEY_LENGTH = 256;
    private static final String DEFAULT_SALT = "CHANGE-THIS-SALT-VALUE";

    private final SecretKey secretKey;
    private final SecureRandom secureRandom;
    private final byte[] salt;
    private final BCryptPasswordEncoder passwordEncoder;

    public TokenEncryptionService(
            @Value("${steve.gateway.encryption.key:}") String encryptionKey,
            @Value("${steve.gateway.encryption.salt:}") String encryptionSalt) {

        if (encryptionKey == null || encryptionKey.isBlank()) {
            throw new IllegalStateException(
                "Gateway encryption key not configured. Set steve.gateway.encryption.key property " +
                "or GATEWAY_ENCRYPTION_KEY environment variable to a secure random string (minimum 32 characters recommended)"
            );
        }

        if (encryptionSalt == null || encryptionSalt.isBlank() || DEFAULT_SALT.equals(encryptionSalt)) {
            throw new IllegalStateException(
                "Gateway encryption salt not configured or using default value. Set steve.gateway.encryption.salt property " +
                "or GATEWAY_ENCRYPTION_SALT environment variable to a unique random string for this instance (minimum 16 characters recommended)"
            );
        }

        this.salt = encryptionSalt.getBytes(StandardCharsets.UTF_8);

        this.secretKey = deriveKey(encryptionKey);
        this.secureRandom = new SecureRandom();
        this.passwordEncoder = new BCryptPasswordEncoder(12);
        log.info("TokenEncryptionService initialized with AES-256-GCM and BCrypt");
    }

    public String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isBlank()) {
            throw new IllegalArgumentException("Plaintext cannot be null or empty");
        }

        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            byteBuffer.put(iv);
            byteBuffer.put(ciphertext);

            return Base64.getEncoder().encodeToString(byteBuffer.array());

        } catch (Exception e) {
            log.error("Encryption failed", e);
            throw new EncryptionException("Failed to encrypt token", e);
        }
    }

    public String decrypt(String ciphertext) {
        if (ciphertext == null || ciphertext.isBlank()) {
            throw new IllegalArgumentException("Ciphertext cannot be null or empty");
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(ciphertext);

            ByteBuffer byteBuffer = ByteBuffer.wrap(decodedBytes);

            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);

            byte[] encryptedData = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedData);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] plaintext = cipher.doFinal(encryptedData);

            return new String(plaintext, StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error("Decryption failed", e);
            throw new EncryptionException("Failed to decrypt token", e);
        }
    }

    public String hashToken(String plainToken) {
        if (plainToken == null || plainToken.isBlank()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        return passwordEncoder.encode(plainToken);
    }

    public boolean verifyToken(String plainToken, String hashedToken) {
        if (plainToken == null || plainToken.isBlank() || hashedToken == null || hashedToken.isBlank()) {
            return false;
        }
        try {
            return passwordEncoder.matches(plainToken, hashedToken);
        } catch (Exception e) {
            log.error("Token verification failed", e);
            return false;
        }
    }

    private SecretKey deriveKey(String password) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(hash, "AES");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to derive encryption key", e);
        }
    }

    public static class EncryptionException extends RuntimeException {
        public EncryptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}