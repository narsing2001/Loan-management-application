package com.loanmanagement.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanDto {

    private Long loanId;
    private BigDecimal principal;
    private BigDecimal interestRate;
    private int tenure;
    private int moratorium;
    private LocalDate startDate;
    private int lockInMonths;
}
