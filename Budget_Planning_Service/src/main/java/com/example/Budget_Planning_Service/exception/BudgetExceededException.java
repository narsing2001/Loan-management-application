package com.example.Budget_Planning_Service.exception;

public class BudgetExceededException extends RuntimeException {
    public BudgetExceededException(String message) { super(message); }
}
