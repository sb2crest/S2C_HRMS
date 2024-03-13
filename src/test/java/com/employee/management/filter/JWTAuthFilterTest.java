package com.employee.management.filter;

import com.employee.management.exception.CompanyException;
import com.employee.management.service.JWTService;
import com.employee.management.service.UserInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JWTAuthFilterTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private UserInfoService userInfoService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JWTAuthFilter jwtAuthFilter;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testValidToken() throws IOException, ServletException {
        // Given
        String token = "validToken";
        String empId = "testUser";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(empId);
        when(userInfoService.loadUserByUsername(empId)).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(true);

        // When
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtService).extractUsername(token);
        verify(userInfoService).loadUserByUsername(empId);
        verify(jwtService).validateToken(token, userDetails);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken);
    }

    @Test
    public void testInvalidToken() throws IOException, ServletException {
        // Given
        String token = "invalidToken";
        String empId = "testUser";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(empId);
        when(userInfoService.loadUserByUsername(empId)).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(false);

        // When & Then
        assertThrows(CompanyException.class, () -> jwtAuthFilter.doFilterInternal(request, response, filterChain));
    }
}
