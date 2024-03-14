package com.employee.management.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JWTServiceTest {

    private JWTService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JWTService();
        userDetails = User.withUsername("testUser").password("password").roles("USER").build();
    }

    @Test
    void testExtractUsername() {
        String token = jwtService.generateToken("testUser");
        String username = jwtService.extractUsername(token);
        assertEquals("testUser", username);
    }

    @Test
    void testExtractExpiration() {
        String token = jwtService.generateToken("testUser");
        Date expiration = jwtService.extractExpiration(token);
        assertNotNull(expiration);
    }

    @Test
    void testValidateToken() {
        String token = jwtService.generateToken("testUser");
        boolean isValid = jwtService.validateToken(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_returnInvalidUser() {
        String token = jwtService.generateToken("testUse");
        boolean isValid = (jwtService.validateToken(token, userDetails));

        assertFalse(isValid);
    }
    @Test
    void testValidateToken_withExpiredTime() {
        String token = jwtService.generateToken("testUse");
        String username = jwtService.extractUsername(token);

        boolean valid = username.equals(userDetails.getUsername()) && jwtService.isTokenExpired(token);
        boolean isValid = (jwtService.validateToken(token, userDetails));

        assertFalse(isValid);
        assertFalse(valid);
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken("testUser");
        assertNotNull(token);
    }

}
