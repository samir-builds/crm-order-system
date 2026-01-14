package com.samir.crm_order_system.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "throttling")
@Data
public class ThrottlingProperties {
    private Ip ip = new Ip();
    private User user = new User();
    private Auth auth = new Auth();
    private List<String> excludePaths = List.of();

    @Data
    public static class Ip {
        private int limitPerMinute = 100;
    }

    @Data
    public static class User {
        private int limitPerMinute = 60;
    }

    @Data
    public static class Auth {
        private int limitPerMinute = 10;
    }
}
