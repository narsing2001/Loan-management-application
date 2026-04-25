package com.educationloan.document.entity;
import com.educationloan.document.enumConst.DocumentStatus;
import com.educationloan.document.enumConst.DocumentType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DocumentEntityTest {
    @Test
    void testBuilder() {
        ApplicantEntity applicant = new ApplicantEntity();
        applicant.setId(1L);
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        DocumentEntity entity = DocumentEntity.builder()
                .id(id)
                .applicant(applicant)
                .docType(DocumentType.AADHAAR)
                .status(DocumentStatus.UPLOADED)
                .filename("file.pdf")
                .s3Key("s3/key")
                .contentType("application/pdf")
                .fileSize(100L)
                .uploadedAt(now)
                .build();

        assertEquals(id, entity.getId());
        assertEquals(applicant, entity.getApplicant());
        assertEquals(DocumentType.AADHAAR, entity.getDocType());
        assertEquals(DocumentStatus.UPLOADED, entity.getStatus());
        assertEquals("file.pdf", entity.getFilename());
        assertEquals("s3/key", entity.getS3Key());
        assertEquals("application/pdf", entity.getContentType());
        assertEquals(100L, entity.getFileSize());
        assertEquals(now, entity.getUploadedAt());
    }

    @Test
    void testSettersAndGetters() {
        DocumentEntity entity = new DocumentEntity();
        ApplicantEntity applicant = new ApplicantEntity();
        applicant.setId(2L);

        entity.setId(UUID.randomUUID());
        entity.setApplicant(applicant);
        entity.setDocType(DocumentType.PAN);
        entity.setStatus(DocumentStatus.UPLOADED);
        entity.setFilename("test.pdf");
        entity.setS3Key("key123");
        entity.setContentType("application/pdf");
        entity.setFileSize(500L);
        entity.setUploadedAt(Instant.now());

        assertEquals("test.pdf", entity.getFilename());
        assertEquals("key123", entity.getS3Key());
        assertEquals(DocumentType.PAN, entity.getDocType());
        assertEquals(500L, entity.getFileSize());
    }

    @Test
    void testAllArgsConstructor() {
        ApplicantEntity applicant = new ApplicantEntity();
        applicant.setId(3L);
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        DocumentEntity entity = new DocumentEntity(
                id,
                applicant,
                DocumentType.PASSPORT,
                DocumentStatus.UPLOADED,
                "passport.pdf",
                "s3/passport",
                "application/pdf",
                200L,
                now);
        assertEquals(id, entity.getId());
        assertEquals("passport.pdf", entity.getFilename());
        assertEquals("s3/passport", entity.getS3Key());
        assertEquals(DocumentType.PASSPORT, entity.getDocType());
    }

    @Test
    void testNullValues() {
        DocumentEntity entity = DocumentEntity.builder()
                .filename(null)
                .s3Key(null)
                .build();
        assertNull(entity.getFilename());
        assertNull(entity.getS3Key());
    }

    @Test
    void testBuilderToString() {
        DocumentEntity.DocumentEntityBuilder builder = DocumentEntity.builder()
                .filename("file.pdf");

        String result = builder.toString();

        assertNotNull(result);
    }

    @Test
    void testPartialBuilder() {
        DocumentEntity entity = DocumentEntity.builder()
                .filename("only-file.pdf")
                .build();

        assertEquals("only-file.pdf", entity.getFilename());
        assertNull(entity.getS3Key());
        assertNull(entity.getDocType());
    }
    @Test
    void testBuilderWithAllNulls() {
        DocumentEntity entity = DocumentEntity.builder().build();

        assertNull(entity.getId());
        assertNull(entity.getApplicant());
        assertNull(entity.getDocType());
        assertNull(entity.getStatus());
        assertNull(entity.getFilename());
    }
}