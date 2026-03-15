package com.bank.loan.eligibility_service.Validator;

import com.bank.loan.eligibility_service.entity.CoApplicantDetails;
import com.bank.loan.eligibility_service.exception.BusinessException;

import java.util.Optional;

public class CoApplicantValidator {

    public void validate(CoApplicantDetails coApplicant){

        if(Boolean.TRUE.equals(coApplicant.getCoApplicationPresent())){

            Optional.ofNullable(coApplicant.getCoApplicantIncome())
                    .filter(income -> income > 0)
                    .orElseThrow(() ->
                            new BusinessException("Co applicant income must be greater than zero"));

            Optional.ofNullable(coApplicant.getCoApplicantCreditScore())
                    .filter(score -> score >= 300 && score <= 900)
                    .orElseThrow(() ->
                            new BusinessException("Invalid co applicant credit score"));
        }
    }


}
