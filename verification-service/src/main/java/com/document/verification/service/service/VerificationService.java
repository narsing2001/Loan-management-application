package com.document.verification.service.service;

import com.document.verification.service.dto.DocumentUploadedEventDTO;
import com.document.verification.service.dto.VerificationResponseDTO;

import java.util.UUID;

public interface VerificationService {

    void verifyDocument(DocumentUploadedEventDTO event);

    VerificationResponseDTO getVerificationStatus(UUID documentId);

}
