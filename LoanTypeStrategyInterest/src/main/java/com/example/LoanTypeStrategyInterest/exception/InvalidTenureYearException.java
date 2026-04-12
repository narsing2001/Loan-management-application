package com.example.LoanTypeStrategyInterest.exception;

public class InvalidTenureYearException extends RuntimeException{
    InvalidTenureYearException(){

    }
    public InvalidTenureYearException(String msg){
        super(msg);
    }

}
