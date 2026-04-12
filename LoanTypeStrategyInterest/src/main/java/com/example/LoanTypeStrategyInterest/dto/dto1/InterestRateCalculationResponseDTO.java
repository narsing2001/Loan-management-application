package com.example.LoanTypeStrategyInterest.dto.dto1;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestRateCalculationResponseDTO {

    private BigDecimal approvedAmount;
    private BigDecimal monthlyEmi;
    private Integer tenureYears;
    private Integer totalMonths;
    private BigDecimal calculatedAnnualInterestRate;
    private BigDecimal calculatedMonthlyInterestRate;
    private BigDecimal totalPayment;
    private BigDecimal totalInterestPaid;
    private String calculationMethod;


}