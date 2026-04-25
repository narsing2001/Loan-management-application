package com.bank.loan.eligibility_service.Calculator;

import com.bank.loan.eligibility_service.entity.CoApplicantDetails;
import com.bank.loan.eligibility_service.entity.FinancialDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FOIRCalculator {

    public double calculate(CoApplicantDetails coApplicant, FinancialDetails financial){

        double income = Optional.ofNullable(coApplicant)
                .map(CoApplicantDetails::getCoApplicantIncome)
                .orElse(0.0);

        double monthlyIncome = income / 12;

        double emi = Optional.ofNullable(financial.getExistingEMI()).orElse(0.0);

        return monthlyIncome == 0 ? 100 : (emi / monthlyIncome) * 100;
    }

}
