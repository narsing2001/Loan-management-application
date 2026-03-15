package com.bank.loan.eligibility_service.nationalityStrategy;

import com.bank.loan.eligibility_service.entity.CoApplicantDetails;
import com.bank.loan.eligibility_service.entity.FinancialDetails;
import com.bank.loan.eligibility_service.entity.StudentDetails;

public interface NationalityEligibilityStrategy {
    void validate(StudentDetails student,
                  FinancialDetails financial,
                  CoApplicantDetails coApplicant);


}
