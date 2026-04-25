package com.document.verification.service.dto;
import com.document.verification.service.constant.DocumentType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentUploadedEventDTOTest {

    @Test
    void testAllArgsConstructor() {
        UUID documentId = UUID.randomUUID();
        Long applicantId = 1L;
        Long loanId = 100L;
        DocumentType documentType = DocumentType.AADHAAR;
        String filename = "file.pdf";
        String s3Key = "s3/key";
        String contentType = "application/pdf";
        Long fileSize = 1024L;
        Instant uploadedAt = Instant.now();

        DocumentUploadedEventDTO dto =
                new DocumentUploadedEventDTO(
                        documentId,
                        applicantId,
                        loanId,
                        documentType,
                        filename,
                        s3Key,
                        contentType,
                        fileSize,
                        uploadedAt
                );

        assertEquals(documentId, dto.getDocumentId());
        assertEquals(applicantId, dto.getApplicantId());
        assertEquals(loanId, dto.getLoanId());
        assertEquals(documentType, dto.getDocumentType());
        assertEquals(filename, dto.getFilename());
        assertEquals(s3Key, dto.getS3Key());
        assertEquals(contentType, dto.getContentType());
        assertEquals(fileSize, dto.getFileSize());
        assertEquals(uploadedAt, dto.getUploadedAt());
    }

    @Test
    void testBuilderMethodsExplicitly() {
        DocumentUploadedEventDTO.DocumentUploadedEventDTOBuilder builder =
                DocumentUploadedEventDTO.builder();

        builder.documentId(UUID.randomUUID());
        builder.applicantId(1L);
        builder.loanId(2L);
        builder.documentType(DocumentType.AADHAAR);
        builder.filename("file.pdf");
        builder.s3Key("s3/key");
        builder.contentType("application/pdf");
        builder.fileSize(100L);
        builder.uploadedAt(Instant.now());

        DocumentUploadedEventDTO dto = builder.build();

        assertNotNull(dto);
    }
    @Test
    void testSettersAndGetters() {
        DocumentUploadedEventDTO dto = new DocumentUploadedEventDTO();

        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        dto.setDocumentId(id);
        dto.setApplicantId(10L);
        dto.setLoanId(20L);
        dto.setDocumentType(DocumentType.AADHAAR);
        dto.setFilename("abc.pdf");
        dto.setS3Key("s3/abc");
        dto.setContentType("application/pdf");
        dto.setFileSize(5000L);
        dto.setUploadedAt(now);

        assertEquals(id, dto.getDocumentId());
        assertEquals(10L, dto.getApplicantId());
        assertEquals(20L, dto.getLoanId());
        assertEquals(DocumentType.AADHAAR, dto.getDocumentType());
        assertEquals("abc.pdf", dto.getFilename());
        assertEquals("s3/abc", dto.getS3Key());
        assertEquals("application/pdf", dto.getContentType());
        assertEquals(5000L, dto.getFileSize());
        assertEquals(now, dto.getUploadedAt());
    }
}

