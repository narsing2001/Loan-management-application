package com.example.loantypecategories.exception;

public class LoanRejectionException extends RuntimeException {
    public LoanRejectionException(String reason) {
        super("Loan application rejected: " + reason);
    }
}
