package com.bank.loan.eligibility_service.nationalityStrategy;

import com.bank.loan.eligibility_service.entity.CoApplicantDetails;
import com.bank.loan.eligibility_service.entity.FinancialDetails;
import com.bank.loan.eligibility_service.entity.StudentDetails;
import com.bank.loan.eligibility_service.exception.BusinessException;

public class ForeignEligibilityStrategy implements NationalityEligibilityStrategy{
    @Override
    public void validate(StudentDetails student, FinancialDetails financial, CoApplicantDetails coApplicant) {
        if (financial.getCreditScore() < 700) {

            throw new BusinessException(
                    "Foreign students require minimum credit score of 700");
        }
    }
}
