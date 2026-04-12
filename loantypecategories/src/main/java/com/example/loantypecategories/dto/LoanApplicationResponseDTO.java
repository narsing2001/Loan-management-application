package com.example.loantypecategories.dto;

import com.example.loantypecategories.constant.ApplicationStatus;
import com.example.loantypecategories.constant.CourseType;
import com.example.loantypecategories.constant.Gender;
import com.example.loantypecategories.constant.LoanType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
public class LoanApplicationResponseDTO {

    private Long applicationId;
    private String applicantName;
    private Gender gender;
    private LoanType loanType;
    private CourseType courseType;

    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;
    private BigDecimal interestRate;
    private boolean femaleDiscountApplied;
    private Integer tenureYears;
    private BigDecimal monthlyEmi;

    // Coverage breakdown
    private CoverageBreakdown coverageBreakdown;

    private ApplicationStatus status;
    private String message;
    private LocalDateTime createdAt;

    // ---- Inner class for coverage ----

    @Getter
    @Setter
    public static class CoverageBreakdown {
        private BigDecimal tuitionFee;
        private BigDecimal accommodation;
        private BigDecimal travelExpense;
        private BigDecimal laptopExpense;
        private BigDecimal examFee;
        private BigDecimal otherExpense;
        private BigDecimal totalCoverage;
    }
}
