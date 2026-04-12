package com.example.loantypecategories.strategy;

import com.example.loantypecategories.dto.LoanApplicationRequestDTO;
import com.example.loantypecategories.entity.LoanApplication;

import java.math.BigDecimal;

public interface LoanInterestStrategy {
    BigDecimal calculateInterestRate(LoanApplicationRequestDTO request);
    BigDecimal getMaxLoanAmount(LoanApplicationRequestDTO request);
    int getMaxTenureYears();
    void validatePreConditions(LoanApplicationRequestDTO request);
    void calculateCoverage(LoanApplicationRequestDTO request, LoanApplication application);

}