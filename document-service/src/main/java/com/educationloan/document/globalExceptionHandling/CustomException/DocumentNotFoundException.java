package com.educationloan.document.globalExceptionHandling.CustomException;

public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(String message) {
        super(message);
    }
}
