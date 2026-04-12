package com.example.LoanTypeStrategyInterest.dto.dto1;

import com.example.LoanTypeStrategyInterest.entity.entity1.UserApplicationDetail;
import com.example.LoanTypeStrategyInterest.enums.enum1.ApplicationStatus;
import com.example.LoanTypeStrategyInterest.enums.enum1.CourseType;
import com.example.LoanTypeStrategyInterest.enums.enum1.Gender;
import com.example.LoanTypeStrategyInterest.enums.enum1.LoanType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserApplicationResponseDTO {

    private UUID id;
    private String applicantName;
    private String applicantEmail;
    private Gender gender;
    private LoanType loanType;
    private CourseType courseType;

    private BigDecimal requestedAmount;
    private BigDecimal interestRate;
    private BigDecimal approvedAmount;
    private Integer tenureYears;
    private BigDecimal monthlyEmi;

    private BigDecimal tuitionFeeCoverage;
    private BigDecimal accommodationCoverage;
    private BigDecimal travelExpenseCoverage;
    private BigDecimal laptopExpenseCoverage;
    private BigDecimal examFeeCoverage;
    private BigDecimal otherExpenseCoverage;

    private boolean admissionLetterVerified;
    private boolean institutionApproved;

    private ApplicationStatus applicationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // ---- Mapper from Entity ----
    public static UserApplicationResponseDTO toResponse(UserApplicationDetail entity) {
        return UserApplicationResponseDTO.builder()
                .id(entity.getId())
                .applicantName(entity.getApplicantName())
                .applicantEmail(entity.getApplicantEmail())
                .gender(entity.getGender())
                .loanType(entity.getLoanType())
                .courseType(entity.getCourseType())
                .requestedAmount(entity.getRequestedAmount())
                .interestRate(entity.getInterestRate())
                .approvedAmount(entity.getApprovedAmount())
                .tenureYears(entity.getTenureYears())
                .monthlyEmi(entity.getMonthlyEmi())
                .tuitionFeeCoverage(entity.getTuitionFeeCoverage())
                .accommodationCoverage(entity.getAccommodationCoverage())
                .travelExpenseCoverage(entity.getTravelExpenseCoverage())
                .laptopExpenseCoverage(entity.getLaptopExpenseCoverage())
                .examFeeCoverage(entity.getExamFeeCoverage())
                .otherExpenseCoverage(entity.getOtherExpenseCoverage())
                .admissionLetterVerified(entity.isAdmissionLetterVerified())
                .institutionApproved(entity.isInstitutionApproved())
                .applicationStatus(entity.getApplicationStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}