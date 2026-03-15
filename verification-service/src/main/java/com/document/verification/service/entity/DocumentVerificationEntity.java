package com.document.verification.service.entity;

import com.document.verification.service.constant.DocumentType;
import com.document.verification.service.constant.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "document_verification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentVerificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID documentId;

    private Long applicantId;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    private String extractedName;

    private String extractedDob;

    private String extractedNumber;

    private Instant verifiedAt;
}