package com.example.loantypecategories.controller.controll1;

import com.example.loantypecategories.constant.CourseType;
import com.example.loantypecategories.constant.Gender;
import com.example.loantypecategories.constant.LoanType;
import com.example.loantypecategories.constant.cont1.BankType;
import com.example.loantypecategories.entity.entit1.LoanStrategyConfig;
import com.example.loantypecategories.service.serv1.LoanStrategyConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v3/loan-strategies")
@RequiredArgsConstructor
public class LoanStrategyConfigController {

    private final LoanStrategyConfigService service;

    @GetMapping("/exact")
    public ResponseEntity<LoanStrategyConfig> getExactConfig(
            @RequestParam BankType bankType, @RequestParam LoanType loanType,
            @RequestParam CourseType courseType, @RequestParam Gender gender) {
        return ResponseEntity.ok(service.getExactConfig(bankType, loanType, courseType, gender));
    }

    @GetMapping("/bank/{bankType}")
    public ResponseEntity<List<LoanStrategyConfig>> getByBank(@PathVariable BankType bankType) {
        return ResponseEntity.ok(service.getByBank(bankType));
    }

    @GetMapping("/loan-type/{loanType}")
    public ResponseEntity<List<LoanStrategyConfig>> getByLoanType(@PathVariable LoanType loanType) {
        return ResponseEntity.ok(service.getByLoanType(loanType));
    }

    @GetMapping("/bank-loan")
    public ResponseEntity<List<LoanStrategyConfig>> getByBankAndLoanType(
            @RequestParam BankType bankType,
            @RequestParam LoanType loanType) {
        return ResponseEntity.ok(service.getByBankAndLoanType(bankType, loanType));
    }

    @GetMapping("/subsidy")
    public ResponseEntity<List<LoanStrategyConfig>> getSubsidyEligibleConfigs() {
        return ResponseEntity.ok(service.getSubsidyEligibleConfigs());
    }

    @GetMapping("/best-rate")
    public ResponseEntity<BigDecimal> getBestInterestRate(
            @RequestParam LoanType loanType,
            @RequestParam CourseType courseType) {
        return ResponseEntity.ok(service.getBestInterestRate(loanType, courseType));
    }

    @GetMapping("/govt-banks")
    public ResponseEntity<List<LoanStrategyConfig>> getGovernmentBankConfigs() {
        return ResponseEntity.ok(service.getGovernmentBankConfigs());
    }

    @GetMapping("/compare-rates")
    public ResponseEntity<List<LoanStrategyConfig>> compareRates(
            @RequestParam LoanType loanType,
            @RequestParam CourseType courseType,
            @RequestParam Gender gender) {
        return ResponseEntity.ok(service.compareRates(loanType, courseType, gender));
    }
}