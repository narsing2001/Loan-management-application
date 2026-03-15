package com.document.verification.service.dto;

import lombok.Data;

@Data
public class AadhaarVerifyResponse {

    private boolean valid;
    private String message;

}
