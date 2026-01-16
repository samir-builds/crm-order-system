package com.samir.crm_order_system.config;

import io.github.bucket4j.Bandwidth;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ThrottlingPolicy {

    public Bandwidth ipBandwidth() {
        return Bandwidth.builder()
                .capacity(100)
                .refillIntervally(100, Duration.ofMinutes(1))
                .build();
    }

    public Bandwidth userBandwidth() {
        return Bandwidth.builder()
                .capacity(50)
                .refillIntervally(50, Duration.ofMinutes(1))
                .build();
    }

    public Bandwidth adminBandwidth() {
        return Bandwidth.builder()
                .capacity(200)
                .refillIntervally(200, Duration.ofMinutes(1))
                .build();
    }
}
