package com.example.educationloan.exception;

public class EmailMismatchException extends RuntimeException {
    public EmailMismatchException(String message) {
        super(message);
    }

    public EmailMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}