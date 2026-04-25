package com.loanmanagement.service;

import com.loanmanagement.dto.PaymentRequestDto;
import com.loanmanagement.dto.PrepaymentPreview;
import com.loanmanagement.dto.RepaymentScheduleDto;
import com.loanmanagement.dto.ScheduleResponseDto;

import java.math.BigDecimal;

public interface RepaymentService {

    void generateSchedule(Long loanId);

    void payEmi(Long loanId, int month, BigDecimal amount);

    void markHoliday(Long loanId, int month);

    void partPayment(PaymentRequestDto dto);

    PrepaymentPreview prepaymentPreview(Long loanId);

    void prepayment(Long loanId);

    ScheduleResponseDto getSchedule(Long loanId);

    RepaymentScheduleDto getEmi(Long loanId, int month);
}