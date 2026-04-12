package com.example.LoanTypeStrategyInterest.exception;

public class InvalidLoanParameterException extends RuntimeException {
    InvalidLoanParameterException(){

    }
    public InvalidLoanParameterException(String msg){
        super(msg);
    }
}
