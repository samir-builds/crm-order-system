package com.samir.crm_order_system.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CRM Order System API")
                        .version("1.0")
                        .description("Samir'in CRM sistemi üçün Swagger sənədləşməsi"));
    }
}
