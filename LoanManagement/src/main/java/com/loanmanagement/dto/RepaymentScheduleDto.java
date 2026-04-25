package com.loanmanagement.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RepaymentScheduleDto {

    private int month;
    private BigDecimal emi;
    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal balance;
    private boolean paid;
    private boolean holiday;
    private LocalDate dueDate;
}
