package com.document.verification.service.controller;

import com.document.verification.service.dto.AadhaarVerifyRequest;
import com.document.verification.service.dto.AadhaarVerifyResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aadhaar")
public class MockAadhaarController {

    @PostMapping("/verify")
    public AadhaarVerifyResponse verify(@RequestBody AadhaarVerifyRequest request) {

        String aadhaar = request.getAadhaarNumber();

        AadhaarVerifyResponse response = new AadhaarVerifyResponse();

        if (aadhaar != null && aadhaar.matches("\\d{12}")) {

            response.setValid(true);
            response.setMessage("Aadhaar valid");

        } else {
            response.setValid(false);
            response.setMessage("Invalid Aadhaar");
        }
        return response;
    }
}