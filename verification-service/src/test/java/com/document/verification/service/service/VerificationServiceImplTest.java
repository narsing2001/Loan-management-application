package com.document.verification.service.service;
import com.document.verification.service.client.AadhaarVerificationClient;
import com.document.verification.service.client.ApplicantClient;
import com.document.verification.service.constant.DocumentType;
import com.document.verification.service.constant.VerificationStatus;
import com.document.verification.service.dto.*;
import com.document.verification.service.entity.DocumentVerificationEntity;
import com.document.verification.service.globalExceptionHandling.customException.S3DownloadException;
import com.document.verification.service.parser.DocumentParser;
import com.document.verification.service.parser.ParserFactory;
import com.document.verification.service.repository.DocumentVerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;
import java.io.File;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class VerificationServiceImplTest {
    @InjectMocks
    private VerificationServiceImpl service;

    @Mock private DocumentVerificationRepository repository;
    @Mock private ParserFactory parserFactory;
    @Mock private S3DownloadService s3DownloadService;
    @Mock private OCRService ocrService;
    @Mock private ModelMapper modelMapper;
    @Mock private AadhaarVerificationClient aadhaarClient;
    @Mock private ApplicantClient applicantClient;
    @Mock private DocumentParser parser;

    private DocumentUploadedEventDTO event;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "apiToken", "test-token");
        event = new DocumentUploadedEventDTO();
        event.setDocumentId(UUID.randomUUID());
        event.setApplicantId(1L);
        event.setDocumentType(DocumentType.AADHAAR);
        event.setS3Key("key");
    }
    @Test
    void verifyDocument_success_verified() {
        File file = new File("test");
        when(s3DownloadService.download(any())).thenReturn(file);
        when(ocrService.extractText(file)).thenReturn("1234 5678 9123\nDOB: 01/01/2000");
        when(parserFactory.getParser(any())).thenReturn(parser);
        ParsedDocumentDTO parsed = ParsedDocumentDTO.builder()
                .documentNumber("123456789123")
                .name("Parveen")
                .dob("01/01/2000")
                .build();

        when(parser.parse(any())).thenReturn(parsed);
        AadhaarVerifyResponse aadhaarResponse = new AadhaarVerifyResponse();
        aadhaarResponse.setValid(true);
        when(aadhaarClient.verifyAadhaar(any(), any()))
                .thenReturn(aadhaarResponse);
        ApplicantResponseDTO applicant = new ApplicantResponseDTO();
        applicant.setAadhaarNumber("123456789123");
        when(applicantClient.getApplicant(any()))
                .thenReturn(applicant);
        service.verifyDocument(event);
        verify(repository).save(argThat(entity ->
                entity.getVerificationStatus() == VerificationStatus.VERIFIED));
    }
    @Test
    void verifyDocument_aadhaarMismatch() {
        File file = new File("test");
        when(s3DownloadService.download(any())).thenReturn(file);
        when(ocrService.extractText(file)).thenReturn("1234 5678 9999");
        when(parserFactory.getParser(any())).thenReturn(parser);
        ParsedDocumentDTO parsed = ParsedDocumentDTO.builder()
                .documentNumber("123456789999")
                .build();
        when(parser.parse(any())).thenReturn(parsed);
        AadhaarVerifyResponse response = new AadhaarVerifyResponse();
        response.setValid(true);
        when(aadhaarClient.verifyAadhaar(any(), any()))
                .thenReturn(response);
        ApplicantResponseDTO applicant = new ApplicantResponseDTO();
        applicant.setAadhaarNumber("123456789123");
        when(applicantClient.getApplicant(any()))
                .thenReturn(applicant);
        service.verifyDocument(event);
        verify(repository).save(argThat(entity ->
                entity.getVerificationStatus() == VerificationStatus.FAILED));
    }
    @Test
    void verifyDocument_aadhaarInvalid() {
        File file = new File("test");
        when(s3DownloadService.download(any())).thenReturn(file);
        when(ocrService.extractText(file)).thenReturn("1234 5678 9123");
        when(parserFactory.getParser(any())).thenReturn(parser);
        ParsedDocumentDTO parsed = ParsedDocumentDTO.builder()
                .documentNumber("123456789123")
                .build();

        when(parser.parse(any())).thenReturn(parsed);
        AadhaarVerifyResponse response = new AadhaarVerifyResponse();
        response.setValid(false);
        when(aadhaarClient.verifyAadhaar(any(), any()))
                .thenReturn(response);
        service.verifyDocument(event);
        verify(repository).save(argThat(entity ->
                entity.getVerificationStatus() == VerificationStatus.FAILED));
    }
    @Test
    void verifyDocument_noAadhaarExtracted() {
        File file = new File("test");

        when(s3DownloadService.download(any())).thenReturn(file);
        when(ocrService.extractText(file)).thenReturn("No number");
        when(parserFactory.getParser(any())).thenReturn(parser);

        ParsedDocumentDTO parsed = ParsedDocumentDTO.builder()
                .documentNumber(null)
                .build();
        when(parser.parse(any())).thenReturn(parsed);

        service.verifyDocument(event);

        verify(repository).save(argThat(entity ->
                entity.getVerificationStatus() == VerificationStatus.FAILED));
    }
    @Test
    void verifyDocument_parserNotFound() {
        when(parserFactory.getParser(any()))
                .thenThrow(new RuntimeException("Parser not found"));
        service.verifyDocument(event);
        verify(repository).save(argThat(entity ->
                entity.getVerificationStatus() == VerificationStatus.FAILED));
    }
    @Test
    void verifyDocument_ocrFailure() {
        when(s3DownloadService.download(any()))
                .thenThrow(new RuntimeException("Download failed"));
        service.verifyDocument(event);
        verify(repository).save(argThat(entity ->
                entity.getVerificationStatus() == VerificationStatus.FAILED));
    }
    @Test
    void getVerificationStatus_success() {
        UUID id = UUID.randomUUID();
        DocumentVerificationEntity entity = new DocumentVerificationEntity();
        entity.setDocumentId(id);

         when(repository.findByDocumentId(id))
                .thenReturn(Optional.of(entity));
        VerificationResponseDTO dto = new VerificationResponseDTO();

        when(modelMapper.map(entity, VerificationResponseDTO.class))
                .thenReturn(dto);
        VerificationResponseDTO result = service.getVerificationStatus(id);

        assertNotNull(result);
    }
    @Test
    void getVerificationStatus_notFound() {

        when(repository.findByDocumentId(any()))
                .thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () ->
                service.getVerificationStatus(UUID.randomUUID()));
    }
    @Test
    void verifyDocument_shouldSkipAadhaarApi_whenNumberNull() {
        File file = new File("test");

        when(s3DownloadService.download(any())).thenReturn(file);
        when(ocrService.extractText(file)).thenReturn("no aadhaar");
        when(parserFactory.getParser(any())).thenReturn(parser);
        ParsedDocumentDTO parsed = ParsedDocumentDTO.builder()
                .documentNumber(null)
                .build();
        when(parser.parse(any())).thenReturn(parsed);
        service.verifyDocument(event);
        verify(aadhaarClient, never()).verifyAadhaar(any(), any());
    }
    @Test
    void verifyDocument_shouldNotCallApplicant_whenAadhaarInvalid() {
        File file = new File("test");
        when(s3DownloadService.download(any())).thenReturn(file);
        when(ocrService.extractText(file)).thenReturn("1234 5678 9123");
        when(parserFactory.getParser(any())).thenReturn(parser);
        ParsedDocumentDTO parsed = ParsedDocumentDTO.builder()
                .documentNumber("123456789123")
                .build();
        when(parser.parse(any())).thenReturn(parsed);
        AadhaarVerifyResponse response = new AadhaarVerifyResponse();
        response.setValid(false);
        when(aadhaarClient.verifyAadhaar(any(), any()))
                .thenReturn(response);
        service.verifyDocument(event);
        verify(applicantClient, never()).getApplicant(any());
    }
    @Test
    void verifyDocument_aadhaarApiFailure() {
        File file = new File("test");
        when(s3DownloadService.download(any())).thenReturn(file);
        when(ocrService.extractText(file)).thenReturn("1234 5678 9123");
        when(parserFactory.getParser(any())).thenReturn(parser);

        ParsedDocumentDTO parsed = ParsedDocumentDTO.builder()
                .documentNumber("123456789123")
                .build();
        when(parser.parse(any())).thenReturn(parsed);
        when(aadhaarClient.verifyAadhaar(any(), any()))
                .thenThrow(new RuntimeException("API down"));
        service.verifyDocument(event);
        verify(repository).save(argThat(entity ->
                entity.getVerificationStatus() == VerificationStatus.FAILED
        ));
    }
    @Test
    void verifyDocument_applicantApiFailure() {
        File file = new File("test");
        when(s3DownloadService.download(any())).thenReturn(file);
        when(ocrService.extractText(file)).thenReturn("1234 5678 9123");
        when(parserFactory.getParser(any())).thenReturn(parser);
        ParsedDocumentDTO parsed = ParsedDocumentDTO.builder()
                .documentNumber("123456789123")
                .build();

        when(parser.parse(any())).thenReturn(parsed);
        AadhaarVerifyResponse response = new AadhaarVerifyResponse();
        response.setValid(true);

        when(aadhaarClient.verifyAadhaar(any(), any()))
                .thenReturn(response);

        when(applicantClient.getApplicant(any()))
                .thenThrow(new RuntimeException("Applicant service down"));

        service.verifyDocument(event);
        verify(repository).save(argThat(entity ->
                entity.getVerificationStatus() == VerificationStatus.FAILED));
    }
    @Test
    void verifyDocument_shouldStoreParsedFields() {
        File file = new File("test");
        when(s3DownloadService.download(any())).thenReturn(file);
        when(ocrService.extractText(file)).thenReturn("1234 5678 9123\nDOB: 01/01/2000\nParveen");
        when(parserFactory.getParser(any())).thenReturn(parser);
        ParsedDocumentDTO parsed = ParsedDocumentDTO.builder()
                .documentNumber("123456789123")
                .name("Parveen")
                .dob("01/01/2000")
                .build();

        when(parser.parse(any())).thenReturn(parsed);
        AadhaarVerifyResponse response = new AadhaarVerifyResponse();
        response.setValid(true);

        when(aadhaarClient.verifyAadhaar(any(), any())).thenReturn(response);
        ApplicantResponseDTO applicant = new ApplicantResponseDTO();
        applicant.setAadhaarNumber("123456789123");

        when(applicantClient.getApplicant(any())).thenReturn(applicant);
        service.verifyDocument(event);
        ArgumentCaptor<DocumentVerificationEntity> captor = ArgumentCaptor.forClass(DocumentVerificationEntity.class);
        verify(repository).save(captor.capture());
        DocumentVerificationEntity saved = captor.getValue();

        assertEquals(VerificationStatus.VERIFIED, saved.getVerificationStatus());
        assertEquals("Parveen", saved.getExtractedName());
        assertEquals("01/01/2000", saved.getExtractedDob());
        assertEquals("123456789123", saved.getExtractedNumber());
        assertEquals(event.getDocumentId(), saved.getDocumentId());
    }
    @Test
    void verifyDocument_shouldFail_whenAadhaarMismatch() {
        File file = new File("test");
        when(s3DownloadService.download(any())).thenReturn(file);
        when(ocrService.extractText(file)).thenReturn("1234 5678 9999");
        when(parserFactory.getParser(any())).thenReturn(parser);
        ParsedDocumentDTO parsed = ParsedDocumentDTO.builder()
                .documentNumber("123456789999")
                .build();

        when(parser.parse(any())).thenReturn(parsed);
        AadhaarVerifyResponse response = new AadhaarVerifyResponse();
        response.setValid(true);

        when(aadhaarClient.verifyAadhaar(any(), any())).thenReturn(response);
        ApplicantResponseDTO applicant = new ApplicantResponseDTO();
        applicant.setAadhaarNumber("123456789123");

        when(applicantClient.getApplicant(any())).thenReturn(applicant);
        service.verifyDocument(event);
        ArgumentCaptor<DocumentVerificationEntity> captor = ArgumentCaptor.forClass(DocumentVerificationEntity.class);
        verify(repository).save(captor.capture());
        DocumentVerificationEntity saved = captor.getValue();
        assertEquals(VerificationStatus.FAILED, saved.getVerificationStatus());
    }
    @Test
    void verifyDocument_shouldHandleException() {
        when(s3DownloadService.download(any())).thenThrow(new S3DownloadException("S3 failed"));
        service.verifyDocument(event);
        ArgumentCaptor<DocumentVerificationEntity> captor = ArgumentCaptor.forClass(DocumentVerificationEntity.class);
        verify(repository).save(captor.capture());
        DocumentVerificationEntity saved = captor.getValue();
        assertEquals(VerificationStatus.FAILED, saved.getVerificationStatus());
        assertEquals(event.getDocumentId(), saved.getDocumentId());
    }
}