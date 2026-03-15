package com.bank.loan.eligibility_service.strategy;

import com.bank.loan.eligibility_service.entity.FinancialDetails;
import com.bank.loan.eligibility_service.enums.RiskCategory;

import java.util.Arrays;
import java.util.List;

public class RiskStrategyFactory {
    private final List<RiskAssessmentStrategy> strategies =
            Arrays.asList(
                    new LowRiskStrategy(),
                    new MediumRiskStrategy(),
                    new HighRiskStrategy()
            );

    public RiskCategory evaluate(FinancialDetails financial,double foir){

        return strategies.stream()
                .filter(s -> s.matches(financial,foir))
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