package com.educationloan.document.service;
import com.educationloan.document.dto.DocumentResponseDTO;
import com.educationloan.document.dto.kafEvent.DocumentUploadedEventDTO;
import com.educationloan.document.entity.*;
import com.educationloan.document.enumConst.*;
import com.educationloan.document.globalExceptionHandling.CustomException.*;
import com.educationloan.document.repository.*;
import com.educationloan.document.strategy.DocumentRuleEngine;
import com.educationloan.document.util.S3Util;
import com.educationloan.document.validate.DocumentValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class DocumentUploadServiceImplTest {
    @InjectMocks
    private DocumentUploadServiceImpl service;

    @Mock private ApplicantRepository applicantRepo;
    @Mock private LoanRepository loanRepository;
    @Mock private DocumentRepository documentRepo;
    @Mock private DocumentValidator documentValidator;
    @Mock private S3Util s3Util;
    @Mock private DocumentRuleEngine ruleEngine;
    @Mock private KafkaTemplate<String, DocumentUploadedEventDTO> kafkaTemplate;
    @Mock private ModelMapper mapper;
    @Mock private MultipartFile file;

    @Test
    void uploadDocument_success() {
        ReflectionTestUtils.setField(service, "documentUploadedTopic", "topic");

        LoanEntity loan = new LoanEntity();
        loan.setId(1L);
        loan.setLoanAmount(BigDecimal.valueOf(400000));

        ApplicantEntity applicant = new ApplicantEntity();
        applicant.setId(1L);
        applicant.setApplicantType(ApplicantType.STUDENT);
        applicant.setLoan(loan);

        when(applicantRepo.findById(1L)).thenReturn(Optional.of(applicant));
        when(ruleEngine.getAllowedApplicants(any(), any())).thenReturn(Set.of(ApplicantType.STUDENT));

        when(file.getOriginalFilename()).thenReturn("test.pdf");
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSize()).thenReturn(1000L);

        doNothing().when(documentValidator).validateFileType(file);
        doNothing().when(s3Util).uploadFile(anyString(), any());

        when(s3Util.generatePreSignedDownloadUrl(anyString(), eq(5))).thenReturn("download-url");

        DocumentEntity saved = new DocumentEntity();
        saved.setId(UUID.randomUUID());
        saved.setApplicant(applicant);
        saved.setS3Key("loan-documents/1/STUDENT/test.pdf");

        when(documentRepo.save(any())).thenReturn(saved);
        when(mapper.map(any(), eq(DocumentResponseDTO.class))).thenReturn(new DocumentResponseDTO());
        DocumentResponseDTO response = service.uploadDocument(file, 1L, DocumentType.AADHAAR, StudyLocationType.DOMESTIC);
        assertNotNull(response);
        assertEquals("download-url", response.getDownloadUrl());

        verify(kafkaTemplate).send(anyString(), anyString(), any());
    }

    @Test
    void uploadDocument_applicantNotFound() {
        when(applicantRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ApplicantNotFoundException.class, () -> service.uploadDocument(file, 1L,
                        DocumentType.AADHAAR,
                        StudyLocationType.DOMESTIC));
    }

    @Test
    void uploadDocument_invalidFile() {

        doThrow(new InvalidFileException("Invalid file")).when(documentValidator).validateFileType(file);

        assertThrows(InvalidFileException.class, () -> service.uploadDocument(file, 1L,
                        DocumentType.AADHAAR,
                        StudyLocationType.DOMESTIC));

        verify(s3Util, never()).uploadFile(anyString(), any());
    }

    @Test
    void uploadDocument_applicantNotAllowed() {

        LoanEntity loan = new LoanEntity();
        loan.setLoanAmount(BigDecimal.valueOf(400000));

        ApplicantEntity applicant = new ApplicantEntity();
        applicant.setApplicantType(ApplicantType.GUARANTOR);
        applicant.setLoan(loan);

        when(applicantRepo.findById(1L)).thenReturn(Optional.of(applicant));
        when(ruleEngine.getAllowedApplicants(any(), any())).thenReturn(Set.of(ApplicantType.STUDENT));
        assertThrows(ApplicantNotAllowedException.class, () -> service.uploadDocument(file, 1L,
                        DocumentType.AADHAAR,
                        StudyLocationType.DOMESTIC));

        verify(s3Util, never()).uploadFile(any(), any());
    }

    @Test
    void uploadDocument_s3Failure() {
        LoanEntity loan = new LoanEntity();
        loan.setLoanAmount(BigDecimal.valueOf(400000));

        ApplicantEntity applicant = new ApplicantEntity();
        applicant.setApplicantType(ApplicantType.STUDENT);
        applicant.setLoan(loan);

        when(applicantRepo.findById(1L)).thenReturn(Optional.of(applicant));

        when(ruleEngine.getAllowedApplicants(any(), any())).thenReturn(Set.of(ApplicantType.STUDENT));

        doThrow(new RuntimeException("S3 failed")).when(s3Util).uploadFile(anyString(), any());

        assertThrows(RuntimeException.class, () -> service.uploadDocument(file, 1L,
                        DocumentType.AADHAAR,
                        StudyLocationType.DOMESTIC));

        verify(documentRepo, never()).save(any());
    }

    @Test
    void uploadDocument_s3KeyMissing() {
        ReflectionTestUtils.setField(service, "documentUploadedTopic", "topic");

        LoanEntity loan = new LoanEntity();
        loan.setLoanAmount(BigDecimal.valueOf(400000));

        ApplicantEntity applicant = new ApplicantEntity();
        applicant.setApplicantType(ApplicantType.STUDENT);
        applicant.setLoan(loan);

        when(applicantRepo.findById(1L)).thenReturn(Optional.of(applicant));
        when(ruleEngine.getAllowedApplicants(any(), any())).thenReturn(Set.of(ApplicantType.STUDENT));

        doNothing().when(documentValidator).validateFileType(file);

        DocumentEntity saved = new DocumentEntity();
        saved.setId(UUID.randomUUID());
        saved.setApplicant(applicant);
        saved.setS3Key("");

        when(documentRepo.save(any())).thenReturn(saved);

        when(mapper.map(any(), eq(DocumentResponseDTO.class))).thenReturn(new DocumentResponseDTO());

        assertThrows(RuntimeException.class, () -> service.uploadDocument(file, 1L,
                        DocumentType.AADHAAR,
                        StudyLocationType.DOMESTIC));
    }

    @Test
    void uploadDocument_kafkaFailure_shouldNotFailAPI() {
        ReflectionTestUtils.setField(service, "documentUploadedTopic", "topic");

        LoanEntity loan = new LoanEntity();
        loan.setLoanAmount(BigDecimal.valueOf(400000));

        ApplicantEntity applicant = new ApplicantEntity();
        applicant.setApplicantType(ApplicantType.STUDENT);
        applicant.setLoan(loan);

        when(applicantRepo.findById(1L)).thenReturn(Optional.of(applicant));
        when(ruleEngine.getAllowedApplicants(any(), any())).thenReturn(Set.of(ApplicantType.STUDENT));

        when(file.getOriginalFilename()).thenReturn("test.pdf");
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSize()).thenReturn(100L);

        doNothing().when(documentValidator).validateFileType(file);

        DocumentEntity saved = new DocumentEntity();
        saved.setId(UUID.randomUUID());
        saved.setApplicant(applicant);
        saved.setS3Key("key");

        when(documentRepo.save(any())).thenReturn(saved);
        when(mapper.map(any(), eq(DocumentResponseDTO.class))).thenReturn(new DocumentResponseDTO());
        when(s3Util.generatePreSignedDownloadUrl(any(), anyInt())).thenReturn("url");
        doThrow(new RuntimeException("Kafka down")).when(kafkaTemplate).send(any(), any(), any());

        DocumentResponseDTO response = service.uploadDocument(
                file, 1L,
                DocumentType.AADHAAR,
                StudyLocationType.DOMESTIC);

        assertNotNull(response);
    }
    @Test
    void uploadDocument_ruleEngineEmptySet_shouldFail() {

        LoanEntity loan = new LoanEntity();
        loan.setLoanAmount(BigDecimal.valueOf(400000));

        ApplicantEntity applicant = new ApplicantEntity();
        applicant.setApplicantType(ApplicantType.STUDENT);
        applicant.setLoan(loan);

        when(applicantRepo.findById(1L)).thenReturn(Optional.of(applicant));
        when(ruleEngine.getAllowedApplicants(any(), any())).thenReturn(Set.of());
        assertThrows(ApplicantNotAllowedException.class, () -> service.uploadDocument(file, 1L,
                        DocumentType.AADHAAR,
                        StudyLocationType.DOMESTIC));
    }

    @Test
    void uploadDocument_international_success_flow() {

        LoanEntity loan = new LoanEntity();
        loan.setLoanAmount(BigDecimal.valueOf(400000));

        ApplicantEntity applicant = new ApplicantEntity();
        applicant.setApplicantType(ApplicantType.STUDENT);
        applicant.setLoan(loan);

        when(applicantRepo.findById(1L)).thenReturn(Optional.of(applicant));
        when(ruleEngine.getAllowedApplicants(any(), any())).thenReturn(Set.of(ApplicantType.STUDENT));

        when(file.getOriginalFilename()).thenReturn("test.pdf");
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSize()).thenReturn(100L);

        doNothing().when(documentValidator).validateFileType(file);
        doNothing().when(s3Util).uploadFile(anyString(), any());

        DocumentEntity saved = new DocumentEntity();
        saved.setId(UUID.randomUUID());
        saved.setS3Key("loan-documents/1/STUDENT/test.pdf");
        saved.setApplicant(applicant);

        when(documentRepo.save(any())).thenReturn(saved);
        when(mapper.map(any(), eq(DocumentResponseDTO.class))).thenReturn(new DocumentResponseDTO());
        when(s3Util.generatePreSignedDownloadUrl(anyString(), eq(5))).thenReturn("url");
        DocumentResponseDTO response = service.uploadDocument(file, 1L, DocumentType.PASSPORT, StudyLocationType.INTERNATIONAL);
        assertNotNull(response);
    }
    @Test
    void getDownloadUrl_whitespaceKey_shouldFail() {

        UUID id = UUID.randomUUID();
        DocumentEntity doc = new DocumentEntity();
        doc.setS3Key("   ");
        when(documentRepo.findByIdAndApplicant_Loan_Id(id, 1L)).thenReturn(Optional.of(doc));
        assertThrows(S3KeyNotFoundException.class, () -> service.getDownloadUrl(id, 1L));
    }

    @Test
    void getDownloadUrl_success() {
        UUID id = UUID.randomUUID();

        DocumentEntity doc = new DocumentEntity();
        doc.setId(id);
        doc.setS3Key("key");

        when(documentRepo.findByIdAndApplicant_Loan_Id(id, 1L)).thenReturn(Optional.of(doc));
        when(s3Util.generatePreSignedDownloadUrl("key", 5)).thenReturn("url");
        String result = service.getDownloadUrl(id, 1L);
        assertEquals("url", result);
    }

    @Test
    void getDownloadUrl_notFound() {
        UUID id = UUID.randomUUID();
        when(documentRepo.findByIdAndApplicant_Loan_Id(id, 1L)).thenReturn(Optional.empty());
        assertThrows(DocumentNotFoundException.class, () -> service.getDownloadUrl(id, 1L));
    }

    @Test
    void getDownloadUrl_s3KeyMissing() {
        UUID id = UUID.randomUUID();
        DocumentEntity doc = new DocumentEntity();
        doc.setS3Key("");
        when(documentRepo.findByIdAndApplicant_Loan_Id(id, 1L)).thenReturn(Optional.of(doc));
        assertThrows(S3KeyNotFoundException.class, () -> service.getDownloadUrl(id, 1L));
    }
}