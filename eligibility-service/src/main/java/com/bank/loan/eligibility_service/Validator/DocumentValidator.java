package com.bank.loan.eligibility_service.Validator;

import com.bank.loan.eligibility_service.entity.DocumentDetails;
import com.bank.loan.eligibility_service.enums.LoanType;
import com.bank.loan.eligibility_service.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DocumentValidator {

    public void validate(DocumentDetails document, LoanType loanType) {

        DocumentDetails doc = Optional.ofNullable(document)
                .orElseThrow(() -> new BusinessException("Documents required"));


        validateField(doc.getPanAvailable(), "PAN required");
        validateField(doc.getAadhaarAvailable(), "Aadhaar required");
        validateField(doc.getIdentityProof(), "Identity proof required");


        if (loanType == LoanType.INTERNATIONAL) {
            validateField(doc.getAdmissionLetter(), "Admission letter required");
            validateField(doc.getAcademicRecords(), "Academic records required");
        }
    }

    private void validateField(Boolean field,String errormessaage) {
        Optional.ofNullable(field)
                .filter(Boolean::booleanValue)
                .orElseThrow(() -> new BusinessException("fiels is invalid"));
    }
}