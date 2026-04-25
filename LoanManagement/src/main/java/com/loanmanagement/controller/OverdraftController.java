package com.loanmanagement.controller;

import com.loanmanagement.constants.MessageConstants;
import com.loanmanagement.dto.ApiResponseDto;
import com.loanmanagement.dto.OverdraftResponseDto;
import com.loanmanagement.enums.Status;
import com.loanmanagement.service.OverdraftService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/overdraft")
@RequiredArgsConstructor
@Validated
public class OverdraftController {

    private final OverdraftService service;

    //Get Overdraft Details
    @GetMapping("/{loanId}")
    public ResponseEntity<OverdraftResponseDto> getOD(
            @PathVariable @NotNull @Positive Long loanId) {

        OverdraftResponseDto overdraft = service.getOD(loanId);
        return ResponseEntity.ok(overdraft);
    }

    //Withdraw Amount
    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponseDto> withdraw(
            @RequestParam @NotNull @Positive Long loanId,
            @RequestParam @NotNull @Positive BigDecimal amount) {

        service.withdraw(loanId, amount);
        return ResponseEntity.ok(new ApiResponseDto(MessageConstants.WITHDRAW_SUCCESS, Status.SUCCESS, LocalDate.now()));
    }

    //Repay Amount
    @PostMapping("/repay")
    public ResponseEntity<ApiResponseDto> repay(
            @RequestParam @NotNull @Positive Long loanId,
            @RequestParam @NotNull @Positive BigDecimal amount) {

        service.repay(loanId, amount);
        return ResponseEntity.ok(new ApiResponseDto(MessageConstants.REPAY_SUCCESS, Status.SUCCESS, LocalDate.now()));
    }

    //Calculate Interest
    @GetMapping("/interest/{loanId}")
    public ResponseEntity<BigDecimal> interest(
            @PathVariable @NotNull @Positive Long loanId) {

        BigDecimal interest = service.calculateInterest(loanId);
        return ResponseEntity.ok(interest);
    }
}
