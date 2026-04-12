package com.example.LoanTypeStrategyInterest.exception;

public class UnsupportedLoanTypeSelectionException extends RuntimeException{
    public UnsupportedLoanTypeSelectionException(){

    }

    public UnsupportedLoanTypeSelectionException(String msg){
        super(msg);

    }
}
