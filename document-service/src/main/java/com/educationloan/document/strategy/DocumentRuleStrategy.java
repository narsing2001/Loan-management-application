
package com.educationloan.document.strategy;

import com.educationloan.document.enumConst.ApplicantType;

import java.math.BigDecimal;
import java.util.Set;

public interface DocumentRuleStrategy {

        Set<ApplicantType> getAllowedApplicants(BigDecimal loanAmount);
    }
