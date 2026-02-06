package com.samir.crm_order_system.testconfig;

import com.samir.crm_order_system.config.SecurityConfig;
import com.samir.crm_order_system.security.AppUserDetailsService;
import com.samir.crm_order_system.security.JwtAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Test
    void testPasswordEncoderBean() {
        JwtAuthFilter jwtAuthFilter = mock(JwtAuthFilter.class);
        AppUserDetailsService userDetailsService = mock(AppUserDetailsService.class);

        SecurityConfig config = new SecurityConfig(jwtAuthFilter, userDetailsService);

        PasswordEncoder encoder = config.passwordEncoder();

        assertNotNull(encoder);

        String raw = "12345";
        String encoded = encoder.encode(raw);

        assertTrue(encoder.matches(raw, encoded));
    }

}
