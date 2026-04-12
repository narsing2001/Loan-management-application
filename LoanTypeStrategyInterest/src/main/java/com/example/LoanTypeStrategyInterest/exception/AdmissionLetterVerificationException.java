package com.example.LoanTypeStrategyInterest.exception;

public class AdmissionLetterVerificationException  extends RuntimeException{
   public AdmissionLetterVerificationException(){

    }

    public  AdmissionLetterVerificationException(String msg){
        super(msg);
    }
}
