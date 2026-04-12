package com.example.loantypecategories.exception;

public class LoanApplicationNotFoundException extends RuntimeException {
    public LoanApplicationNotFoundException(Long id) {
        super("Loan application not found with ID: " + id);
    }
}
