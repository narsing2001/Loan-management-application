package com.bank.loan.eligibility_service.loanEligibilityStrategy;

import com.bank.loan.eligibility_service.entity.CoApplicantDetails;
import com.bank.loan.eligibility_service.entity.EducationDetails;
import com.bank.loan.eligibility_service.entity.FinancialDetails;
import com.bank.loan.eligibility_service.entity.StudentDetails;

public interface LoanEligibilityStrategy {
    void validate(StudentDetails student,
                                                   EducationDetails education,
                                                   FinancialDetails financial,
                                                   CoApplicantDetails coApplicant);

}
