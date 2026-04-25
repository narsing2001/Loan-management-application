package com.bank.loan.eligibility_service.strategy;

import com.bank.loan.eligibility_service.entity.FinancialDetails;
import com.bank.loan.eligibility_service.enums.CollegeCategory;
import com.bank.loan.eligibility_service.enums.RiskCategory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RiskStrategyFactoryTest {

    private final RiskStrategyFactory factory =
            new RiskStrategyFactory(
                    List.of(
                            new LowRiskStrategy(),
                            new MediumRiskStrategy(),
                            new HighRiskStrategy()
                    )
            );

    @Test
    void shouldReturnLowRisk() {
        FinancialDetails f = FinancialDetails.builder()
                .creditScore(800)
                .build();

        RiskCategory result = factory.evaluate(f, 30, CollegeCategory.TIER_1);

        assertEquals(RiskCategory.LOW, result);
    }

    @Test
    void shouldReturnMediumRisk() {
        FinancialDetails f = FinancialDetails.builder()
                .creditScore(700)
                .build();

        RiskCategory result = factory.evaluate(f, 40, CollegeCategory.TIER_2);

        assertEquals(RiskCategory.MEDIUM, result);
    }

    @Test
    void shouldReturnHighRisk() {
        FinancialDetails f = FinancialDetails.builder()
                .creditScore(400)
                .build();

        RiskCategory result = factory.evaluate(f, 70, CollegeCategory.PRIVATE);

        assertEquals(RiskCategory.HIGH, result);
    }

    @Test
    void shouldReturnDefaultHighRiskWhenNoMatch() {
        FinancialDetails f = FinancialDetails.builder()
                .creditScore(600)
                .build();

        RiskCategory result = factory.evaluate(f, 80, CollegeCategory.OTHER);

        assertEquals(RiskCategory.HIGH, result);
    }
}