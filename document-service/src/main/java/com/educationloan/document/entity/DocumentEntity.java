package com.educationloan.document.entity;
import com.educationloan.document.enumConst.DocumentStatus;
import com.educationloan.document.enumConst.DocumentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "document_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentEntity {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private ApplicantEntity applicant;

    @Enumerated(EnumType.STRING)
    private DocumentType docType;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    private String filename;

    @Column(name = "s3_key")
    private String s3Key;

    private String contentType;

    private Long fileSize;

    private Instant uploadedAt;
}