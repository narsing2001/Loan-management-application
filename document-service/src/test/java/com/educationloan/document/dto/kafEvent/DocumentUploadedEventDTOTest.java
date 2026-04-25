package com.educationloan.document.dto.kafEvent;
import com.educationloan.document.enumConst.DocumentType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DocumentUploadedEventDTOTest {
    @Test
    void testBuilder() {
        UUID docId = UUID.randomUUID();
        Instant now = Instant.now();
        DocumentUploadedEventDTO dto = DocumentUploadedEventDTO.builder()
                .documentId(docId)
                .applicantId(1L)
                .loanId(100L)
                .documentType(DocumentType.AADHAAR)
                .filename("file.pdf")
                .s3Key("s3/key")
                .contentType("application/pdf")
                .fileSize(500L)
                .uploadedAt(now)
                .build();

        assertEquals(docId, dto.getDocumentId());
        assertEquals(1L, dto.getApplicantId());
        assertEquals(100L, dto.getLoanId());
        assertEquals(DocumentType.AADHAAR, dto.getDocumentType());
        assertEquals("file.pdf", dto.getFilename());
        assertEquals("s3/key", dto.getS3Key());
        assertEquals("application/pdf", dto.getContentType());
        assertEquals(500L, dto.getFileSize());
        assertEquals(now, dto.getUploadedAt());
    }

    @Test
    void testAllArgsConstructor() {
        UUID docId = UUID.randomUUID();
        Instant now = Instant.now();
        DocumentUploadedEventDTO dto = new DocumentUploadedEventDTO(
                docId,
                2L,
                200L,
                DocumentType.PAN,
                "pan.pdf",
                "s3/pan",
                "application/pdf",
                300L,
                now);
        assertEquals("pan.pdf", dto.getFilename());
        assertEquals("s3/pan", dto.getS3Key());
        assertEquals(DocumentType.PAN, dto.getDocumentType());
    }

    @Test
    void testSettersAndGetters() {
        DocumentUploadedEventDTO dto = new DocumentUploadedEventDTO();
        dto.setApplicantId(3L);
        dto.setLoanId(300L);
        dto.setFilename("test.pdf");
        dto.setS3Key("key123");
        dto.setContentType("pdf");
        dto.setFileSize(700L);

        assertEquals(3L, dto.getApplicantId());
        assertEquals("test.pdf", dto.getFilename());
        assertEquals("key123", dto.getS3Key());
    }

    @Test
    void testNullValues() {
        DocumentUploadedEventDTO dto = DocumentUploadedEventDTO.builder()
                .filename(null)
                .s3Key(null)
                .build();
        assertNull(dto.getFilename());
        assertNull(dto.getS3Key());
    }
}