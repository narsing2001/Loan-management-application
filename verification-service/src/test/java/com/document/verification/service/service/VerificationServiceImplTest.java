package com.document.verification.service.service;


import com.document.verification.service.client.AadhaarVerificationClient;
import com.document.verification.service.client.ApplicantClient;
import com.document.verification.service.constant.DocumentType;
import com.document.verification.service.dto.AadhaarVerifyResponse;
import com.document.verification.service.dto.ApplicantResponseDTO;
import com.document.verification.service.dto.DocumentUploadedEventDTO;
import com.document.verification.service.dto.ParsedDocumentDTO;
import com.document.verification.service.entity.DocumentVerificationEntity;
import com.document.verification.service.parser.DocumentParser;
import com.document.verification.service.parser.ParserFactory;
import com.document.verification.service.repository.DocumentVerificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.io.File;
import java.util.UUID;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class VerificationServiceImplTest {

        @Mock
        private DocumentVerificationRepository repository;

        @Mock
        private ParserFactory parserFactory;

        @Mock
        private S3DownloadService s3DownloadService;

        @Mock
        private OCRService ocrService;

        @Mock
        private AadhaarVerificationClient aadhaarClient;

        @Mock
        private ApplicantClient applicantClient;

        @Mock
        private ModelMapper modelMapper;

        @Mock
        private DocumentParser parser;

        @InjectMocks
        private VerificationServiceImpl verificationService;

    @Test
    void verifyDocument_success() {
        DocumentUploadedEventDTO event = new DocumentUploadedEventDTO();
        event.setDocumentId(UUID.randomUUID());
        event.setApplicantId(1L);
        event.setDocumentType(DocumentType.AADHAAR);
        event.setS3Key("doc.png");

        File file = new File("test.png");

        ParsedDocumentDTO parsed = new ParsedDocumentDTO();
        parsed.setDocumentNumber("123456789012");
        parsed.setName("Parveen");
        parsed.setDob("01-01-2000");

        AadhaarVerifyResponse response = new AadhaarVerifyResponse();
        response.setValid(true);

        ApplicantResponseDTO applicant = new ApplicantResponseDTO();
        applicant.setAadhaarNumber("123456789012");

        when(s3DownloadService.download("doc.png")).thenReturn(file);
        when(ocrService.extractText(file)).thenReturn("aadhaar text");
        when(parserFactory.getParser("AADHAAR")).thenReturn(parser);
        when(parser.parse("aadhaar text")).thenReturn(parsed);
        when(aadhaarClient.verifyAadhaar(anyString(), any())).thenReturn(response);
        when(applicantClient.getApplicant(1L)).thenReturn(applicant);

        verificationService.verifyDocument(event);

        verify(repository, times(1)).save(any(DocumentVerificationEntity.class));
    }

    @Test
    void verifyDocument_ocrFailure() {
        DocumentUploadedEventDTO event = new DocumentUploadedEventDTO();
        event.setDocumentId(UUID.randomUUID());
        event.setApplicantId(1L);
        event.setDocumentType(DocumentType.AADHAAR);
        event.setS3Key("doc.png");

        File file = new File("test.png");

        when(s3DownloadService.download("doc.png")).thenReturn(file);
        when(ocrService.extractText(file)).thenThrow(new RuntimeException("OCR failed"));
        verificationService.verifyDocument(event);
        verify(repository).save(any(DocumentVerificationEntity.class));
    }
    }

