package com.document.verification.service.client;

import com.document.verification.service.dto.AadhaarVerifyRequest;
import com.document.verification.service.dto.AadhaarVerifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

    @FeignClient(name = "aadhaarClient", url = "${aadhaar.api.url}")
    public interface AadhaarVerificationClient {
        @PostMapping("/aadhaar/verify")
        AadhaarVerifyResponse verifyAadhaar(
                @RequestHeader("Authorization") String token,
                @RequestBody AadhaarVerifyRequest request
        );
    }

