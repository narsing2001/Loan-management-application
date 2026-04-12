package com.example.LoanTypeStrategyInterest.dto.dto1.mockdto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class InterestRateCalculationMockResponseDTO extends InterestRateCalculationMockRequestDTO {
    private BigDecimal interestRate;
    private String calculationMethod;
    private BigDecimal principalAmount;
    private BigDecimal monthlyEmi;
    private Integer tenureYears;
}
