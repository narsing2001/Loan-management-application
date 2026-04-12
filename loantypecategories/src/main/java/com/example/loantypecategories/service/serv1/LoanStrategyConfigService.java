package com.example.loantypecategories.service.serv1;

import com.example.loantypecategories.constant.CourseType;
import com.example.loantypecategories.constant.Gender;
import com.example.loantypecategories.constant.LoanType;
import com.example.loantypecategories.constant.cont1.BankType;
import com.example.loantypecategories.entity.entit1.LoanStrategyConfig;
import com.example.loantypecategories.repository.repo1.LoanStrategyConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanStrategyConfigService {

    private final LoanStrategyConfigRepository repository;

    public LoanStrategyConfig getExactConfig(BankType bankType, LoanType loanType,
                                             CourseType courseType, Gender gender) {
        return repository.findByBankTypeAndLoanTypeAndCourseTypeAndGenderAndActiveTrue(
                bankType, loanType, courseType, gender
        ).orElseThrow(() -> new RuntimeException("No matching strategy config found"));
    }

    public List<LoanStrategyConfig> getByBank(BankType bankType) {
        return repository.findByBankTypeAndActiveTrue(bankType);
    }

    public List<LoanStrategyConfig> getByLoanType(LoanType loanType) {
        return repository.findByLoanTypeAndActiveTrue(loanType);
    }

    public List<LoanStrategyConfig> getByBankAndLoanType(BankType bankType, LoanType loanType) {
        return repository.findByBankTypeAndLoanTypeAndActiveTrue(bankType, loanType);
    }

    public List<LoanStrategyConfig> getSubsidyEligibleConfigs() {
        return repository.findSubsidyEligibleConfigs();
    }

    public BigDecimal getBestInterestRate(LoanType loanType, CourseType courseType) {
        return repository.findBestInterestRate(loanType, courseType)
                .orElseThrow(() -> new RuntimeException("No configs found for given parameters"));
    }

    public List<LoanStrategyConfig> getGovernmentBankConfigs() {
        return repository.findGovernmentBankConfigs();
    }

    public List<LoanStrategyConfig> compareRates(LoanType loanType, CourseType courseType, Gender gender) {
        return repository.findBestRatesForComparison(loanType, courseType, gender);
    }
}