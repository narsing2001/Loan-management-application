package com.bank.loan.eligibility_service.strategy;

import com.bank.loan.eligibility_service.entity.FinancialDetails;
import com.bank.loan.eligibility_service.enums.CollegeCategory;

public interface RiskAssessmentStrategy {

    boolean matches(FinancialDetails financial, double foir, CollegeCategory collegeCategory);

}
