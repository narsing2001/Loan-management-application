package com.bank.loan.eligibility_service.Validator;

import com.bank.loan.eligibility_service.entity.CoApplicantDetails;
import com.bank.loan.eligibility_service.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
@Component
public class CoApplicantValidator {

    private static final List<String> ALLOWED_RELATIONS = Arrays.asList(
            "FATHER", "MOTHER", "SPOUSE", "BROTHER", "SISTER", "GUARDIAN", "PARENT_IN_LAW"
    );


    public void validateBasic(CoApplicantDetails coApplicant){

        Optional.ofNullable(coApplicant)
                .orElseThrow(() -> new BusinessException("Co-applicant details are required"));

        Optional.ofNullable(coApplicant.getCoApplicantName())
                .map(String :: trim)
                .filter(name -> name.length() >= 3)
                .orElseThrow(()->new BusinessException("Valid co-applicant name is required"));

        Optional.ofNullable(coApplicant.getCoApplicantIncome())
                .orElseThrow(()->new BusinessException("Co-applicant income is required"));

        Optional.ofNullable(coApplicant.getCoApplicantCreditScore())
                .orElseThrow(()->new BusinessException("co-applicant credit score is required"));

    }
}

