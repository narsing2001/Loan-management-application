package com.bank.loan.eligibility_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EligibilityResponseDTO {

private Boolean eligible;
private String riskCategory;
private Double foir;
private Double ltvRatio;
private Double maxEligibleAmount;
private String message;

}
