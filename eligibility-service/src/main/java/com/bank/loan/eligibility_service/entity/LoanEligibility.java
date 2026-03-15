package com.bank.loan.eligibility_service.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "loan_eligibility")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanEligibility {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Embedded
    private StudentDetails studentDetails;

    @Embedded
    private EducationDetails educationDetails;

    @Embedded
    private FinancialDetails financialDetails;

    @Embedded
    private CoApplicantDetails coApplicantDetails;

    @Embedded
    private DecisionDetails decisionDetails;

    private LocalDateTime createdAt;    //audit fields
    private LocalDateTime updatedAt;    //audit fields
    private String createdBy;           //audit field


}