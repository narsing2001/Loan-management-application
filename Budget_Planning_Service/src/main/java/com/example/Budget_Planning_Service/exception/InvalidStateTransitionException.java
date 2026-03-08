package com.example.Budget_Planning_Service.exception;

public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(String message) { super(message); }
}
