package com.educationloan.document.service;

import com.educationloan.document.dto.DocumentResponseDTO;
import com.educationloan.document.entity.*;
import com.educationloan.document.enumConst.*;
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

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentUploadServiceImplTest {

    @InjectMocks
    private DocumentUploadServiceImpl service;

    @Mock
    private ApplicantRepository applicantRepo;

    @Mock
    private DocumentRepository documentRepo;

    @Mock
    private DocumentValidator documentValidator;

    @Mock
    private S3Util s3Util;

    @Mock
    private DocumentRuleEngine ruleEngine;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private MultipartFile file;

    @Test
    void testUploadDocumentSuccess() {

        ReflectionTestUtils.setField(
                service,
                "documentUploadedTopic",
                "documentUploadTopic"
        );

        LoanEntity loan = new LoanEntity();
        loan.setId(1L);
        loan.setLoanAmount(BigDecimal.valueOf(500000));

        ApplicantEntity applicant = new ApplicantEntity();
        applicant.setId(1L);
        applicant.setApplicantType(ApplicantType.STUDENT);
        applicant.setLoan(loan);

        when(applicantRepo.findById(1L)).thenReturn(Optional.of(applicant));

        when(ruleEngine.getAllowedApplicants(
                StudyLocationType.DOMESTIC,
                loan.getLoanAmount()))
                .thenReturn(Set.of(ApplicantType.STUDENT));

        when(file.getOriginalFilename()).thenReturn("aadhaar.pdf");
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSize()).thenReturn(2000L);

        doNothing().when(documentValidator).validateFileType(file);
        doNothing().when(s3Util).uploadFile(anyString(), any());

        DocumentEntity savedDoc = new DocumentEntity();
        savedDoc.setId(UUID.randomUUID());
        savedDoc.setApplicant(applicant);

        when(documentRepo.save(any())).thenReturn(savedDoc);

        DocumentResponseDTO dto = new DocumentResponseDTO();
        when(modelMapper.map(any(), eq(DocumentResponseDTO.class)))
                .thenReturn(dto);

        DocumentResponseDTO response = service.uploadDocument(
                file,
                1L,
                DocumentType.AADHAAR,
                StudyLocationType.DOMESTIC
        );

        assertNotNull(response);

        verify(applicantRepo, times(1)).findById(1L);

        verify(documentValidator, times(1))
                .validateFileType(file);

        verify(ruleEngine, times(1))
                .getAllowedApplicants(
                        StudyLocationType.DOMESTIC,
                        loan.getLoanAmount());

        verify(s3Util, times(1))
                .uploadFile(anyString(), any());

        verify(documentRepo, times(1))
                .save(any());

        verify(kafkaTemplate, times(1))
                .send(anyString(), anyString(), any());
    }

    @Test
    void testApplicantNotFound() {

        when(applicantRepo.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.uploadDocument(
                        file,
                        1L,
                        DocumentType.AADHAAR,
                        StudyLocationType.DOMESTIC
                )
        );
        System.out.println("Exception thrown: " + exception.getMessage());
        assertEquals("Applicant not found", exception.getMessage());
    }
}
