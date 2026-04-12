package com.example.loantypecategories.entity.entit1;




import com.example.loantypecategories.constant.*;
import com.example.loantypecategories.constant.cont1.BankType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications")
@Getter
@Setter
public class LoanApplication1 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String applicantName;

    @Column(nullable = false, unique = true)
    private String applicantEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BankType bankType;  // NEW: Bank selection

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseType courseType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @Column(precision = 5, scale = 2)
    private BigDecimal baseInterestRate;  // Base rate before discounts

    @Column(precision = 5, scale = 2)
    private BigDecimal finalInterestRate;  // Final rate after all discounts

    @Column(precision = 5, scale = 2)
    private BigDecimal femaleDiscountApplied;  // Actual discount given

    @Column(precision = 5, scale = 2)
    private BigDecimal govtBankDiscountApplied;  // Government bank discount

    @Column(precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    private Integer tenureYears;

    @Column(precision = 10, scale = 2)
    private BigDecimal monthlyEmi;

    @Column(precision = 15, scale = 2)
    private BigDecimal processingFee;

    // Coverage breakdown
    @Column(precision = 15, scale = 2)
    private BigDecimal tuitionFeeCoverage;

    @Column(precision = 15, scale = 2)
    private BigDecimal accommodationCoverage;

    @Column(precision = 15, scale = 2)
    private BigDecimal travelExpenseCoverage;

    @Column(precision = 15, scale = 2)
    private BigDecimal laptopExpenseCoverage;

    @Column(precision = 15, scale = 2)
    private BigDecimal examFeeCoverage;

    @Column(precision = 15, scale = 2)
    private BigDecimal otherExpenseCoverage;

    // Verification flags
    private boolean admissionLetterVerified;
    private boolean institutionApproved;
    private boolean collateralProvided;

    @Column(precision = 15, scale = 2)
    private BigDecimal collateralValue;

    // Subsidy information
    private boolean subsidyEligible;

    @Column(columnDefinition = "TEXT")
    private String subsidyDetails;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = ApplicationStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}