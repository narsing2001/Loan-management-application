package com.example.LoanTypeStrategyInterest.exception;

public class ApplicationNotFoundException extends RuntimeException {

    ApplicationNotFoundException(){

    }

    ApplicationNotFoundException(String msg){
        super(msg);
    }
}
