package com.example.educationloan.config;



import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI educationLoanOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:" + serverPort);
        localServer.setDescription("🏠 Local Development Server");

        Server productionServer = new Server();
        productionServer.setUrl("https://api.educationloan.com");
        productionServer.setDescription("🚀 Production Server");

        Contact contact = new Contact();
        contact.setName("Education Loan API Support Team");
        contact.setEmail("TekeUpSkill.devteam@educationloan.com");
        contact.setUrl("https://educationloan.com/support");


        License license = new License()
                .name("Apache License 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html");


        Info info = new Info()
                .title("🎓 Education Loan Management System API")
                .version("v1.0.0")
                .description(buildApiDescription())
                .contact(contact)
                .license(license)
                .termsOfService("https://educationloan.com/terms");

        SecurityScheme bearerAuthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("Bearer Authentication")
                .description("🔐 JWT Bearer Token Authentication\n\n" +
                        " How to authenticate:\n" +
                        "1. 📝 Register a new account using `/api/v1/auth/register` endpoint\n" +
                        "2. 🔑 Login using `/api/v1/auth/login` endpoint with your credentials\n" +
                        "3. 📋 Copy the `accessToken` from the response\n" +
                        "4. 🔓 Click the 'Authorize' button (top right) and paste the token\n" +
                        "5. ✅ All subsequent API calls will automatically include your token\n\n" +
                        "Token Format: `Bearer <your-access-token>`\n\n" +
                        "Token Expiration: Access tokens expire in 150 minutes (9000000 ms)\n" +
                        "Refresh Token: Use `/api/v1/auth/refresh` to get a new access token");

        Components components = new Components().addSecuritySchemes("BearerAuth", bearerAuthScheme);
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

        List<Tag> tags = List.of(
                new Tag()
                        .name("🔐 Authentication")
                        .description("**User authentication and authorization endpoints**\n\n" +
                                "Manage user registration, login, token refresh, and session management. " +
                                "These endpoints handle JWT token generation and validation."),

                new Tag()
                        .name("👥 User Management")
                        .description("**Complete user lifecycle management**\n\n" +
                                "CRUD operations for user accounts including creation, retrieval, updates, and deletion. " +
                                "Manage user profiles, passwords, email verification, and account status (active/inactive)."),

                new Tag()
                        .name("🎭 Role Management")
                        .description("**Role-based access control (RBAC) operations**\n\n" +
                                "Manage roles and permissions across the system. Assign and remove roles from users, " +
                                "fetch role details, and query users by their assigned roles."),

                new Tag()
                        .name("🔗 User-Role Assignment")
                        .description("**User-Role relationship management**\n\n" +
                                "Direct management of user-role mappings. Assign roles to users, remove role assignments, " +
                                "and query all role assignments for users or roles."),

                new Tag()
                        .name("📊 Reports & Analytics")
                        .description("**Generate comprehensive reports in PDF format**\n\n" +
                                "Export user lists, user-role mappings, and authentication logs as PDF reports. " +
                                "Also provides JSON data endpoints for live previews and custom integrations.")
        );

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, productionServer))
                .components(components)
                .addSecurityItem(securityRequirement)
                .tags(tags);
    }

    private String buildApiDescription() {
        return """
                📖 Overview
                
                The Education Loan Management System API provides a comprehensive suite of RESTful endpoints 
                for managing user authentication, authorization, role-based access control, and administrative reports.
                
                🌟  🔒 Authentication Key Features
                ✅ Secure Authentication - JWT-based token authentication with refresh token support  
                ✅ Role-Based Access Control - Granular permissions with ADMIN, EMPLOYEE, and USER roles  
                ✅ User Management - Complete CRUD operations for user accounts  
                ✅ Audit Logging - Track all authentication events and user activities  
                ✅ PDF Reports - Generate comprehensive reports using Jasper Reports  
                ✅ Email Verification - Built-in email verification workflow  
                --------------------------------------------------------------------------------------------------------
                """;
    }
}