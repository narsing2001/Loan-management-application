package com.example.LoanTypeStrategyInterest.exception;

public class LoanStrategyNotSelectException extends RuntimeException{
    public LoanStrategyNotSelectException(){

    }
    public LoanStrategyNotSelectException(String msg){
        super(msg);
    }
}
