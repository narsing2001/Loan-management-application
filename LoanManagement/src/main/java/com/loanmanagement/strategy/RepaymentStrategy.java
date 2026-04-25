package com.loanmanagement.strategy;

import com.loanmanagement.entity.RepaymentSchedule;

import java.math.BigDecimal;
import java.util.List;

public interface RepaymentStrategy {

    void apply(List<RepaymentSchedule> list,
               Long loanId,
               int month,
               BigDecimal amount);
}