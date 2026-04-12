package com.example.loantypecategories.strategy.strategy1;

public class InstitutionNotApprovedException extends Throwable {
    public InstitutionNotApprovedException(String msg) {
        super(msg);
    }
}
