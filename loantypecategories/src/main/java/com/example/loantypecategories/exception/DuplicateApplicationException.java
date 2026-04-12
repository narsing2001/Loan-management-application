package com.example.loantypecategories.exception;

public class DuplicateApplicationException extends RuntimeException {
    public DuplicateApplicationException(String email) {
        super("A loan application already exists for email: " + email);
    }
}
