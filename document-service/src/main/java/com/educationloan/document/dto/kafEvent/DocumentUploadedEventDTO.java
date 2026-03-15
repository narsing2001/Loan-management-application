package com.educationloan.document.dto.kafEvent;

import com.educationloan.document.enumConst.DocumentType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentUploadedEventDTO {

    private UUID documentId;

    private Long applicantId;

    private Long loanId;

    private DocumentType documentType;

    private String filename;

    private String s3Key;

    private String contentType;

    private Long fileSize;

    private Instant uploadedAt;

}