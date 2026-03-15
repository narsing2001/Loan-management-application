package com.bank.loan.eligibility_service.Calculator;

import com.bank.loan.eligibility_service.entity.FinancialDetails;

import java.util.Optional;

public class FOIRCalculator {

    public double calculate(FinancialDetails financial){

        return Optional.ofNullable(financial.getAnnualIncome())
                .map(income -> income / 12)
                .map(monthlyIncome -> {

                    double emi = Optional.ofNullable(financial.getExistingEMI()).orElse(0.0);

                    return monthlyIncome == 0 ? 100 : (emi / monthlyIncome) * 100;

                })
                .orElse(100.0);
    }

}
