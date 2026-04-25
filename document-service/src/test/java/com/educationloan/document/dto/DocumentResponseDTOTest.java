package com.educationloan.document.dto;
import com.educationloan.document.enumConst.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DocumentResponseDTOTest {
    @Test
    void testSettersAndGetters() {
        DocumentResponseDTO dto = new DocumentResponseDTO();
        UUID id = UUID.randomUUID();
        dto.setId(id);
        dto.setFilename("file.pdf");
        dto.setContentType("application/pdf");
        dto.setFileSize(100L);
        dto.setDocType(DocumentType.AADHAAR);
        dto.setStatus(DocumentStatus.UPLOADED);
        dto.setApplicantType(ApplicantType.STUDENT);
        dto.setUploadedAt(Instant.now());
        dto.setDownloadUrl("url");

        assertEquals(id, dto.getId());
        assertEquals("file.pdf", dto.getFilename());
        assertEquals("url", dto.getDownloadUrl());
    }
}
