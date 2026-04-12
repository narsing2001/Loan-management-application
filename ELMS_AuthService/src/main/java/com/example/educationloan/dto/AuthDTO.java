package com.example.educationloan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDTO {
    private String accessToken;
    private String refreshToken;
    private String username;
    private String tokenType = "Bearer";


    private LocalDateTime accessTokenExpiresAt;
    private LocalDateTime refreshTokenExpiresAt;
    private long          accessTokenExpiresInSeconds;
    private long          refreshTokenExpiresInSeconds;


    public AuthDTO(String accessToken, String refreshToken, String username) {
        this.accessToken  = accessToken;
        this.refreshToken = refreshToken;
        this.username     = username;
    }
}