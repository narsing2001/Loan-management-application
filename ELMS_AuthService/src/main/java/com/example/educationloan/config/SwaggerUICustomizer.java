package com.example.educationloan.config;


import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerUICustomizer {

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            openApi.externalDocs(new ExternalDocumentation()
                    .description("📚 Full API Documentation & Guides")
                    .url("https://educationloan.com/api-docs"));
        };
    }
}