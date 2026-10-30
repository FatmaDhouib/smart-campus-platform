package com.smartcampus.auth.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    // Test-only secret: 32 bytes, Base64 encoded.
    private static final String TEST_SECRET =
            "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(TEST_SECRET, 86400000);
    }

    @Test
    void generateTokenShouldReturnValidToken() {

        String token = jwtService.generateToken("student@example.com");

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void extractEmailShouldReturnCorrectSubject() {

        String token = jwtService.generateToken("student@example.com");

        String email = jwtService.extractEmail(token);

        assertEquals("student@example.com", email);
    }

    @Test
    void generatedTokenShouldBeValid() {

        String token = jwtService.generateToken("student@example.com");

        assertTrue(
                jwtService.isTokenValid(token, "student@example.com")
        );
    }

    @Test
    void tokenShouldBeInvalidForDifferentEmail() {

        String token = jwtService.generateToken("student@example.com");

        assertFalse(
                jwtService.isTokenValid(token, "other@example.com")
        );
    }

    @Test
    void generatedTokenShouldHaveFutureExpiration() {

        String token = jwtService.generateToken("student@example.com");

        Date expiration = jwtService.extractExpiration(token);

        assertTrue(expiration.after(new Date()));
    }

    @Test
    void tokenShouldBeInvalidWhenSignatureIsModified() {

        String token = jwtService.generateToken("student@example.com");

        String[] parts = token.split("\\.");
        String modifiedToken = parts[0] + "." + parts[1] + ".invalid";

        assertFalse(
                jwtService.isTokenValid(
                        modifiedToken,
                        "student@example.com"
                )
        );
    }
}