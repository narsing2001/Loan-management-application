package com.bank.loan.eligibility_service.exception;

public class InvalidPhoneNumber extends RuntimeException{

    public InvalidPhoneNumber(String message){
        super(message);
    }

}
