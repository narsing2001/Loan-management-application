package com.bank.loan.eligibility_service.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoApplicantDetails {

    private Boolean coApplicationPresent;
    private String coApplicantName;
    private String coApplicantRelation;
    private Double coApplicantIncome;
    private Integer coApplicantCreditScore;
}
