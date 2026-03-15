package com.document.verification.service.controller;

import com.document.verification.service.dto.VerificationResponseDTO;
import com.document.verification.service.service.VerificationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationServiceImpl verificationService;

    @GetMapping("/{documentId}")
    public VerificationResponseDTO getVerificationStatus(
            @PathVariable UUID documentId) {
        return verificationService.getVerificationStatus(documentId);
    }
}
