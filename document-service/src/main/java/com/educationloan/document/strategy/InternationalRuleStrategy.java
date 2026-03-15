package com.educationloan.document.strategy;

import com.educationloan.document.enumConst.ApplicantType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("INTERNATIONAL")
public class InternationalRuleStrategy implements DocumentRuleStrategy {

    private final List<LoanApplicantRule> rules = List.of(

            new LoanApplicantRule(
                    new BigDecimal("100000"),
                    new BigDecimal("1000000"),
                    Set.of(
                            ApplicantType.STUDENT,
                            ApplicantType.CO_APPLICANT,
                            ApplicantType.GUARANTOR
                    )
            ),

            new LoanApplicantRule(
                    new BigDecimal("1000001"),
                    new BigDecimal("2500000"),
                    Set.of(
                            ApplicantType.STUDENT,
                            ApplicantType.CO_APPLICANT,
                            ApplicantType.GUARANTOR,
                            ApplicantType.COLLATERAL_PROVIDER
                    )
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