package com.loanmanagement.service;

import com.loanmanagement.dto.OverdraftResponseDto;
import java.math.BigDecimal;

public interface OverdraftService {

    void createOD(Long loanId, BigDecimal loanAmount);

    void withdraw(Long loanId, BigDecimal amount);

    void repay(Long loanId, BigDecimal amount);

    BigDecimal calculateInterest(Long loanId);

    OverdraftResponseDto getOD(Long loanId);
}