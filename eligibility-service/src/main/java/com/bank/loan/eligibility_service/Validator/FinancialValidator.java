package com.bank.loan.eligibility_service.Validator;

import com.bank.loan.eligibility_service.entity.FinancialDetails;
import com.bank.loan.eligibility_service.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FinancialValidator {

    public void validate(FinancialDetails financial) {

        Optional.ofNullable(financial)
                .orElseThrow(() ->
                        new BusinessException("Financial details cannot be null"));

        validateCreditScore(financial);
        validateCourseFees(financial);
        validateExistingEMI(financial);
        validateLoanToValue(financial);
    }

    private void validateCreditScore(FinancialDetails financial) {

        Optional.ofNullable(financial.getCreditScore())
                .filter(score -> score >= 300 && score <= 900)
                .orElseThrow(() ->
                        new BusinessException("Credit score must be between 300 and 900"));
    }

    private void validateCourseFees(FinancialDetails financial) {

        Optional.ofNullable(financial.getCourseFees())
                .filter(fees -> fees > 0)
                .orElseThrow(() ->
                        new BusinessException("Course fees must be greater than zero"));
    }

    private void validateExistingEMI(FinancialDetails financial) {

        Optional.ofNullable(financial.getExistingEMI())
                .filter(emi -> emi >= 0)
                .orElseThrow(() ->
                        new BusinessException("Existing EMI cannot be negative"));
    }

    private void validateLoanToValue(FinancialDetails financial) {

        Optional.ofNullable(financial.getCourseFees())
                .flatMap(fees ->
                        Optional.ofNullable(financial.getRequestedLoanAmount())
                                .filter(loan -> loan <= fees * 0.9)
                )
                .orElseThrow(() ->
                        new BusinessException("Requested loan exceeds 90% of course fees or values are null"));
    }
}