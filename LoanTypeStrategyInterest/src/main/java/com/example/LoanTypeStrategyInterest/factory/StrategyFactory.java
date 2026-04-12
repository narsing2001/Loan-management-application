package com.example.LoanTypeStrategyInterest.factory;

import com.example.LoanTypeStrategyInterest.enums.enum1.LoanType;
import com.example.LoanTypeStrategyInterest.exception.LoanStrategyNotSelectException;
import com.example.LoanTypeStrategyInterest.exception.UnsupportedLoanTypeSelectionException;
import com.example.LoanTypeStrategyInterest.strategy.DomesticLoanStrategy;
import com.example.LoanTypeStrategyInterest.strategy.InterestStrategy;
import com.example.LoanTypeStrategyInterest.strategy.InternationalLoanStrategy;

public class StrategyFactory {
    private StrategyFactory() {

    }
    public static InterestStrategy getStrategy(LoanType loanType) throws UnsupportedLoanTypeSelectionException {
        if (loanType == null) {
            throw new LoanStrategyNotSelectException("applicant not selected loan type, loan type empty selection");
        }
        return switch (loanType){
            case DOMESTIC -> new DomesticLoanStrategy();
            case INTERNATIONAL -> new InternationalLoanStrategy();
            default            -> throw new UnsupportedLoanTypeSelectionException("Unsupported loan type: must be DOMESTIC or INTERNATIONAL");
        };
    }

}
