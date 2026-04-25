package com.bank.loan.eligibility_service.strategy;

import com.bank.loan.eligibility_service.entity.FinancialDetails;
import com.bank.loan.eligibility_service.enums.CollegeCategory;
import com.bank.loan.eligibility_service.enums.RiskCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
@Component
@RequiredArgsConstructor
public class RiskStrategyFactory {
    private final List<RiskAssessmentStrategy> strategies;


    public RiskCategory evaluate(FinancialDetails financial, double foir, CollegeCategory collegeCategory){

        return strategies.stream()
                .filter(s -> s.matches(financial,foir,collegeCategory))
                .findFirst()
                .map(strategy -> {

                    if(strategy instanceof LowRiskStrategy)
                        return RiskCategory.LOW;

                    if(strategy instanceof MediumRiskStrategy)
                        return RiskCategory.MEDIUM;

                    return RiskCategory.HIGH;

                })
                .orElse(RiskCategory.HIGH);
    }
}