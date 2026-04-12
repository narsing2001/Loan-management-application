package com.example.LoanTypeStrategyInterest.exception;

public class EmailAlradyExistsException extends RuntimeException {
    public  EmailAlradyExistsException(){

    }

    public EmailAlradyExistsException(String msg){
        super(msg);
    }
}
