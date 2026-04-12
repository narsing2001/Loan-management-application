package com.example.LoanTypeStrategyInterest.dto.dto1.mockdto;

import com.example.LoanTypeStrategyInterest.dto.dto1.InterestRateCalculationRequestDTO;
import com.example.LoanTypeStrategyInterest.dto.dto1.InterestRateCalculationResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class InterestRateCalculationMockRequestDTO extends InterestRateCalculationRequestDTO {
    private BigDecimal approvedAmount;
    private BigDecimal monthlyEmi;
    private Integer tenureYears;
}
