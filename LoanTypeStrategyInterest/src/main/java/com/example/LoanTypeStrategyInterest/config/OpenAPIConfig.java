package com.example.LoanTypeStrategyInterest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${server.port:8083}")
    private String serverPort;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Bean
    public OpenAPI loanInterestCalculatorOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:" + serverPort + contextPath);
        localServer.setDescription("Local Development Server");

        Contact contact = new Contact();
        contact.setName("Loan Interest Calculator Team");
        contact.setEmail("support@example.com");

        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html");

        Info info = new Info()
                .title("Loan Interest Rate Calculator API")
                .version("1.0.0")
                .description("REST API for calculating loan interest rates using multiple algorithms:\n\n" +
                        "- **Strategy Pattern**: Loan-type-specific calculations (Education loan)\n" +
                        "- **Newton-Raphson Method**: Fast numerical convergence for standard scenarios\n" +
                        "- **Binary Search Method**: Stable algorithm for edge cases\n\n" +
                        "This API helps determine interest rates based on loan parameters including " +
                        "principal amount, EMI, and tenure.")
                .contact(contact)
                .license(license)
                .termsOfService("https://example.com/terms");
        return new OpenAPI().info(info).servers(List.of(localServer));
    }
}