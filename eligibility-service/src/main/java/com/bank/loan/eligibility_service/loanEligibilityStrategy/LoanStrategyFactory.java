package com.bank.loan.eligibility_service.loanEligibilityStrategy;

import com.bank.loan.eligibility_service.enums.LoanType;
import org.springframework.stereotype.Component;

@Component
public class LoanStrategyFactory {

    private final DomesticLoanStrategy domestic;
    private final InternationalLoanStrategy international;

    public LoanStrategyFactory(DomesticLoanStrategy domestic,
                               InternationalLoanStrategy international) {
        this.domestic = domestic;
        this.international = international;
    }

    public LoanEligibilityStrategy getStrategy(LoanType loanType) {

        if (loanType == LoanType.DOMESTIC) {
            return domestic;
        } else if (loanType == LoanType.INTERNATIONAL) {
            return international;
        }

        throw new RuntimeException("Invalid Loan Type");

    }
}