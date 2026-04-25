package com.educationloan.document.service;
import com.educationloan.document.dto.DocumentResponseDTO;
import com.educationloan.document.dto.kafEvent.DocumentUploadedEventDTO;
import com.educationloan.document.entity.ApplicantEntity;
import com.educationloan.document.entity.DocumentEntity;
import com.educationloan.document.entity.LoanEntity;
import com.educationloan.document.enumConst.ApplicantType;
import com.educationloan.document.enumConst.DocumentStatus;
import com.educationloan.document.enumConst.DocumentType;
import com.educationloan.document.enumConst.StudyLocationType;
import com.educationloan.document.globalExceptionHandling.CustomException.*;
import com.educationloan.document.repository.ApplicantRepository;
import com.educationloan.document.repository.DocumentRepository;
import com.educationloan.document.repository.LoanRepository;
import com.educationloan.document.strategy.DocumentRuleEngine;
import com.educationloan.document.util.S3Util;
import com.educationloan.document.validate.DocumentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentUploadServiceImpl implements DocumentUploadService {
    private final ApplicantRepository applicantRepo;
    private final LoanRepository loanRepository;
    private final DocumentRepository documentRepo;
    private final DocumentValidator documentValidator;
    private final S3Util s3Util;
    private final ModelMapper mapper;
    private final DocumentRuleEngine documentRuleEngine;
    private final KafkaTemplate<String , DocumentUploadedEventDTO> kafkaTemplate;

    @Value("${topic.document-uploaded}")
    private String documentUploadedTopic;

    @Override
    @Transactional
    public DocumentResponseDTO uploadDocument(
            MultipartFile file,
            Long applicantId,
            DocumentType docType,
            StudyLocationType studyLocationType) {

        documentValidator.validateFileType(file);

        ApplicantEntity applicant = applicantRepo.findById(applicantId).orElseThrow(() -> new ApplicantNotFoundException("Applicant not found"));

        LoanEntity loan = applicant.getLoan();
        BigDecimal loanAmount = loan.getLoanAmount();
        Long loanId = loan.getId();

        Set<ApplicantType> allowedApplicants = documentRuleEngine.getAllowedApplicants(studyLocationType, loanAmount);

        if (!allowedApplicants.contains(applicant.getApplicantType())) {
            throw new ApplicantNotAllowedException("Applicant type " + applicant.getApplicantType() + " not allowed for loan amount " + loanAmount +
                            " and study location " + studyLocationType);
        }
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        String s3Key = buildS3Key(loanId, applicant.getApplicantType().name(), fileName);

        s3Util.uploadFile(s3Key, file);

        DocumentEntity entity = DocumentEntity.builder()
                .applicant(applicant)
                .docType(docType)
                .status(DocumentStatus.UPLOADED)
                .filename(fileName)
                .s3Key(s3Key)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .uploadedAt(Instant.now())
                .build();

        DocumentEntity savedEntity = documentRepo.save(entity);

        log.info("Document uploaded successfully: {}", savedEntity.getId());

        try {
            publishKafkaEvent(savedEntity);
        } catch (Exception ex) {
            log.error("Kafka publish failed for documentId: {}", savedEntity.getId(), ex);
        }

        DocumentResponseDTO dto = mapper.map(savedEntity, DocumentResponseDTO.class);

        dto.setApplicantType(savedEntity.getApplicant().getApplicantType());

        String key = savedEntity.getS3Key();
        if (key == null || key.isBlank()) {
            log.error("S3 key is null for documentId: {}", savedEntity.getId());
            throw new RuntimeException("S3 key missing for document");
        }
        dto.setDownloadUrl(s3Util.generatePreSignedDownloadUrl(key, 5));
        return dto;
    }
    private void publishKafkaEvent(DocumentEntity savedEntity){
        DocumentUploadedEventDTO event = DocumentUploadedEventDTO.builder()
                .documentId(savedEntity.getId())
                .applicantId(savedEntity.getApplicant().getId())
                .loanId(savedEntity.getApplicant().getLoan().getId())
                .documentType(savedEntity.getDocType())
                .filename(savedEntity.getFilename())
                .s3Key(savedEntity.getS3Key())
                .contentType(savedEntity.getContentType())
                .fileSize(savedEntity.getFileSize())
                .uploadedAt(savedEntity.getUploadedAt())
                .build();

        kafkaTemplate.send(documentUploadedTopic, savedEntity.getId().toString(), event);
        log.info("Kafka event published: {}", event);
    }

    @Override
    public String getDownloadUrl(UUID documentId, Long loanId) {
        DocumentEntity document = documentRepo.findByIdAndApplicant_Loan_Id(documentId, loanId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));

        String key = document.getS3Key();
        if (key == null || key.isBlank()) {
            throw new S3KeyNotFoundException("S3 key missing for document");
        }
        System.out.println("S3 KEY: " + document.getS3Key());
        return s3Util.generatePreSignedDownloadUrl(key, 5);
    }

    @Override
    public Set<ApplicantType> getRequiredApplicantTypes(Long loanId, StudyLocationType studyLocationType) {
        LoanEntity loan = loanRepository.findById(loanId).orElseThrow(() -> new LoanNotFoundException("Loan not found"));
        return documentRuleEngine.getAllowedApplicants(
                studyLocationType,
                loan.getLoanAmount());
    }
    private String buildS3Key(
            Long loanId,
            String applicantType,
            String fileName) {

        return "loan-documents/"
                + loanId + "/"
                + applicantType + "/"
                + fileName;
    }
}