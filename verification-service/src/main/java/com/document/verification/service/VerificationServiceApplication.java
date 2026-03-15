package com.document.verification.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VerificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VerificationServiceApplication.class, args);
	}
}
