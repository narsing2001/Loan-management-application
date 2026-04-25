package com.document.verification.service.repository;

import com.document.verification.service.constant.DocumentType;
import com.document.verification.service.constant.VerificationStatus;
import com.document.verification.service.entity.DocumentVerificationEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DocumentVerificationRepositoryTest {

    @Autowired
    private DocumentVerificationRepository repository;

    @Test
    @DisplayName("Should save and fetch entity by documentId")
    void testFindByDocumentId_Success() {
        UUID documentId = UUID.randomUUID();

        DocumentVerificationEntity entity = DocumentVerificationEntity.builder()
                .documentId(documentId)
                .applicantId(101L)
                .documentType(DocumentType.AADHAAR)
                .verificationStatus(VerificationStatus.VERIFIED)
                .extractedName("John Doe")
                .extractedDob("01-01-1990")
                .extractedNumber("123456789012")
                .verifiedAt(Instant.now())
                .build();

        repository.save(entity);
        Optional<DocumentVerificationEntity> result =
                repository.findByDocumentId(documentId);
        assertTrue(result.isPresent());
        assertEquals(documentId, result.get().getDocumentId());
        assertEquals("John Doe", result.get().getExtractedName());
    }
    @Test
    @DisplayName("Should return empty when documentId not found")
    void testFindByDocumentId_NotFound() {
        UUID randomId = UUID.randomUUID();

        Optional<DocumentVerificationEntity> result =
                repository.findByDocumentId(randomId);

        assertFalse(result.isPresent());
    }
    @Test
    @DisplayName("Should save entity successfully")
    void testSaveEntity() {
        UUID documentId = UUID.randomUUID();

        DocumentVerificationEntity entity = DocumentVerificationEntity.builder()
                .documentId(documentId)
                .build();

        DocumentVerificationEntity saved = repository.save(entity);

        assertNotNull(saved.getId());
        assertEquals(documentId, saved.getDocumentId());
    }
    @Test
    @DisplayName("Should throw exception for duplicate documentId")
    void testUniqueConstraint() {
        UUID documentId = UUID.randomUUID();

        DocumentVerificationEntity entity1 = DocumentVerificationEntity.builder()
                .documentId(documentId)
                .build();

        DocumentVerificationEntity entity2 = DocumentVerificationEntity.builder()
                .documentId(documentId)
                .build();

        repository.save(entity1);

        assertThrows(Exception.class, () -> {
            repository.saveAndFlush(entity2);
        });
    }
}
