package com.educationloan.document.controller;

import com.educationloan.document.dto.DocumentResponseDTO;
import com.educationloan.document.enumConst.ApplicantType;
import com.educationloan.document.enumConst.DocumentType;
import com.educationloan.document.enumConst.StudyLocationType;
import com.educationloan.document.service.DocumentUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class DocumentControllerTest {

    @Mock
    private DocumentUploadService documentService;

    @InjectMocks
    private DocumentController documentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadDocument_Positive() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Test content".getBytes()
        );

        DocumentResponseDTO mockResponse = new DocumentResponseDTO();
        mockResponse.setId(UUID.randomUUID());
        mockResponse.setFilename("test.pdf");
        mockResponse.setContentType("application/pdf");
        mockResponse.setFileSize(1024L);
        mockResponse.setDownloadUrl("https://s3bucket/documents/test.pdf"); // use downloadUrl instead of s3Key

        when(documentService.uploadDocument(any(), anyLong(), any(DocumentType.class), any(StudyLocationType.class)))
                .thenReturn(mockResponse);

        ResponseEntity<DocumentResponseDTO> response = documentController.uploadDocument(
                file, 1L, DocumentType.AADHAAR, StudyLocationType.DOMESTIC
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test.pdf", response.getBody().getFilename());
        assertEquals("https://s3bucket/documents/test.pdf", response.getBody().getDownloadUrl());

        System.out.println("testUploadDocument_Positive passed");
    }

    @Test
    void testGetRequiredApplicantTypes_Positive() {
        Set<ApplicantType> mockTypes = new HashSet<>();
        mockTypes.add(ApplicantType.STUDENT); // example applicant type

        when(documentService.getRequiredApplicantTypes(anyLong(), any(StudyLocationType.class)))
                .thenReturn(mockTypes);

        ResponseEntity<List<ApplicantType>> response = documentController.getRequiredApplicantTypes(
                1L, StudyLocationType.DOMESTIC
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(ApplicantType.STUDENT, response.getBody().get(0));

        System.out.println("testGetRequiredApplicantTypes_Positive passed");
    }

    @Test
    void testGetDownloadUrl_Positive() {
        UUID documentId = UUID.randomUUID();
        String mockUrl = "https://s3bucket/documents/test.pdf";

        when(documentService.getDownloadUrl(any(UUID.class), anyLong()))
                .thenReturn(mockUrl);

        ResponseEntity<String> response = documentController.getDownloadUrl(documentId, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUrl, response.getBody());

        System.out.println("testGetDownloadUrl_Positive passed");
    }
}