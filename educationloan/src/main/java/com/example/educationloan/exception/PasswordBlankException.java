package com.example.educationloan.exception;

public class PasswordBlankException extends RuntimeException {
    public PasswordBlankException(String message) {
        super(message);
    }
}
