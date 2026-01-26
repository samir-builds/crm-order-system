package com.samir.crm_order_system.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        jwtUtil = new JwtUtil();

        Field secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "12345678901234567890123456789012");

        Field expField = JwtUtil.class.getDeclaredField("EXP_MS");
        expField.setAccessible(true);
        expField.set(jwtUtil, 3600000L);
    }
    private User mockUser(){
        return new User(
                "samir",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    @Test
    void testGenerateToken(){
        String token = jwtUtil.generateToken(mockUser());
        assertNotNull(token);
    }

    @Test
    void testExtractUsername(){
        String token = jwtUtil.generateToken(mockUser());
        String username = jwtUtil.extractUsername(token);
        assertEquals("samir", username);
    }

    @Test
    void testExtractRoles(){
        String token = jwtUtil.generateToken(mockUser());
        List<String> roles = jwtUtil.extractRoles(token);
        assertEquals(1, roles.size());
        assertEquals("ROLE_ADMIN", roles.get(0));
    }

    @Test
    void testExtractAllClaims(){
        String token = jwtUtil.generateToken(mockUser());
        Claims claims = jwtUtil.extractAllClaims(token);
        assertEquals("samir", claims.getSubject());
        assertEquals("ROLE_ADMIN", ((List<?>) claims.get("roles")).get(0));
    }

    @Test
    void testIsValid_ValidToken(){
        String token = jwtUtil.generateToken(mockUser());
        boolean result = jwtUtil.isValid(token, mockUser());
        assertTrue(result);
    }

    @Test
    void testIsValid_InvalidUser(){
        String token = jwtUtil.generateToken(mockUser());

        User wrongUser = new User(
                "not_samir",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        boolean valid = jwtUtil.isValid(token, wrongUser);
        assertFalse(valid);
    }

    @Test
    void testIsValid_ExpiredToken() throws Exception{
        Field expField =  JwtUtil.class.getDeclaredField("EXP_MS");
        expField.setAccessible(true);
        expField.set(jwtUtil, -1000L);

        String token = jwtUtil.generateToken(mockUser());
        boolean result = jwtUtil.isValid(token, mockUser());

        assertFalse(result);
    }

    @Test
    void testInvalidSignature() throws Exception{
        String token = jwtUtil.generateToken(mockUser());

        Field secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "not_samir");

        boolean result = jwtUtil.isValid(token, mockUser());

        assertFalse(result);

    }
}
