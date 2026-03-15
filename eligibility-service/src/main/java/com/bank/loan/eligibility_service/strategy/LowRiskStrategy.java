package com.bank.loan.eligibility_service.strategy;

import com.bank.loan.eligibility_service.entity.FinancialDetails;

public class LowRiskStrategy implements RiskAssessmentStrategy{
    @Override
    public boolean matches(FinancialDetails f, double foir) {
        return f.getCreditScore() >= 750 && foir < 40;
    }
}
