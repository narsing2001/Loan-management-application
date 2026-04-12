package com.example.educationloan.controller;

import com.example.educationloan.dto.RefreshRequestDTO;
import com.example.educationloan.security.jwt.AuthService;
import com.example.educationloan.dto.AuthDTO;
import com.example.educationloan.dto.LoginDTO;
import com.example.educationloan.dto.RegisterDTO;
import com.example.educationloan.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Validated
@Tag(name = "🔐 Authentication", description = "User authentication and authorization endpoints")
public class AuthController {
    private final AuthService authService;
    private static final String ROW = "+---------------------------+----------------------------------+";
    private void logAuthTable(String operation, AuthDTO r) {
        log.info("Operation   : {}", operation);
        log.info(ROW);
        log.info(String.format("| %-25s | %-32s |", "Field", "Value"));
        log.info(ROW);
        log.info(String.format("| %-25s | %-32s |", "Username",                r.getUsername()));
        log.info(String.format("| %-25s | %-32s |", "Token Type",              r.getTokenType()));
        log.info(String.format("| %-25s | %-32s |", "Access Token",            "abstract"));
        log.info(String.format("| %-25s | %-32s |", "Refresh Token",           "abstract"));
        log.info(String.format("| %-25s | %-32s |", "Access  Expires At",      r.getAccessTokenExpiresAt()));
        log.info(String.format("| %-25s | %-32s |", "Access  Expires In",      r.getAccessTokenExpiresInSeconds()  + " sec"));
        log.info(String.format("| %-25s | %-32s |", "Refresh Expires At",      r.getRefreshTokenExpiresAt()));
        log.info(String.format("| %-25s | %-32s |", "Refresh Expires In",      r.getRefreshTokenExpiresInSeconds() + " sec"));
        log.info(ROW);
    }
    //1.register controller swagger-ui-----------------------------------------------------------------------------------
    @Operation(
            summary = "📝 Register New User",
            description = """
                    Create a new user account in the system.
                    
                    Requirements:
                    - Unique username
                    - Valid email address
                    - Password must meet security requirements
                    - First name and last name required
                    
                    Success Response:
                    Returns JWT access token and refresh token for immediate login.
                    
                    Note: No authentication required for this endpoint.
                    """,
            security = {}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "✅ User registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthDTO.class),
                            examples = @ExampleObject(
                                    name = "Successful Registration",
                                    value = """
                                            {
                                              "success": true,
                                              "message": "User registered successfully",
                                              "data": {
                                                "username": "johndoe",
                                                "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                                "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                                "tokenType": "Bearer",
                                                "accessTokenExpiresAt": "2025-04-10T12:30:00Z",
                                                "refreshTokenExpiresAt": "2025-04-17T10:00:00Z",
                                                "accessTokenExpiresInSeconds": 9000,
                                                "refreshTokenExpiresInSeconds": 604800
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "❌ Invalid input data or user already exists",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "⚠️ Username or email already taken",
                    content = @Content
            )
    })
    //1.register controller----------------------------------------------------------------------------------------------------------------------------------------------
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDTO>> register(@Valid @RequestBody RegisterDTO request) {
        log.info("REQUEST  : POST /api/v1/auth/register | username={}", request.getFirstName());
        AuthDTO response = authService.register(request);
        logAuthTable("REGISTER", response);
        log.info("RESPONSE : 200 OK | username={} | tokenType={} | accessExpiresIn={}sec | refreshExpiresIn={}sec",
                response.getUsername(), response.getTokenType(),
                response.getAccessTokenExpiresInSeconds(),
                response.getRefreshTokenExpiresInSeconds());
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", response));
    }
//----------------------------------------------------------------------------------------------------------------------



    //2.login controller swagger-ui-------------------------------------------------------------------------------------
@Operation(
        summary = "🔑 User Login",
        description = """
                    Authenticate user and receive JWT tokens.
                    
                    Accepts:
                    - Username or Email address
                    - Password
                    
                    Returns:
                    - Access Token (for API requests)
                    - Refresh Token (for token renewal)
                    - Token expiration details
                    
                    Token Lifespan:
                    - Access Token: 150 minutes
                    - Refresh Token: 7 days
                    
                    Note: No authentication required for this endpoint.
                    """,
        security = {}
)
@ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "✅ Login successful",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AuthDTO.class)
                )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "❌ Invalid credentials",
                content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "❌ User not found",
                content = @Content
        )
})
//2.login- controller--------------------------------------------------------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDTO>> login(@Valid @RequestBody LoginDTO request) {
        log.info("REQUEST  : POST /api/v1/auth/login | usernameOrEmail={}", request.getUsernameOrEmail());
        AuthDTO response = authService.login(request.getUsernameOrEmail(), request.getPassword());
        logAuthTable("LOGIN", response);
        log.info("RESPONSE : 200 OK | username={} | tokenType={} | accessExpiresIn={}sec | refreshExpiresIn={}sec",
                response.getUsername(), response.getTokenType(),
                response.getAccessTokenExpiresInSeconds(),
                response.getRefreshTokenExpiresInSeconds());

        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
    }
//----------------------------------------------------------------------------------------------------------------------




    //3.refresh controller swagger ui----------------------------------------------------------------------------------
    @Operation(
            summary = "🔄 Refresh Access Token (Query Param)",
            description = """
                    Obtain a new access token using a valid refresh token.
                    
                    When to use:
                    - When your access token expires (after 150 minutes)
                    - Before making critical API calls with an expiring token
                    
                    Process:
                    1. Provide your refresh token as a query parameter
                    2. Receive a new access token
                    3. Update your authorization header with the new token
                    
                    **Note:** Refresh token must not be expired (valid for 7 days).
                    """,
            security = {}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "✅ Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = AuthDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "❌ Refresh token is blank or invalid",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "❌ Refresh token expired or invalid",
                    content = @Content
            )
    })
    //3.refresh controller----------------------------------------------------------------------------------------------
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthDTO>> refresh(@RequestParam String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token cannot be blank");
        }
        log.info("REQUEST  : POST /api/v1/auth/refresh | Token refresh initiated");
        AuthDTO response = authService.refresh(refreshToken);
        logAuthTable("REFRESH_TOKEN", response);
        log.info("RESPONSE : 200 OK | username={} | tokenType={} | newAccessExpiresIn={}sec | refreshExpiresIn={}sec",
                response.getUsername(), response.getTokenType(),
                response.getAccessTokenExpiresInSeconds(),
                response.getRefreshTokenExpiresInSeconds());
        return ResponseEntity.ok(new ApiResponse<>(true, "Token refreshed successfully", response));
    }
//-----------------------------------------------------------------------------------------------------------





//4.refresh1 controller swagger ui version-1---------------------------------------------------------------------------
    @Operation(
            summary = "🔄 Refresh Access Token (Request Body)",
            description = """
                    Alternative endpoint to refresh access token using request body.
                    
                    **Difference from /refresh:**
                    - Accepts refresh token in request body instead of query parameter
                    - Same functionality and response structure
                    - Use this if you prefer POST body over query params
                    
                    **Note:** Both /refresh and /refresh1 endpoints achieve the same result.
                    """,
            security = {}
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "✅ Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = AuthDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "❌ Invalid request body or token",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "❌ Refresh token expired or invalid",
                    content = @Content
            )
    })

//4.refresh1 controller-------------------------------------------------------------------------------------------------
    @PostMapping("/refresh1")
    public ResponseEntity<ApiResponse<AuthDTO>> refresh(@Valid @RequestBody RefreshRequestDTO request) {
        log.info("REQUEST  : POST /api/v1/auth/refresh | Token refresh initiated");
        AuthDTO response = authService.refresh(request.getRefreshToken());
        logAuthTable("REFRESH_TOKEN", response);
        log.info("RESPONSE : 200 OK | username={} | tokenType={} | newAccessExpiresIn={}sec | refreshExpiresIn={}sec",
                response.getUsername(), response.getTokenType(),
                response.getAccessTokenExpiresInSeconds(),
                response.getRefreshTokenExpiresInSeconds());
        return ResponseEntity.ok(new ApiResponse<>(true, "Token refreshed successfully", response));
    }

}