package com.document.verification.service.entity;

import com.document.verification.service.constant.DocumentType;
import com.document.verification.service.constant.VerificationStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentVerificationEntityTest {
    @Test
    void testGetterAndSetter() {
        DocumentVerificationEntity entity = new DocumentVerificationEntity();

        UUID docId = UUID.randomUUID();
        Instant now = Instant.now();

        entity.setId(1L);
        entity.setDocumentId(docId);
        entity.setApplicantId(100L);
        entity.setDocumentType(DocumentType.AADHAAR);
        entity.setVerificationStatus(VerificationStatus.VERIFIED);
        entity.setExtractedName("John Doe");
        entity.setExtractedDob("01-01-1990");
        entity.setExtractedNumber("123456789012");
        entity.setVerifiedAt(now);

        assertEquals(1L, entity.getId());
        assertEquals(docId, entity.getDocumentId());
        assertEquals(100L, entity.getApplicantId());
        assertEquals(DocumentType.AADHAAR, entity.getDocumentType());
        assertEquals(VerificationStatus.VERIFIED, entity.getVerificationStatus());
        assertEquals("John Doe", entity.getExtractedName());
        assertEquals("01-01-1990", entity.getExtractedDob());
        assertEquals("123456789012", entity.getExtractedNumber());
        assertEquals(now, entity.getVerifiedAt());
    }
    @Test
    void testBuilder() {
        UUID docId = UUID.randomUUID();
        Instant now = Instant.now();

        DocumentVerificationEntity entity = DocumentVerificationEntity.builder()
                .id(1L)
                .documentId(docId)
                .applicantId(200L)
                .documentType(DocumentType.PAN)
                .verificationStatus(VerificationStatus.PENDING)
                .extractedName("Alice")
                .extractedDob("02-02-1995")
                .extractedNumber("ABCDE1234F")
                .verifiedAt(now)
                .build();

        assertNotNull(entity);
        assertEquals(docId, entity.getDocumentId());
        assertEquals(DocumentType.PAN, entity.getDocumentType());
        assertEquals(VerificationStatus.PENDING, entity.getVerificationStatus());
    }
    @Test
    void testNoArgsConstructor() {
        DocumentVerificationEntity entity = new DocumentVerificationEntity();
        assertNotNull(entity);
    }
    @Test
    void testAllArgsConstructor() {
        UUID docId = UUID.randomUUID();
        Instant now = Instant.now();

        DocumentVerificationEntity entity = new DocumentVerificationEntity(
                1L,
                docId,
                101L,
                DocumentType.AADHAAR,
                VerificationStatus.VERIFIED,
                "John",
                "01-01-1990",
                "123456789012",
                now);

        assertEquals(1L, entity.getId());
        assertEquals(docId, entity.getDocumentId());
    }

    @Test
    void shouldCallBuilderToString() {
        DocumentVerificationEntity.DocumentVerificationEntityBuilder builder =
                DocumentVerificationEntity.builder()
                        .extractedName("John");

        String result = builder.toString();

        assertNotNull(result);
    }

    @Test
    void shouldHandleNullValues() {
        DocumentVerificationEntity entity = DocumentVerificationEntity.builder().build();

        assertNull(entity.getDocumentId());
        assertNull(entity.getExtractedName());
        assertNull(entity.getVerificationStatus());
    }

    @Test
    void shouldVerifyAllFields() {
        UUID docId = UUID.randomUUID();
        Instant now = Instant.now();

        DocumentVerificationEntity entity = DocumentVerificationEntity.builder()
                .id(1L)
                .documentId(docId)
                .applicantId(10L)
                .documentType(DocumentType.AADHAAR)
                .verificationStatus(VerificationStatus.VERIFIED)
                .extractedName("John Doe")
                .extractedDob("01-01-2000")
                .extractedNumber("1234567890")
                .verifiedAt(now)
                .build();

        assertAll(
                () -> assertEquals(1L, entity.getId()),
                () -> assertEquals(docId, entity.getDocumentId()),
                () -> assertEquals(10L, entity.getApplicantId()),
                () -> assertEquals(DocumentType.AADHAAR, entity.getDocumentType()),
                () -> assertEquals(VerificationStatus.VERIFIED, entity.getVerificationStatus()),
                () -> assertEquals("John Doe", entity.getExtractedName()),
                () -> assertEquals("01-01-2000", entity.getExtractedDob()),
                () -> assertEquals("1234567890", entity.getExtractedNumber()),
                () -> assertEquals(now, entity.getVerifiedAt())
        );
    }
}