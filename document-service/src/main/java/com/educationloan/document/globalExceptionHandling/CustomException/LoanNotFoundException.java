package com.educationloan.document.globalExceptionHandling.CustomException;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(String message) {
        super(message);
    }
}
