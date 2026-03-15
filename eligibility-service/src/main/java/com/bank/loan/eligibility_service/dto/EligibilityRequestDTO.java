package com.bank.loan.eligibility_service.dto;

import com.bank.loan.eligibility_service.entity.CoApplicantDetails;
import com.bank.loan.eligibility_service.entity.EducationDetails;
import com.bank.loan.eligibility_service.entity.FinancialDetails;
import com.bank.loan.eligibility_service.entity.StudentDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EligibilityRequestDTO {

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
 private CoApplicantDetails coApplicantDetails;


}

