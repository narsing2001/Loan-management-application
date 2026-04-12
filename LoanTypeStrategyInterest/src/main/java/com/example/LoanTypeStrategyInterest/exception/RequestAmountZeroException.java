package com.example.LoanTypeStrategyInterest.exception;

public class RequestAmountZeroException extends RuntimeException{

    public RequestAmountZeroException(){

    }

    public RequestAmountZeroException(String msg){
        super(msg);
    }
}
