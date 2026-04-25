package com.bank.loan.eligibility_service.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDetails {

    private Boolean panAvailable;
    private Boolean aadhaarAvailable;
    private Boolean identityProof;
    private Boolean addressProof;


    private Boolean salarySlips;
    private Boolean bankStatement;
    private Boolean itr;
    private Boolean balanceSheet;
    private Boolean admissionLetter;
    private Boolean academicRecords;
    private Boolean applicationForm;
    private Boolean photograph;

}
