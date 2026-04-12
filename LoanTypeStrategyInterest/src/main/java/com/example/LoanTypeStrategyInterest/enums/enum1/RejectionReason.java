package com.example.LoanTypeStrategyInterest.enums.enum1;

import lombok.Getter;

@Getter
public enum RejectionReason {
    ADMISSION_NOT_VERIFIED("ERR_ADM_001", "Admission letter not verified."),
    INSTITUTION_NOT_APPROVED("ERR_INST_001", "Institution not approved by UGC or AICTE."),
    AMOUNT_EXCEEDS_LIMIT("ERR_AMT_001", "Requested amount exceeds permissible limit."),
    COURSE_NOT_ELIGIBLE("ERR_CRS_001", "Course type not eligible for loan."),
    OTHER("ERR_GEN_001", "Application rejected due to policy criteria.");

    private final String code;
    private final String message;

    RejectionReason(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
