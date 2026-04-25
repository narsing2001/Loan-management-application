package com.loanmanagement.dto;

import lombok.Data;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Data
public class PaymentRequestDto {

    @NotNull(message = "LoanId is required")
    @Positive(message = "LoanId must be greater than 0")
    private Long loanId;

    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Positive(message = "Month must be greater than 0")
    private int month;
}

