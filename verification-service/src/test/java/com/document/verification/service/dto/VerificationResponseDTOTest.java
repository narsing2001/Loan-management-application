package com.document.verification.service.dto;
import com.document.verification.service.constant.DocumentType;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VerificationResponseDTOTest {

    @Test
    void testBuilder() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        VerificationResponseDTO dto = VerificationResponseDTO.builder()
                .documentId(id)
                .applicantId(1L)
                .documentType(DocumentType.AADHAAR)
                .verificationStatus("VERIFIED")
                .verifiedAt(now)
                .build();

        assertEquals(id, dto.getDocumentId());
        assertEquals("VERIFIED", dto.getVerificationStatus());
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        VerificationResponseDTO dto =
                new VerificationResponseDTO(id, 1L, DocumentType.AADHAAR,
                        "VERIFIED", "John", "2000-01-01",
                        "1234", now);

        assertEquals("John", dto.getExtractedName());
    }
}
