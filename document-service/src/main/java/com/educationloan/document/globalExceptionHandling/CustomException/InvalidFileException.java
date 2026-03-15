package com.educationloan.document.globalExceptionHandling.CustomException;

public class InvalidFileException extends RuntimeException {
    public InvalidFileException(String message) {
        super(message);
    }
}
