package com.bank.loan.eligibility_service.nationalityStrategy;

import com.bank.loan.eligibility_service.entity.CoApplicantDetails;
import com.bank.loan.eligibility_service.entity.FinancialDetails;
import com.bank.loan.eligibility_service.entity.StudentDetails;
import com.bank.loan.eligibility_service.exception.BusinessException;

public class NriEligibilityStrategy implements  NationalityEligibilityStrategy{
    @Override
    public void validate(StudentDetails student, FinancialDetails financial, CoApplicantDetails coApplicant) {
        if (coApplicant == null || !Boolean.TRUE.equals(coApplicant.getCoApplicationPresent())) {

            throw new BusinessException(
                    "NRI students must have a co-applicant in India");
    }
}}
