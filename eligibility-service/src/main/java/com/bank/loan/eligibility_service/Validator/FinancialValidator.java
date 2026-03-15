package com.bank.loan.eligibility_service.Validator;

import com.bank.loan.eligibility_service.entity.FinancialDetails;
import com.bank.loan.eligibility_service.exception.BusinessException;

import java.util.Optional;

public class FinancialValidator {

    public void validate(FinancialDetails financial) {

        Optional.ofNullable(financial)
                .orElseThrow(() ->
                        new BusinessException("Financial details cannot be null"));

        validateCreditScore(financial);
        validateAnnualIncome(financial);
        validateCourseFees(financial);
        validateRequestedLoanAmount(financial);
        validateExistingEMI(financial);
    }

    private void validateCreditScore(FinancialDetails financial) {

        Optional.ofNullable(financial.getCreditScore())
                .filter(score -> score >= 600 && score <= 900)
                .orElseThrow(() ->
                        new BusinessException("Credit score must be between 300 and 900"));
    }

    private void validateAnnualIncome(FinancialDetails financial) {

        Optional.ofNullable(financial.getAnnualIncome())
                .filter(income -> income > 0)
                .orElseThrow(() ->
                        new BusinessException("Annual income must be greater than zero"));
    }

    private void validateCourseFees(FinancialDetails financial) {

        Optional.ofNullable(financial.getCourseFees())
                .filter(fees -> fees > 0)
                .orElseThrow(() ->
                        new BusinessException("Course fees must be greater than zero"));
    }

    private void validateRequestedLoanAmount(FinancialDetails financial) {

        Optional.ofNullable(financial.getRequestedLoanAmount())
                .filter(amount -> amount > 0)
                .orElseThrow(() ->
                        new BusinessException("Requested loan amount must be greater than zero"));
    }

    private void validateExistingEMI(FinancialDetails financial) {

        Optional.ofNullable(financial.getExistingEMI())
                .filter(emi -> emi >= 0)
                .orElseThrow(() ->
                        new BusinessException("Existing EMI cannot be negative"));
    }
}
