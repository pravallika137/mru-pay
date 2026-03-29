package com.phonepe.phonepe_wallet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI phonePayWalletAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local Development Server");

        Contact contact = new Contact();
        contact.setName("PhonePe Wallet Team");
        contact.setEmail("support@phonepewallet.com");

        License license = new License()
                .name("Educational Use")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("PhonePe Wallet Backend API")
                .version("1.0.0")
                .description("Mini UPI Wallet Simulation for B.Tech Students - " +
                        "This API provides endpoints for user management, wallet operations, " +
                        "and UPI transactions including core features like send/receive money, " +
                        "balance management, and transaction history.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}