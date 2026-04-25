package com.educationloan.document.strategy;
import com.educationloan.document.enumConst.ApplicantType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@AllArgsConstructor
public class LoanApplicantRule {
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Set<ApplicantType> applicants;

    public boolean matches(BigDecimal loanAmount) {
        return loanAmount.compareTo(minAmount) >= 0 &&
                loanAmount.compareTo(maxAmount) <= 0;
    }
}
