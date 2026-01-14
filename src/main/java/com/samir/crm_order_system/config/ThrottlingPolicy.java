package com.samir.crm_order_system.config;

import io.github.bucket4j.Bandwidth;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class ThrottlingPolicy {
    private final ThrottlingProperties props;

    public ThrottlingPolicy(ThrottlingProperties props) {
        this.props = props;
    }

    public Bandwidth ipBandwith() {
        int limit = props.getIp().getLimitPerMinute();
        Bandwidth bandwidth = Bandwidth.builder()
                .capacity(limit)
                .refillIntervally(limit, Duration.ofMinutes(1))
                .build();
        return bandwidth;

    }

    public Bandwidth userBandwith() {
        int limit = props.getUser().getLimitPerMinute();
        Bandwidth bandwidth = Bandwidth.builder()
                .capacity(limit)
                .refillIntervally(limit, Duration.ofMinutes(1))
                .build();
        return bandwidth;
    }

    public Bandwidth authBandwith() {
        int limit = props.getAuth().getLimitPerMinute();
        Bandwidth bandwidth = Bandwidth.builder()
                .capacity(limit)
                .refillIntervally(limit, Duration.ofMinutes(1))
                .build();
        return bandwidth;
    }

    public List<String> excludePaths() {
        return props.getExcludePaths();
    }
}
