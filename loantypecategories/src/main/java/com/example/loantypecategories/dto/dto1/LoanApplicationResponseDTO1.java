package com.example.loantypecategories.dto.dto1;

import com.example.loantypecategories.constant.*;
import com.example.loantypecategories.constant.cont1.BankType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class LoanApplicationResponseDTO1 {

    private Long applicationId;
    private String applicantName;
    private String applicantEmail;
    private Gender gender;
    private BankType bankType;
    private LoanType loanType;
    private CourseType courseType;

    // Amount details
    private BigDecimal requestedAmount;
    private BigDecimal approvedAmount;

    // Interest rate breakdown
    private BigDecimal baseInterestRate;
    private BigDecimal finalInterestRate;
    private BigDecimal femaleDiscountApplied;
    private BigDecimal govtBankDiscountApplied;
    private BigDecimal totalDiscountApplied;

    // Loan terms
    private Integer tenureYears;
    private BigDecimal monthlyEmi;
    private BigDecimal processingFee;

    // Coverage breakdown
    private CoverageBreakdown coverageBreakdown;

    // Status and metadata
    private ApplicationStatus status;
    private String message;
    private String remarks;
    private boolean subsidyEligible;
    private String subsidyDetails;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Bank-specific benefits
    private BankBenefits bankBenefits;

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

    @Getter
    @Setter
    public static class BankBenefits {
        private String bankName;
        private boolean governmentBank;
        private String specialBenefits;
        private boolean subsidyAvailable;
        private String processingTimeEstimate;
    }
}