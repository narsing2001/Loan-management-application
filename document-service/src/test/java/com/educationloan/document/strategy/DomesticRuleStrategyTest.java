package com.educationloan.document.strategy;
import com.educationloan.document.enumConst.ApplicantType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DomesticRuleStrategyTest {
    private final DomesticRuleStrategy strategy = new DomesticRuleStrategy();

    @Test
    void loanBelow5L_shouldAllowOnlyStudent() {
        Set<ApplicantType> result = strategy.getAllowedApplicants(new BigDecimal("400000"));

        assertTrue(result.contains(ApplicantType.STUDENT));
        assertFalse(result.contains(ApplicantType.CO_APPLICANT));
    }

    @Test
    void loanBetween5LTo10L_shouldAllowStudentAndCoApplicant() {
        Set<ApplicantType> result =
                strategy.getAllowedApplicants(new BigDecimal("700000"));

        assertTrue(result.contains(ApplicantType.STUDENT));
        assertTrue(result.contains(ApplicantType.CO_APPLICANT));
        assertFalse(result.contains(ApplicantType.COLLATERAL_PROVIDER));
    }

    @Test
    void loanAbove10L_shouldAllowAllApplicants() {
        Set<ApplicantType> result = strategy.getAllowedApplicants(new BigDecimal("1200000"));

        assertTrue(result.contains(ApplicantType.STUDENT));
        assertTrue(result.contains(ApplicantType.CO_APPLICANT));
        assertTrue(result.contains(ApplicantType.COLLATERAL_PROVIDER));
    }

    @Test
    void loanOutOfRange_shouldReturnEmptySet() {
        Set<ApplicantType> result = strategy.getAllowedApplicants(new BigDecimal("2000000"));
        assertTrue(result.isEmpty());
    }
}
