package com.bank.loan.eligibility_service.strategy;

import com.bank.loan.eligibility_service.entity.FinancialDetails;
import com.bank.loan.eligibility_service.enums.CollegeCategory;
import org.springframework.stereotype.Component;

@Component
public class MediumRiskStrategy implements RiskAssessmentStrategy{
    @Override
    public boolean matches(FinancialDetails financial, double foir, CollegeCategory collegeCategory) {

        return financial.getCreditScore() >= 650 &&
                foir < 50 &&
                (collegeCategory == CollegeCategory.TIER_2 ||
                  collegeCategory == CollegeCategory.TIER_3 );
    }
}
