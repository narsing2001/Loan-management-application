package com.example.LoanTypeStrategyInterest.exception;

import java.util.UUID;

public class LoanApplicationNotFoundException extends RuntimeException {
    public LoanApplicationNotFoundException(UUID id) {
        super("Education Loan application not found with ID: " + id);
    }
}
