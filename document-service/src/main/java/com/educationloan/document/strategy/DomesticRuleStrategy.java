package com.educationloan.document.strategy;

import com.educationloan.document.enumConst.ApplicantType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("DOMESTIC")
public class DomesticRuleStrategy implements DocumentRuleStrategy {

    private final List<LoanApplicantRule> rules = List.of(

            new LoanApplicantRule(BigDecimal.ZERO,
                    new BigDecimal("500000"),
                    Set.of(ApplicantType.STUDENT)
            ),

            new LoanApplicantRule(
                    new BigDecimal("500001"),
                    new BigDecimal("1000000"),
                    Set.of(ApplicantType.STUDENT, ApplicantType.CO_APPLICANT)
            ),

            new LoanApplicantRule(
                    new BigDecimal("1000001"),
                    new BigDecimal("1500000"),
                    Set.of(ApplicantType.STUDENT, ApplicantType.CO_APPLICANT, ApplicantType.COLLATERAL_PROVIDER)
            )
    );

    @Override
    public Set<ApplicantType> getAllowedApplicants(BigDecimal loanAmount) {

        return rules.stream()
                .filter(rule -> rule.matches(loanAmount))
                .flatMap(rule -> rule.getApplicants().stream())
                .collect(Collectors.toSet());
    }
}