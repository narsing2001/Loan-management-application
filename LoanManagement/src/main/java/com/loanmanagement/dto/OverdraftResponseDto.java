package com.loanmanagement.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OverdraftResponseDto {
    private Long loanId;
    private BigDecimal usedAmount;
    private BigDecimal limit;
    private BigDecimal interest;
}
