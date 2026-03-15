package com.bank.loan.eligibility_service.strategy;

import com.bank.loan.eligibility_service.entity.FinancialDetails;

public class HighRiskStrategy implements  RiskAssessmentStrategy{
    @Override
    public boolean matches(FinancialDetails financial, double foir) {
        return true;
    }
}
