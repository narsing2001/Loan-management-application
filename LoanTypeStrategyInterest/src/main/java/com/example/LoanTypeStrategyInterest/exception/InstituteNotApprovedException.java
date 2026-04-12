package com.example.LoanTypeStrategyInterest.exception;

public class InstituteNotApprovedException extends RuntimeException{
    InstituteNotApprovedException(){

    }

    public InstituteNotApprovedException(String msg){
        super(msg);
    }
}
