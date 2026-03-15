package com.bank.loan.eligibility_service.strategy;

import com.bank.loan.eligibility_service.entity.FinancialDetails;

public class MediumRiskStrategy implements RiskAssessmentStrategy{
    @Override
    public boolean matches(FinancialDetails f, double foir) {
        return f.getCreditScore() >= 650;
    }
}
