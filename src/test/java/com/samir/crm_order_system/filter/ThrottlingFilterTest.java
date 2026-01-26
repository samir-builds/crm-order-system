package com.samir.crm_order_system.filter;

import com.samir.crm_order_system.config.ThrottlingPolicy;
import com.samir.crm_order_system.security.JwtUtil;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ThrottlingFilterTest {

    private ThrottlingFilter filter;
    private ThrottlingPolicy policy;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        policy = mock(ThrottlingPolicy.class);
        jwtUtil = mock(JwtUtil.class);

        when(policy.ipBandwidth()).thenReturn(Bandwidth.simple(1, java.time.Duration.ofMinutes(1)));
        when(policy.userBandwidth()).thenReturn(Bandwidth.simple(1, java.time.Duration.ofMinutes(1)));
        when(policy.adminBandwidth()).thenReturn(Bandwidth.simple(1, java.time.Duration.ofMinutes(1)));

        filter = new ThrottlingFilter(policy, jwtUtil);
    }

    @Test
    void testBypassH2Console() throws IOException, ServletException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/h2-console");

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(req, res, chain);

        verify(chain, times(1)).doFilter(req, res);
    }

    @Test
    void testInvalidJwtReturns401() throws IOException, ServletException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/products");
        req.addHeader("Authorization", "Bearer invalidtoken");

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtUtil.extractUsername("invalidtoken")).thenThrow(new RuntimeException("bad token"));

        filter.doFilter(req, res, chain);

        assertEquals(401, res.getStatus());
        assertEquals("Invalid JWT token", res.getContentAsString());
        verify(chain, never()).doFilter(req, res);
    }

    @Test
    void testRateLimitExceededReturns429() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/products");
        req.setRemoteAddr("127.0.0.1");

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        Bucket bucket = mock(Bucket.class);
        when(bucket.tryConsume(1)).thenReturn(false);

        Field cacheField = ThrottlingFilter.class.getDeclaredField("cache");
        cacheField.setAccessible(true);

        ConcurrentHashMap<String, Bucket> cache =
                (ConcurrentHashMap<String, Bucket>) cacheField.get(filter);

        cache.put("IP: 127.0.0.1", bucket);

        filter.doFilter(req, res, chain);

        assertEquals(429, res.getStatus());
        assertEquals("Rate limit exceeded", res.getContentAsString());
        verify(chain, never()).doFilter(req, res);
    }

}
