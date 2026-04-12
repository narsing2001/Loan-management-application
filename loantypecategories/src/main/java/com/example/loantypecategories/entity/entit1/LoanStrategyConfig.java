package com.example.loantypecategories.entity.entit1;

import com.example.loantypecategories.constant.cont1.BankType;
import com.example.loantypecategories.constant.CourseType;
import com.example.loantypecategories.constant.Gender;
import com.example.loantypecategories.constant.LoanType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Configuration entity that stores bank-specific, loan-type-specific,
 * gender-specific interest rates and loan parameters
 */
@Entity
@Table(name = "loan_strategy_configs",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"bank_type", "loan_type", "course_type", "gender"}
        ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanStrategyConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "bank_type", nullable = false)
    private BankType bankType;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false)
    private LoanType loanType;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_type", nullable = false)
    private CourseType courseType;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;


    @Column(name = "base_interest_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal baseInterestRate;

    @Column(name = "female_discount", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal femaleDiscount = BigDecimal.ZERO;

    @Column(name = "govt_bank_discount", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal govtBankDiscount = BigDecimal.ZERO;


    @Column(name = "min_loan_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal minLoanAmount;

    @Column(name = "max_loan_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal maxLoanAmount;


    @Column(name = "min_tenure_years", nullable = false)
    private Integer minTenureYears;

    @Column(name = "max_tenure_years", nullable = false)
    private Integer maxTenureYears;

    // Collateral Configuration
    @Column(name = "collateral_required")
    @Builder.Default
    private Boolean collateralRequired = false;

    @Column(name = "collateral_threshold", precision = 15, scale = 2)
    private BigDecimal collateralThreshold;

    // Processing Fee
    @Column(name = "processing_fee_percent", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal processingFeePercent = new BigDecimal("2.00");

    // Coverage Ratios (percentage of approved amount allocated to each expense)
    @Column(name = "tuition_coverage_ratio", precision = 5, scale = 2)
    private BigDecimal tuitionCoverageRatio;

    @Column(name = "accommodation_coverage_ratio", precision = 5, scale = 2)
    private BigDecimal accommodationCoverageRatio;

    @Column(name = "travel_coverage_ratio", precision = 5, scale = 2)
    private BigDecimal travelCoverageRatio;

    @Column(name = "laptop_coverage_ratio", precision = 5, scale = 2)
    private BigDecimal laptopCoverageRatio;

    @Column(name = "exam_fee_coverage_ratio", precision = 5, scale = 2)
    private BigDecimal examFeeCoverageRatio;

    @Column(name = "other_coverage_ratio", precision = 5, scale = 2)
    private BigDecimal otherCoverageRatio;

    // Additional Features
    @Column(name = "subsidy_eligible")
    @Builder.Default
    private Boolean subsidyEligible = false;

    @Column(name = "moratorium_months")
    private Integer moratoriumMonths;

    @Column(name = "special_benefits", columnDefinition = "TEXT")
    private String specialBenefits;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Calculate final interest rate applying all discounts
     */
    public BigDecimal calculateFinalInterestRate() {
        BigDecimal finalRate = baseInterestRate;

        if (gender == Gender.FEMALE && femaleDiscount != null) {
            finalRate = finalRate.subtract(femaleDiscount);
        }

        if (bankType.isGovernmentBank() && govtBankDiscount != null) {
            finalRate = finalRate.subtract(govtBankDiscount);
        }

        return finalRate;
    }
}