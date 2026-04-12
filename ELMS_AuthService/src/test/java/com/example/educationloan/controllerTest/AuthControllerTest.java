package com.example.educationloan.controllerTest;

import com.example.educationloan.dto.RefreshRequestDTO;
import com.example.educationloan.exception.BadCredentialsException;
import com.example.educationloan.exception.EmailAlreadyExistsException;
import com.example.educationloan.exception.TokenExpiredException;
import com.example.educationloan.globalexception.GlobalExceptionHandler;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.example.educationloan.controller.AuthController;
import com.example.educationloan.dto.AuthDTO;
import com.example.educationloan.dto.LoginDTO;
import com.example.educationloan.dto.RegisterDTO;
import com.example.educationloan.security.jwt.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.*;
import java.time.LocalDateTime;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("authentication controller test cases using mockito")
public class AuthControllerTest {

    @Mock
    AuthService authService;

    @InjectMocks
    AuthController authController;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    private AuthDTO authDTO;
    private MockHttpServletRequestBuilder postJson(String uri) {
        return post(uri).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder putJson(String uri) {
        return put(uri).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder patchJson(String uri) {
        return patch(uri).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder deleteJson(String uri) {
        return delete(uri).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    @BeforeEach
    void init() {

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        ;
        authDTO = new AuthDTO();
        authDTO.setAccessToken("access-token-xyz");
        authDTO.setRefreshToken("refresh-token-xyz");
        authDTO.setUsername("narsing.patil");
        authDTO.setTokenType("Bearer");
        authDTO.setAccessTokenExpiresAt(LocalDateTime.now().plusMinutes(15));
        authDTO.setRefreshTokenExpiresAt(LocalDateTime.now().plusDays(7));
        authDTO.setAccessTokenExpiresInSeconds(900L);
        authDTO.setRefreshTokenExpiresInSeconds(604800L);
    }


    private RegisterDTO validateRegisterDTO() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setFirstName("narsing");
        registerDTO.setLastName("patil");
        registerDTO.setEmail("abc@gmail.com");
        registerDTO.setPassword("12345678");
        return registerDTO;
    }

    private LoginDTO validLoginDTO() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsernameOrEmail("abc@gmail.com");
        loginDTO.setPassword("12345678");
        return loginDTO;
    }

    //test-cases for endpoint: /api/v1/auth/register-----=---------------------
    @Nested
    @DisplayName("POST/registeruser")
    class RegisterTests {

        @Test
        @DisplayName("registration success 200 status with token")
        void RegisterSuccess() throws Exception {
            when(authService.register(any(RegisterDTO.class))).thenReturn(authDTO);
            mockMvc.perform(postJson("/api/v1/auth/register")
                            .content(objectMapper.writeValueAsString(validateRegisterDTO())))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("User registered successfully"))
                    .andExpect(jsonPath("$.data.username").value("narsing.patil"))
                    .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.data.accessToken").value("access-token-xyz"));

            verify(authService, times(1)).register(any(RegisterDTO.class));
        }

        @Test
        @DisplayName("Registration failure - email already exists returns 409 Conflict")
        void registrationFailedEmailAlreadyExists() throws Exception {
            when(authService.register(any(RegisterDTO.class)))
                    .thenThrow(new EmailAlreadyExistsException("Email already exists or in use"));

            mockMvc.perform(postJson("/api/v1/auth/register")
                            .content(objectMapper.writeValueAsString(validateRegisterDTO())))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Email already exists or in use"));

            verify(authService, times(1)).register(any(RegisterDTO.class));
        }

        @Test
        @DisplayName("null body returns 400 i.e bad requests")
        void registerEmpty() throws Exception {
            mockMvc.perform(postJson("/api/v1/auth/register")
                    .content("{}"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    //.andExpect(jsonPath("$.message").value("Validation failed"));
                    //.andExpect(jsonPath("$.message").exists());
                    .andExpect(jsonPath("$.message").isNotEmpty());

            verify(authService, never()).register(any());
        }

        @Test
        @DisplayName("missing firstName returns 400")
        void registerMissingFirstName() throws Exception {
            RegisterDTO regdto = new RegisterDTO();
            regdto.setLastName("patil");
            regdto.setEmail("abc@gmail.com");
            regdto.setPassword("12345678");
            mockMvc.perform(postJson("/api/v1/auth/register")
                            .content(objectMapper.writeValueAsString(regdto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(authService, never()).register(any());
        }

        @Test
        @DisplayName("missing lastName returns 400")
        void registerMissingLastName() throws Exception {
            RegisterDTO dto = new RegisterDTO();
            dto.setFirstName("narsing");
            dto.setEmail("abc@gmail.com");
            dto.setPassword("12345678");
            mockMvc.perform(postJson("/api/v1/auth/register")
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
            verify(authService, never()).register(any());
        }

        @Test
        @DisplayName("missing email returns 400")
        void registerMissingEmail() throws Exception {
            RegisterDTO dto = new RegisterDTO();
            dto.setFirstName("narsing");
            dto.setLastName("patil");
            dto.setPassword("12345678");

            mockMvc.perform(postJson("/api/v1/auth/register")
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(authService, never()).register(any());
        }

        @Test
        @DisplayName("missing password returns 400")
        void registerMissingPassword() throws Exception {
            RegisterDTO dto = new RegisterDTO();
            dto.setFirstName("narsing");
            dto.setLastName("patil");
            dto.setEmail("abc@gmail.com");

            mockMvc.perform(postJson("/api/v1/auth/register")
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(authService, never()).register(any());
        }

        @Test
        @DisplayName("invalid email format returns 400")
        void registerInvalidEmail() throws Exception {
            RegisterDTO dto = new RegisterDTO();
            dto.setFirstName("narsing");
            dto.setLastName("patil");
            dto.setEmail("invalid-email");
            dto.setPassword("12345678");

            mockMvc.perform(postJson("/api/v1/auth/register")
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(authService, never()).register(any());
        }

        @Test
        @DisplayName("password too short returns 400")
        void registerWeakPassword() throws Exception {
            RegisterDTO dto = new RegisterDTO();
            dto.setFirstName("narsing");
            dto.setLastName("patil");
            dto.setEmail("abc@gmail.com");
            dto.setPassword("123"); // too short

            mockMvc.perform(postJson("/api/v1/auth/register")
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
            verify(authService, never()).register(any());
        }

        @Test
        @DisplayName("empty strings in all fields returns 400")
        void registerEmptyStrings() throws Exception {
            RegisterDTO dto = new RegisterDTO();
            dto.setFirstName("");
            dto.setLastName("");
            dto.setEmail("");
            dto.setPassword("");

            mockMvc.perform(postJson("/api/v1/auth/register")
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(authService, never()).register(any());
        }

    }

    //test-cases for endpoint: /api/v1/auth/login-----------
    @Nested
    @DisplayName("POST /login_user")
    class LoginTests {

        @Test
        @DisplayName("success login returns 200 with tokens")
        void loginSuccess() throws Exception {
            when(authService.login(anyString(), anyString())).thenReturn(authDTO);

            mockMvc.perform(postJson("/api/v1/auth/login")
                            .content(objectMapper.writeValueAsString(validLoginDTO())))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Login successful"))
                    .andExpect(jsonPath("$.data.accessToken").value("access-token-xyz"));

            verify(authService).login("abc@gmail.com", "12345678");
        }


        @Test
        @DisplayName("Bad credentials returns 401 Unauthorized")
        void loginFailed() throws Exception {
            when(authService.login(anyString(), anyString()))
                    .thenThrow(new BadCredentialsException("Invalid username or password"));

            mockMvc.perform(postJson("/api/v1/auth/login")
                            .content(objectMapper.writeValueAsString(validLoginDTO())))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Invalid username or password"));
        }

        @Test
        @DisplayName("null or empty body returns 400")
        void loginEmptyBody() throws Exception {
            mockMvc.perform(postJson("/api/v1/auth/login")
                            .content("{}"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
            verify(authService, never()).login(any(), any());
        }
        @Test
        @DisplayName("missing username returns 400")
        void loginMissingUsername() throws Exception {
            mockMvc.perform(postJson("/api/v1/auth/login")
                            .content("{\"password\":\"test123\"}"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
            verify(authService, never()).login(any(), any());
        }

        @Test
        @DisplayName("missing password returns 400")
        void loginMissingPassword() throws Exception {
            mockMvc.perform(postJson("/api/v1/auth/login")
                            .content("{\"username\":\"testuser\"}"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
            verify(authService, never()).login(any(), any());
        }

        @Test
        @DisplayName("empty strings return 400")
        void loginEmptyStrings() throws Exception {
            mockMvc.perform(postJson("/api/v1/auth/login")
                            .content("{\"username\":\"\",\"password\":\"\"}"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
            verify(authService, never()).login(any(), any());
        }
        @Test
        @DisplayName("whitespace-only username returns 400")
        void loginWhitespaceUsername() throws Exception {
            LoginDTO dto = new LoginDTO();
            dto.setUsernameOrEmail("   ");
            dto.setPassword("12345678");

            mockMvc.perform(postJson("/api/v1/auth/login")
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(authService, never()).login(any(), any());
        }

        @Test
        @DisplayName("whitespace-only password returns 400")
        void loginWhitespacePassword() throws Exception {
            LoginDTO dto = new LoginDTO();
            dto.setUsernameOrEmail("abc@gmail.com");
            dto.setPassword("   ");

            mockMvc.perform(postJson("/api/v1/auth/login")
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(authService, never()).login(any(), any());
        }
    }

    //test-cases for endpoint: /api/v1/auth/refresh----------------
    @Nested
    @DisplayName("POST /refresh (query param)")
    class RefreshQueryParamTests {

        @Test
        @DisplayName("Happy Path – valid token param returns 200")
        void refreshTokenSuccess() throws Exception {
            when(authService.refresh("valid-token")).thenReturn(authDTO);

            mockMvc.perform(post("/api/v1/auth/refresh").param("refreshToken", "valid-token"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Token refreshed successfully"));

            verify(authService).refresh("valid-token");
        }

        @Test
        @DisplayName("Invalid token returns 401 Unauthorized")
        void refreshTokenInvalid() throws Exception {
            when(authService.refresh(anyString()))
                    .thenThrow(new TokenExpiredException("Invalid refresh token"));

            mockMvc.perform(post("/api/v1/auth/refresh")
                            .param("refreshToken", "invalid-token"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Invalid refresh token"));
        }

        @Test
        @DisplayName("Edge Case – missing param returns 400")
        void refreshTokenMissing() throws Exception {
            mockMvc.perform(post("/api/v1/auth/refresh"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
            verify(authService, never()).refresh(any());
        }


        @Test
        @DisplayName("empty token param returns 400")
        void refreshTokenEmpty() throws Exception {
            mockMvc.perform(post("/api/v1/auth/refresh")
                            .param("refreshToken", ""))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Refresh token cannot be blank"));

            verify(authService, never()).refresh(any());
        }

        @Test
        @DisplayName("whitespace-only token returns 400")
        void refreshTokenWhitespace() throws Exception {
            mockMvc.perform(post("/api/v1/auth/refresh")
                            .param("refreshToken", "   "))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(authService, never()).refresh(any());
        }

        @Test
        @DisplayName("null token explicitly passed returns 400")
        void refreshTokenExplicitNull() throws Exception {
            mockMvc.perform(post("/api/v1/auth/refresh")
                            .param("refreshToken", (String) null))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(authService, never()).refresh(any());
        }
    }

    //test-cases for endpoint: /api/v1/auth/refresh1------------------
    @Nested
    @DisplayName("POST /refresh1 (request body)")
    class RefreshBodyTests {

        @Test
        @DisplayName("valid token body returns 200")
        void refresh1Success() throws Exception {
            RefreshRequestDTO body = new RefreshRequestDTO();
            body.setRefreshToken("valid-token");

            when(authService.refresh("valid-token")).thenReturn(authDTO);

            mockMvc.perform(postJson("/api/v1/auth/refresh1")
                            .content(objectMapper.writeValueAsString(body)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.accessToken").value("access-token-xyz"));
        }

        @Test
        @DisplayName("Expired token returns 401 Unauthorized")
        void refresh1TokenExpired() throws Exception {
            RefreshRequestDTO body = new RefreshRequestDTO();
            body.setRefreshToken("expired-token");

            when(authService.refresh("expired-token"))
                    .thenThrow(new TokenExpiredException("Refresh token has expired"));

            mockMvc.perform(postJson("/api/v1/auth/refresh1")
                            .content(objectMapper.writeValueAsString(body)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Refresh token has expired"));
        }

        @Test
        @DisplayName("Edge Case – null refreshToken returns 400 without calling service")
        void refresh1TokenMissing() throws Exception {
            RefreshRequestDTO body = new RefreshRequestDTO();
            body.setRefreshToken(null);

            // No stubbing needed - controller should reject before calling service

            mockMvc.perform(postJson("/api/v1/auth/refresh1")
                            .content(objectMapper.writeValueAsString(body)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").exists());

            // Verify service was never called
            verify(authService, never()).refresh(any());
        }

        @Test
        @DisplayName("empty refreshToken returns 400")
        void refresh1TokenEmpty() throws Exception {
            RefreshRequestDTO body = new RefreshRequestDTO();
            body.setRefreshToken("");

            mockMvc.perform(postJson("/api/v1/auth/refresh1")
                            .content(objectMapper.writeValueAsString(body)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
            verify(authService, never()).refresh(any());
        }

        @Test
        @DisplayName("whitespace-only token returns 400")
        void refresh1TokenWhitespace() throws Exception {
            RefreshRequestDTO body = new RefreshRequestDTO();
            body.setRefreshToken("   ");

            mockMvc.perform(postJson("/api/v1/auth/refresh1")
                            .content(objectMapper.writeValueAsString(body)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
            verify(authService, never()).refresh(any());
        }

        @Test
        @DisplayName("empty JSON body returns 400")
        void refresh1EmptyBody() throws Exception {
            mockMvc.perform(postJson("/api/v1/auth/refresh1")
                            .content("{}"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(authService, never()).refresh(any());
        }
    }
}







