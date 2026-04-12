package com.example.LoanTypeStrategyInterest.exception;

public class InterestComputationException  extends RuntimeException{

    InterestComputationException(){

    }

    public InterestComputationException(String msg){
        super(msg);
    }


}
