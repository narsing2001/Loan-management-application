package com.example.LoanTypeStrategyInterest.strategy;

import com.example.LoanTypeStrategyInterest.dto.dto1.UserApplicationRequestDTO;
import com.example.LoanTypeStrategyInterest.entity.entity1.UserApplicationDetail;

import java.math.BigDecimal;

public interface InterestStrategy {

    BigDecimal findInterestRate(UserApplicationRequestDTO requestDTO);
    BigDecimal getMaxLoanAmount(UserApplicationRequestDTO requestDTO);
     int getMaxTenureYears(int TenureYears);
    void validatePreConditions(UserApplicationRequestDTO requestDTO);
    void totalCoverage(UserApplicationRequestDTO requestDTO, UserApplicationDetail userApplicationDetail);
}
