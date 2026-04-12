package com.example.loantypecategories.strategy.strategy1;

import com.example.loantypecategories.dto.dto1.LoanApplicationRequestDTO1;
import com.example.loantypecategories.entity.entit1.LoanApplication1;
import com.example.loantypecategories.entity.entit1.LoanStrategyConfig;
import com.example.loantypecategories.exception.AdmissionLetterRequiredException;
import org.springframework.stereotype.Component;

/**
 * Strategy implementation for International Education Loans
 *
 * Key Features:
 * - Requires admission letter from foreign institution
 * - Higher interest rates (10-15%)
 * - Up to ₹1.5 Crore loan amount
 * - Bank-specific and gender-specific rates
 * - Higher collateral requirements
 * - Longer tenure options (up to 20 years)
 */
@Component("INTERNATIONAL")
public class InternationalLoanStrategy1 extends AbstractLoanStrategy {

    @Override
    public void validatePreConditions(LoanApplicationRequestDTO1 request) {
        if (!request.isHasAdmissionLetter()) {
            throw new AdmissionLetterRequiredException(
                    "Loan application rejected: Valid admission letter from the foreign institution " +
                            "is mandatory for international education loans. Please provide proof of admission."
            );
        }
    }

    @Override
    public void calculateCoverage(LoanApplicationRequestDTO1 request, LoanApplication1 application, LoanStrategyConfig config) {

    }

    @Override
    public String getStrategyType() {
        return "INTERNATIONAL";
    }
}