package com.example.LoanTypeStrategyInterest.exception;

public class EmiTooLowException extends RuntimeException {
    public EmiTooLowException(String message) {
        super(message);
    }
}
