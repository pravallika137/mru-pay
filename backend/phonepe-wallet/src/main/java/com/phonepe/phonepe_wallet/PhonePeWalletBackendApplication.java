package com.phonepe.phonepe_wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PhonePeWalletBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhonePeWalletBackendApplication.class, args);
		System.out.println("\n========================================");
		System.out.println("PhonePe Wallet Backend Started!");
		System.out.println("API Base URL: http://localhost:8080/api");
		System.out.println("Swagger UI: http://localhost:8080/swagger-ui.html");
		System.out.println("API Docs: http://localhost:8080/v3/api-docs");
		System.out.println("Database: MySQL (phonepay_db)");
		System.out.println("========================================\n");
	}
}