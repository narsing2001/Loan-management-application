package com.document.verification.service.repository;

import com.document.verification.service.entity.DocumentVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentVerificationRepository extends JpaRepository<DocumentVerificationEntity, Long> {

    Optional<DocumentVerificationEntity> findByDocumentId(UUID documentId);
}
