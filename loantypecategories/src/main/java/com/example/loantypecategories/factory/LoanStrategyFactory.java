package com.example.loantypecategories.factory;

import com.example.loantypecategories.constant.LoanType;

import com.example.loantypecategories.strategy.DomesticLoanStrategy;
import com.example.loantypecategories.strategy.InternationalLoanStrategy;
import com.example.loantypecategories.strategy.LoanInterestStrategy;

import java.util.Optional;

public class LoanStrategyFactory {

    private LoanStrategyFactory() {

    }

    public static LoanInterestStrategy getStrategy(LoanType loanType) {
        if (loanType == null) {
            throw new IllegalArgumentException("Loan type must not be null");
        }
        return switch (loanType) {
            case DOMESTIC      -> new DomesticLoanStrategy();
            case INTERNATIONAL -> new InternationalLoanStrategy();

        };
    }
}