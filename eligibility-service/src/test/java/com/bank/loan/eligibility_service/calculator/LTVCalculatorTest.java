package com.bank.loan.eligibility_service.calculator;

import com.bank.loan.eligibility_service.Calculator.LTVCalculator;
import com.bank.loan.eligibility_service.entity.FinancialDetails;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LTVCalculatorTest {

    private final LTVCalculator calculator = new LTVCalculator();

    @Test
    void shouldCalculateLtvCorrectly() {
        FinancialDetails f = FinancialDetails.builder()
                .courseFees(1000000.0)
                .requestedLoanAmount(800000.0)
                .build();

        double result = calculator.calculate(f);

        assertEquals(80.0, result);
    }

    @Test
    void shouldReturnZeroWhenFeesNull() {
        FinancialDetails f = new FinancialDetails();
        assertEquals(0.0, calculator.calculate(f));
    }
}