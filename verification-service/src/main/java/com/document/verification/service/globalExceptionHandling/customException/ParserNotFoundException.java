package com.document.verification.service.globalExceptionHandling.customException;

public class ParserNotFoundException extends RuntimeException {
    public ParserNotFoundException(String message) {
        super(message);
    }
}
