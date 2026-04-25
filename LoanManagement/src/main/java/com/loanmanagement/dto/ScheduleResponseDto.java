package com.loanmanagement.dto;

import lombok.Data;

import java.util.List;

@Data
public class ScheduleResponseDto {
    private Long loanId;
    private List<RepaymentScheduleDto> emis;
}
