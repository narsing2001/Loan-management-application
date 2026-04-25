package com.document.verification.service.globalExceptionHandling.customException;

public class VerificationNotFoundException extends RuntimeException {
    public VerificationNotFoundException(String message) {
        super(message);
    }
}
