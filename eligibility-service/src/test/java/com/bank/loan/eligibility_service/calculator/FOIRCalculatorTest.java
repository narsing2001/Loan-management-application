package com.bank.loan.eligibility_service.calculator;

import com.bank.loan.eligibility_service.Calculator.FOIRCalculator;
import com.bank.loan.eligibility_service.entity.FinancialDetails;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FOIRCalculatorTest {

    private final FOIRCalculator calculator = new FOIRCalculator();

    @Test
    void shouldCalculateFoirCorrectly() {
        FinancialDetails f = FinancialDetails.builder()
                .annualIncome(600000.0)
                .existingEMI(5000.0)
                .build();

        double result = calculator.calculate(f);

        assertTrue(result > 0);
    }

    @Test
    void shouldReturn100WhenIncomeNull() {
        FinancialDetails f = new FinancialDetails();
        assertEquals(100.0, calculator.calculate(f));
    }
}