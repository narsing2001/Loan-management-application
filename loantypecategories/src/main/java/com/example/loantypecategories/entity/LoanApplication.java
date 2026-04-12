package com.example.loantypecategories.entity;


import com.example.loantypecategories.constant.ApplicationStatus;
import com.example.loantypecategories.constant.CourseType;
import com.example.loantypecategories.constant.Gender;
import com.example.loantypecategories.constant.LoanType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications")
@Getter
@Setter
public class LoanApplication {

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
    private LoanType loanType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseType courseType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @Column(precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(precision = 15, scale = 2)
    private BigDecimal approvedAmount;

    private Integer tenureYears;

    @Column(precision = 10, scale = 2)
    private BigDecimal monthlyEmi;

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

    private boolean admissionLetterVerified;


    private boolean institutionApproved;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = ApplicationStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


}
