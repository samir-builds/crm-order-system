package com.samir.crm_order_system.filter;

import com.samir.crm_order_system.config.ThrottlingPolicy;
import com.samir.crm_order_system.security.JwtUtil;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ThrottlingFilter implements Filter {

    private final ConcurrentHashMap<String, Bucket> cache = new ConcurrentHashMap<>();
    private final ThrottlingPolicy policy;
    private final JwtUtil jwtUtil;

    public ThrottlingFilter(ThrottlingPolicy policy, JwtUtil jwtUtil) {
        this.policy = policy;
        this.jwtUtil = jwtUtil;
    }

    private Bucket resolveBucket(String key, Bandwidth limit) {
        return cache.computeIfAbsent(key, k ->
                Bucket.builder()
                        .addLimit(limit)
                        .build()
        );
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) request;
        String path = httpReq.getRequestURI();

        if (path.startsWith("/auth/")) {
            chain.doFilter(request, response);
            return;
        }

        if (path.startsWith("/h2-console")) {
            chain.doFilter(request, response);
            return;
        }

        String ip = httpReq.getRemoteAddr();
        Bucket ipBucket = resolveBucket("IP: " + ip, policy.ipBandwidth());

        String authHeader = httpReq.getHeader("Authorization");
        String userId = null;
        String role = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                userId = jwtUtil.extractUsername(token);
                List<String> roles = jwtUtil.extractRoles(token);
                role = roles.isEmpty() ? null : roles.get(0);

            } catch (Exception e) {
                ((HttpServletResponse) response).setStatus(401);
                response.getWriter().write("Invalid JWT token");
                return;
            }
        }

        Bucket userBucket = null;

        if (userId != null) {
            if ("ROLE_ADMIN".equals(role)) {
                userBucket = resolveBucket("ADMIN: " + userId, policy.adminBandwidth());
            } else {
                userBucket = resolveBucket("USER: " + userId, policy.userBandwidth());
            }
        }

        boolean allowed = ipBucket.tryConsume(1);

        if (userBucket != null) {
            allowed = allowed && userBucket.tryConsume(1);
        }

        if (allowed) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).setStatus(429);
            response.getWriter().write("Rate limit exceeded");
        }
    }
}
