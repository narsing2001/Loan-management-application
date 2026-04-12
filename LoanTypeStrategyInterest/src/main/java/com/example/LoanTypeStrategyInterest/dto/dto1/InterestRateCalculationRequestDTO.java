package com.example.LoanTypeStrategyInterest.dto.dto1;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestRateCalculationRequestDTO {

    @NotNull(message = "Approved amount is required")
    @DecimalMin(value = "1.00", message = "Approved amount must be greater than 0")
    private BigDecimal approvedAmount;

    @NotNull(message = "Monthly EMI is required")
    @DecimalMin(value = "1.00", message = "Monthly EMI must be greater than 0")
    private BigDecimal monthlyEmi;

    @NotNull(message = "Tenure in years is required")
    @Min(value = 1, message = "Tenure must be at least 1 year")
    @Max(value = 20, message = "Tenure cannot exceed 30 years")
    private Integer tenureYears;
}