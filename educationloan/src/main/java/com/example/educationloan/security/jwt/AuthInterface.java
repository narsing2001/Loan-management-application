package com.example.educationloan.security.jwt;

import com.example.educationloan.dto.AuthDTO;
import com.example.educationloan.dto.RegisterDTO;

public interface AuthInterface {
    AuthDTO login(String usernameOrEmail, String password);
    AuthDTO register(RegisterDTO request);
    AuthDTO refresh(String refreshToken);

}
