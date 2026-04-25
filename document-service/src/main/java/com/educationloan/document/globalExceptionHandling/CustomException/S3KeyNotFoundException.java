package com.educationloan.document.globalExceptionHandling.CustomException;

public class S3KeyNotFoundException extends RuntimeException {

    public S3KeyNotFoundException(String message) {
        super(message);
    }
}
