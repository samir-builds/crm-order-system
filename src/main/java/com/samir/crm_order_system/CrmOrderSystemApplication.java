package com.samir.crm_order_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CrmOrderSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrmOrderSystemApplication.class, args);
	}
}
