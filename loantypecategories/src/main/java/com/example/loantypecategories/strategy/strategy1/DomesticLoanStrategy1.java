package com.example.loantypecategories.strategy.strategy1;

import com.example.loantypecategories.dto.dto1.LoanApplicationRequestDTO1;
import org.springframework.stereotype.Component;


/**
 * Strategy implementation for Domestic Education Loans
 *
 * Key Features:
 * - Requires UGC/AICTE approved institution
 * - Lower interest rates (9-12%)
 * - Bank-specific and gender-specific rates
 * - Government bank benefits
 * - Subsidy eligibility for certain categories
 */
@Component
public  class DomesticLoanStrategy1 extends AbstractLoanStrategy {

    @Override
    public void validatePreConditions(LoanApplicationRequestDTO1 request) throws InstitutionNotApprovedException {
        if (!request.isInstitutionApproved()) {
            throw new InstitutionNotApprovedException(
                    "Loan application rejected: Institution must be UGC/AICTE approved. " +
                            "Only government-recognized institutions qualify for domestic education loans."
            );
        }
    }

    @Override
    public String getStrategyType() {
        return "DOMESTIC";
    }
}