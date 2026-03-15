package com.bank.loan.eligibility_service.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialDetails {

    private Integer creditScore;
    private Double annualIncome;
    private Double courseFees;
    private Double requestedLoanAmount;
    private Double existingEMI;
    private Double foir;
    private Double ltvRatio;
    private Double maxEligibleAmount;
}
