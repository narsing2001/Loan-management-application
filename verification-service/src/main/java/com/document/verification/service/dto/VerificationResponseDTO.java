package com.document.verification.service.dto;

import com.document.verification.service.constant.DocumentType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationResponseDTO {
    private UUID documentId;
    private Long applicantId;
    private DocumentType documentType;
    private String verificationStatus;
    private String extractedName;
    private String extractedDob;
    private String extractedNumber;
    private Instant verifiedAt;
}