package com.loanmanagement.controller;

import com.loanmanagement.constants.MessageConstants;
import com.loanmanagement.dto.*;
import com.loanmanagement.enums.Status;
import com.loanmanagement.service.RepaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/repayment")
@RequiredArgsConstructor
@Validated
public class RepaymentController {

    private final RepaymentService service;

    //Generate Schedule
    @PostMapping("/generate/{loanId}")
    public ResponseEntity<ApiResponseDto> generate(
            @PathVariable @NotNull @Positive Long loanId) {

        service.generateSchedule(loanId);

        return ResponseEntity.ok(new ApiResponseDto(MessageConstants.SCHEDULE_GENERATED,Status.SUCCESS,LocalDate.now()));
    }

    //Get Full Schedule
    @GetMapping("/{loanId}")
    public ResponseEntity<ScheduleResponseDto> getSchedule(
            @PathVariable @NotNull @Positive Long loanId) {

        ScheduleResponseDto schedule = service.getSchedule(loanId);
        return ResponseEntity.ok(schedule);
    }


    //Get EMI by Month
    @GetMapping("/{loanId}/{month}")
    public ResponseEntity<RepaymentScheduleDto> getEmi(
            @PathVariable @NotNull @Positive Long loanId,
            @PathVariable @Positive int month) {

        RepaymentScheduleDto emi = service.getEmi(loanId, month);
        return ResponseEntity.ok(emi);
    }

    //Pay EMI
    @PostMapping("/pay")
    public ResponseEntity<ApiResponseDto> pay(
            @RequestParam @NotNull @Positive Long loanId,
            @RequestParam @Positive int month,
            @RequestParam @NotNull @Positive BigDecimal amount) {

        service.payEmi(loanId, month, amount);

        return ResponseEntity.ok(new ApiResponseDto(MessageConstants.PAYMENT_SUCCESS,Status.SUCCESS, LocalDate.now()));
    }

    //Part Payment
    @PostMapping("/part")
    public ResponseEntity<ApiResponseDto> part(
            @Valid @RequestBody PaymentRequestDto dto) {

        service.partPayment(dto);

        return ResponseEntity.ok(new ApiResponseDto(MessageConstants.PART_PAYMENT_SUCCESS,Status.SUCCESS,LocalDate.now()));
    }

    //Holiday
    @PostMapping("/holiday")
    public ResponseEntity<ApiResponseDto> holiday(
            @RequestParam @NotNull @Positive Long loanId,
            @RequestParam @Positive int month) {

        service.markHoliday(loanId, month);

        return ResponseEntity.ok(
                new ApiResponseDto(MessageConstants.HOLIDAY_APPLIED,Status.SUCCESS,LocalDate.now()));
    }

    //Prepayment Preview
    @GetMapping("/prepay-preview/{loanId}")
    public ResponseEntity<PrepaymentPreview> preview(
            @PathVariable @NotNull @Positive Long loanId) {

        PrepaymentPreview response = service.prepaymentPreview(loanId);

        return ResponseEntity.ok(response);
    }

    //Prepayment
    @PostMapping("/prepay/{loanId}")
    public ResponseEntity<ApiResponseDto> prepay(
            @PathVariable @NotNull @Positive Long loanId) {

        service.prepayment(loanId);

        return ResponseEntity.ok(new ApiResponseDto(MessageConstants.LOAN_CLOSED,Status.SUCCESS,LocalDate.now()));
    }
}