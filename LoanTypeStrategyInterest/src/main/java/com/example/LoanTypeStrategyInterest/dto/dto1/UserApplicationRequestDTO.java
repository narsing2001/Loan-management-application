package com.example.LoanTypeStrategyInterest.dto.dto1;

import com.example.LoanTypeStrategyInterest.entity.entity1.UserApplicationDetail;
import com.example.LoanTypeStrategyInterest.enums.enum1.CourseType;
import com.example.LoanTypeStrategyInterest.enums.enum1.Gender;
import com.example.LoanTypeStrategyInterest.enums.enum1.LoanType;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class UserApplicationRequestDTO {

    @NotBlank(message = "Applicant name is required")
    private String applicantName;

    @NotBlank(message = "Applicant email is required")
    @Email(message = "Invalid email format")
    private String applicantEmail;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    @NotNull(message = "Course type is required")
    private CourseType courseType;

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "100000.00", message = "Requested amount must be at least 100000")
    private BigDecimal requestedAmount;

    @NotNull(message = "Tenure years is required")
    @Min(value = 1, message = "Tenure must be at least 1 year")
    @Max(value = 15, message = "Tenure cannot exceed 15 years")
    private Integer tenureYears;

    @DecimalMin(value = "0.00", message = "Coverage cannot be negative")
    private BigDecimal tuitionFeeCoverage;

    @DecimalMin(value = "0.00", message = "Coverage cannot be negative")
    private BigDecimal accommodationCoverage;

    @DecimalMin(value = "0.00", message = "Coverage cannot be negative")
    private BigDecimal travelExpenseCoverage;

    @DecimalMin(value = "0.00", message = "Coverage cannot be negative")
    private BigDecimal laptopExpenseCoverage;

    @DecimalMin(value = "0.00", message = "Coverage cannot be negative")
    private BigDecimal examFeeCoverage;

    @DecimalMin(value = "0.00", message = "Coverage cannot be negative")
    private BigDecimal otherExpenseCoverage;

    private boolean admissionLetterVerified;
    private boolean institutionApproved;


    public static UserApplicationDetail toRequestDTO(UserApplicationRequestDTO dto) {
        UserApplicationDetail entity = new UserApplicationDetail();
        entity.setApplicantName(dto.getApplicantName());
        entity.setApplicantEmail(dto.getApplicantEmail());
        entity.setGender(dto.getGender());
        entity.setLoanType(dto.getLoanType());
        entity.setCourseType(dto.getCourseType());
        entity.setRequestedAmount(dto.getRequestedAmount());
        entity.setTenureYears(dto.getTenureYears());
        entity.setTuitionFeeCoverage(dto.getTuitionFeeCoverage());
        entity.setAccommodationCoverage(dto.getAccommodationCoverage());
        entity.setTravelExpenseCoverage(dto.getTravelExpenseCoverage());
        entity.setLaptopExpenseCoverage(dto.getLaptopExpenseCoverage());
        entity.setExamFeeCoverage(dto.getExamFeeCoverage());
        entity.setOtherExpenseCoverage(dto.getOtherExpenseCoverage());
        entity.setAdmissionLetterVerified(dto.isAdmissionLetterVerified());
        entity.setInstitutionApproved(dto.isInstitutionApproved());
        return entity;
    }
}