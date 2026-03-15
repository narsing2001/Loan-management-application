package com.educationloan.document.globalExceptionHandling.CustomException;

public class ApplicantNotFoundException extends RuntimeException {
    public ApplicantNotFoundException(String message) {
        super(message);
    }
}
