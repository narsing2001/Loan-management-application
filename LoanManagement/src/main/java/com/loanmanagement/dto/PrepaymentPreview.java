package com.loanmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrepaymentPreview {

    private BigDecimal outstanding;
    private BigDecimal charges;
    private BigDecimal totalPayable;
}
