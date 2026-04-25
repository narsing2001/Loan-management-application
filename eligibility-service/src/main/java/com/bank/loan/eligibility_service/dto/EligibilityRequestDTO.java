package com.bank.loan.eligibility_service.dto;

import com.bank.loan.eligibility_service.entity.*;
import com.bank.loan.eligibility_service.enums.LoanType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EligibilityRequestDTO {

 @NotNull
 private LoanType loanType;

 @NotNull(message = "student deatils are required")
 @Valid
 private StudentDetails studentDetails;

 @NotNull(message = "Education details are required")
 @Valid
 private EducationDetails educationDetails;

 @NotNull(message = "financial details are required")
 @Valid
 private FinancialDetails financialDetails;

 @Valid
 @NotNull(message = "co-applicant details are required")
 private CoApplicantDetails coApplicantDetails;

 @Valid
 @NotNull(message = "DocumentDetails are required")
 private DocumentDetails documentDetails;


}

