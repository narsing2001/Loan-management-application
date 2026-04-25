package com.educationloan.document.strategy;
import com.educationloan.document.enumConst.ApplicantType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InternationalRuleStrategyTest {
    private final InternationalRuleStrategy strategy = new InternationalRuleStrategy();

    @Test
    void loanMidRange_shouldAllowGuarantor() {
        Set<ApplicantType> result =
                strategy.getAllowedApplicants(new BigDecimal("500000"));

        assertTrue(result.contains(ApplicantType.STUDENT));
        assertTrue(result.contains(ApplicantType.CO_APPLICANT));
        assertTrue(result.contains(ApplicantType.GUARANTOR));
        assertFalse(result.contains(ApplicantType.COLLATERAL_PROVIDER));
    }

    @Test
    void loanHighRange_shouldAllowAll() {
        Set<ApplicantType> result =
                strategy.getAllowedApplicants(new BigDecimal("2000000"));

        assertTrue(result.contains(ApplicantType.STUDENT));
        assertTrue(result.contains(ApplicantType.CO_APPLICANT));
        assertTrue(result.contains(ApplicantType.GUARANTOR));
        assertTrue(result.contains(ApplicantType.COLLATERAL_PROVIDER));
    }

    @Test
    void loanBelowRange_shouldReturnEmpty() {
        Set<ApplicantType> result =
                strategy.getAllowedApplicants(new BigDecimal("50000"));

        assertTrue(result.isEmpty());
    }
}