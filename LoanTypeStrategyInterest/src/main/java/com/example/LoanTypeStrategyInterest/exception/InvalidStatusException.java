package com.example.LoanTypeStrategyInterest.exception;

import lombok.Getter;

@Getter
public class InvalidStatusException extends RuntimeException{
    private final String invalidStatus;
    public InvalidStatusException(String invalidStatus){
        this.invalidStatus = invalidStatus;
    }
    public InvalidStatusException(String msg, String invalidStatus){
        super(msg);
        this.invalidStatus = invalidStatus;
    }
}
