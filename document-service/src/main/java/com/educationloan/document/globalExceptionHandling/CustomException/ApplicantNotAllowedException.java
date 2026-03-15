package com.educationloan.document.globalExceptionHandling.CustomException;

public class ApplicantNotAllowedException extends RuntimeException {
    public ApplicantNotAllowedException(String message) {
        super(message);
    }
}
