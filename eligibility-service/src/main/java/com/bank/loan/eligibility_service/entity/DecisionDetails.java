package com.bank.loan.eligibility_service.entity;

import com.bank.loan.eligibility_service.enums.RiskCategory;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecisionDetails {
    @Enumerated(EnumType.STRING)
    private RiskCategory riskCategory; // better → enum
    private Boolean eligible;
    private String rejectionReason;
    private LocalDate decisionDate;
}
