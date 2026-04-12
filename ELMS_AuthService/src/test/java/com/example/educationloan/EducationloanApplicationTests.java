package com.example.educationloan;

import com.example.educationloan.controller.AuthController;
import com.example.educationloan.dto.AuthDTO;
import com.example.educationloan.dto.LoginDTO;
import com.example.educationloan.dto.RefreshRequestDTO;
import com.example.educationloan.dto.RegisterDTO;
import com.example.educationloan.repository.UserRepository;
import com.example.educationloan.repository.RoleRepository;
import com.example.educationloan.security.jwt.AuthService;
import com.example.educationloan.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Education Loan Application Integration Tests")
class EducationloanApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;


    @Nested
    @DisplayName("Application Context Tests")
    class ContextTests {

        @Test
        @DisplayName("Spring application context loads successfully")
        void contextLoads() {
            assertThat(applicationContext).isNotNull();
        }

        @Test
        @DisplayName("Application has beans loaded")
        void applicationHasBeans() {
            int beanCount = applicationContext.getBeanDefinitionCount();
            assertThat(beanCount).isGreaterThan(0);
            System.out.println("Total beans loaded: " + beanCount);
        }
    }


    @Nested
    @DisplayName("Controller Beans Tests")
    class ControllerBeanTests {

        @Test
        @DisplayName("AuthController bean is loaded")
        void authControllerIsLoaded() {
            assertThat(applicationContext.containsBean("authController")).isTrue();
            AuthController controller = applicationContext.getBean(AuthController.class);
            assertThat(controller).isNotNull();
        }

        @Test
        @DisplayName("All controller beans are present")
        void allControllersAreLoaded() {
            // Add other controllers as needed
            assertThat(applicationContext.getBean("authController")).isNotNull();
            // Example: assertThat(applicationContext.getBean("userController")).isNotNull();
            // Example: assertThat(applicationContext.getBean("roleController")).isNotNull();
        }
    }


    @Nested
    @DisplayName("Service Beans Tests")
    class ServiceBeanTests {

        @Test
        @DisplayName("AuthService bean is loaded")
        void authServiceIsLoaded() {
            AuthService service = applicationContext.getBean(AuthService.class);
            assertThat(service).isNotNull();
        }

        @Test
        @DisplayName("UserService bean is loaded")
        void userServiceIsLoaded() {
            assertThat(applicationContext.containsBean("userService")).isTrue();
            UserService service = applicationContext.getBean(UserService.class);
            assertThat(service).isNotNull();
        }

        @Test
        @DisplayName("All service beans are present")
        void allServicesAreLoaded() {
            assertThat(applicationContext.getBean(AuthService.class)).isNotNull();
            assertThat(applicationContext.getBean(UserService.class)).isNotNull();

        }
    }


    @Nested
    @DisplayName("Repository Beans Tests")
    class RepositoryBeanTests {

        @Test
        @DisplayName("UserRepository bean is loaded")
        void userRepositoryIsLoaded() {
            UserRepository repository = applicationContext.getBean(UserRepository.class);
            assertThat(repository).isNotNull();
        }

        @Test
        @DisplayName("RoleRepository bean is loaded")
        void roleRepositoryIsLoaded() {
            RoleRepository repository = applicationContext.getBean(RoleRepository.class);
            assertThat(repository).isNotNull();
        }

        @Test
        @DisplayName("All repository beans are present")
        void allRepositoriesAreLoaded() {
            assertThat(applicationContext.getBean(UserRepository.class)).isNotNull();
            assertThat(applicationContext.getBean(RoleRepository.class)).isNotNull();

        }
    }

    // ==================== CONFIGURATION TESTS ====================
    @Nested
    @DisplayName("Configuration Tests")
    class ConfigurationTests {

        @Test
        @DisplayName("ObjectMapper bean is configured")
        void objectMapperIsConfigured() {
            ObjectMapper mapper = applicationContext.getBean(ObjectMapper.class);
            assertThat(mapper).isNotNull();
        }

        @Test
        @DisplayName("No circular dependencies detected")
        void noCircularDependencies() {
            assertThat(applicationContext).isNotNull();
            String[] beanNames = applicationContext.getBeanDefinitionNames();
            assertThat(beanNames).isNotEmpty();
        }
    }


    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @Transactional // Rollback after each test
    @DisplayName("End-to-End Integration Tests")
    class EndToEndIntegrationTests {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("Complete user registration flow works end-to-end")
        void completeRegistrationFlow() throws Exception {
            RegisterDTO registerRequest = new RegisterDTO();
            registerRequest.setFirstName("Integration");
            registerRequest.setLastName("Test");
            registerRequest.setEmail("integration@test.com");
            registerRequest.setPassword("SecurePass@123");

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("User registered successfully"))
                    .andExpect(jsonPath("$.data.username").exists())
                    .andExpect(jsonPath("$.data.accessToken").exists())
                    .andExpect(jsonPath("$.data.refreshToken").exists());
        }

        @Test
        @DisplayName("Complete login flow works end-to-end")
        void completeLoginFlow() throws Exception {

            RegisterDTO registerRequest = new RegisterDTO();
            registerRequest.setFirstName("Login");
            registerRequest.setLastName("Test");
            registerRequest.setEmail("logintest@test.com");
            registerRequest.setPassword("SecurePass@123");

            mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerRequest)));


            LoginDTO loginRequest = new LoginDTO();
            loginRequest.setUsernameOrEmail("logintest@test.com");
            loginRequest.setPassword("SecurePass@123");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Login successful"))
                    .andExpect(jsonPath("$.data.username").exists())
                    .andExpect(jsonPath("$.data.accessToken").exists())
                    .andExpect(jsonPath("$.data.refreshToken").exists());
        }

        @Test
        @DisplayName("Complete token refresh flow works end-to-end")
        void completeRefreshFlow() throws Exception {

            RegisterDTO registerRequest = new RegisterDTO();
            registerRequest.setFirstName("Refresh");
            registerRequest.setLastName("Test");
            registerRequest.setEmail("refreshtest@test.com");
            registerRequest.setPassword("SecurePass@123");

            String registerResponse = mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Extract refresh token from response
            AuthDTO authResponse = objectMapper.readValue(
                    objectMapper.readTree(registerResponse).get("data").toString(),
                    AuthDTO.class
            );
            String refreshToken = authResponse.getRefreshToken();

            // Use refresh token to get new access token
            RefreshRequestDTO refreshRequest = new RefreshRequestDTO();
            refreshRequest.setRefreshToken(refreshToken);

            mockMvc.perform(post("/api/v1/auth/refresh1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(refreshRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                    .andExpect(jsonPath("$.data.accessToken").exists());
        }

        @Test
        @DisplayName("Duplicate registration is rejected")
        void duplicateRegistrationRejected() throws Exception {
            RegisterDTO registerRequest = new RegisterDTO();
            registerRequest.setFirstName("Duplicate");
            registerRequest.setLastName("Test");
            registerRequest.setEmail("duplicate@test.com");
            registerRequest.setPassword("SecurePass@123");

            // First registration
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isOk());

            // Second registration with same email should fail
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("Invalid credentials are rejected")
        void invalidCredentialsRejected() throws Exception {
            LoginDTO loginRequest = new LoginDTO();
            loginRequest.setUsernameOrEmail("nonexistent@user.com");
            loginRequest.setPassword("WrongPassword");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())  // 401 - More specific
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Invalid username or password"));
        }
    }

    @Nested
    @DisplayName("Application Health Tests")
    class HealthTests {

        @Test
        @DisplayName("Database connectivity is working")
        void databaseConnectivityWorks() {

            UserRepository userRepository = applicationContext.getBean(UserRepository.class);
            assertThat(userRepository).isNotNull();


            long count = userRepository.count();
            assertThat(count).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("JPA/Hibernate is properly configured")
        void jpaIsConfigured() {
            UserRepository userRepository = applicationContext.getBean(UserRepository.class);
            RoleRepository roleRepository = applicationContext.getBean(RoleRepository.class);

            assertThat(userRepository).isNotNull();
            assertThat(roleRepository).isNotNull();
        }
    }


    @Nested
    @DisplayName("Profile and Environment Tests")
    class ProfileTests {

        @Autowired
        private ApplicationContext context;

        @Test
        @DisplayName("Test profile is active")
        void testProfileIsActive() {
            String[] activeProfiles = context.getEnvironment().getActiveProfiles();
            assertThat(activeProfiles).contains("test");
        }

        @Test
        @DisplayName("Required properties are loaded")
        void requiredPropertiesAreLoaded() {
            assertThat(context.getEnvironment().getProperty("spring.application.name")).isNotNull();
        }
    }
}