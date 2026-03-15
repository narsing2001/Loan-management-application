package com.bank.loan.eligibility_service.Calculator;

import com.bank.loan.eligibility_service.entity.FinancialDetails;

import java.util.Optional;

public class LTVCalculator {

    public double calculate(FinancialDetails financial){

        return Optional.ofNullable(financial.getCourseFees())
                .filter(f -> f > 0)
                .map(fees -> {

                    double loan =
                            Optional.ofNullable(financial.getRequestedLoanAmount()).orElse(0.0);

                    return (loan / fees) * 100;

                })
                .orElse(0.0);
    }

}
