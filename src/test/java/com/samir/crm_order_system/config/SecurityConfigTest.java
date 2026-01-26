package com.samir.crm_order_system.config;

import com.samir.crm_order_system.security.JwtAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Test
    void testPasswordEncoderBean() {
        JwtAuthFilter jwtAuthFilter = mock(JwtAuthFilter.class);

        SecurityConfig config = new SecurityConfig(jwtAuthFilter);

        PasswordEncoder encoder = config.passwordEncoder();

        assertNotNull(encoder);
        assertTrue(encoder.matches("12345", encoder.encode("12345")));
    }
}
