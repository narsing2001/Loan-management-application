package com.document.verification.service.service;
import com.document.verification.service.client.AadhaarVerificationClient;
import com.document.verification.service.client.ApplicantClient;
import com.document.verification.service.constant.VerificationStatus;
import com.document.verification.service.dto.*;
import com.document.verification.service.entity.DocumentVerificationEntity;
import com.document.verification.service.parser.DocumentParser;
import com.document.verification.service.parser.ParserFactory;
import com.document.verification.service.repository.DocumentVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationServiceImpl implements VerificationService {

    private final DocumentVerificationRepository repository;
    private final ParserFactory parserFactory;
    private final S3DownloadService s3DownloadService;
    private final OCRService ocrService;
    private final ModelMapper modelMapper;
    private final AadhaarVerificationClient aadhaarClient;
    private final ApplicantClient applicantClient;

    @Value("${aadhaar.api.token}")
    private String apiToken;

    @Override
    public void verifyDocument(DocumentUploadedEventDTO event) {

        log.info("Starting verification for document {}", event.getDocumentId());

        try {

            File file = s3DownloadService.download(event.getS3Key());

            String extractedText = ocrService.extractText(file);

            log.info("OCR extracted text {}", extractedText);

            DocumentParser parser =
                    parserFactory.getParser(event.getDocumentType().name());

            ParsedDocumentDTO parsed = parser.parse(extractedText);

            VerificationStatus status = VerificationStatus.FAILED;

            if ("AADHAAR".equalsIgnoreCase(event.getDocumentType().name())) {

                String extractedAadhaar = parsed.getDocumentNumber();

                if (extractedAadhaar != null) {

                    AadhaarVerifyRequest request = new AadhaarVerifyRequest();
                    request.setAadhaarNumber(extractedAadhaar);

                    AadhaarVerifyResponse response =
                            aadhaarClient.verifyAadhaar("Bearer " + apiToken, request);

                    if (response.isValid()) {

                        ApplicantResponseDTO applicant =
                                applicantClient.getApplicant(event.getApplicantId());

                        if (extractedAadhaar.equals(applicant.getAadhaarNumber())) {

                            log.info("Applicant Aadhaar matched");
                            status = VerificationStatus.VERIFIED;

                        } else {
                            log.warn("Aadhaar mismatch");
                            status = VerificationStatus.FAILED;
                        }
                    }
                }
            }
            DocumentVerificationEntity entity =
                    DocumentVerificationEntity.builder()
                            .documentId(event.getDocumentId())
                            .applicantId(event.getApplicantId())
                            .documentType(event.getDocumentType())
                            .verificationStatus(status)
                            .extractedName(parsed.getName())
                            .extractedDob(parsed.getDob())
                            .extractedNumber(parsed.getDocumentNumber())
                            .verifiedAt(Instant.now())
                            .build();

            repository.save(entity);

        } catch (Exception e) {

            log.error("Verification failed", e);

            repository.save(
                    DocumentVerificationEntity.builder()
                            .documentId(event.getDocumentId())
                            .applicantId(event.getApplicantId())
                            .documentType(event.getDocumentType())
                            .verificationStatus(VerificationStatus.FAILED)
                            .verifiedAt(Instant.now())
                            .build()
            );
        }
    }
    @Override
    public VerificationResponseDTO getVerificationStatus(UUID documentId) {

        DocumentVerificationEntity entity = repository.findByDocumentId(documentId)
                        .orElseThrow(() -> new RuntimeException("Verification not found"));

        return modelMapper.map(entity, VerificationResponseDTO.class);
    }
}