package com.bank.loan.eligibility_service.strategy;

import com.bank.loan.eligibility_service.entity.FinancialDetails;

public interface RiskAssessmentStrategy {

    boolean matches(FinancialDetails financial, double foir);

}
