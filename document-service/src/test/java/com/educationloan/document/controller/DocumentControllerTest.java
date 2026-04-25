package com.educationloan.document.controller;
import com.educationloan.document.dto.DocumentResponseDTO;
import com.educationloan.document.enumConst.ApplicantType;
import com.educationloan.document.enumConst.DocumentType;
import com.educationloan.document.globalExceptionHandling.CustomException.*;
import com.educationloan.document.globalExceptionHandling.GlobalExceptionHandler;
import com.educationloan.document.service.DocumentUploadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(DocumentController.class)
@Import(GlobalExceptionHandler.class)
@Slf4j
class DocumentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentUploadService documentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void uploadDocument_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "dummy content".getBytes());

        DocumentResponseDTO responseDTO = new DocumentResponseDTO();
        responseDTO.setId(UUID.randomUUID());
        responseDTO.setFilename("test.pdf");
        responseDTO.setContentType("application/pdf");
        responseDTO.setFileSize(100L);
        responseDTO.setDocType(DocumentType.AADHAAR);
        responseDTO.setApplicantType(ApplicantType.STUDENT);
        responseDTO.setDownloadUrl("http://download-url");
        responseDTO.setUploadedAt(Instant.now());
        Mockito.when(documentService.uploadDocument(any(), anyLong(), any(), any())).thenReturn(responseDTO);
        mockMvc.perform(multipart("/api/documents/upload")
                        .file(file)
                        .param("applicantId", "1")
                        .param("docType", "AADHAAR")
                        .param("studyLocationType", "DOMESTIC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filename").value("test.pdf"))
                .andExpect(jsonPath("$.docType").value("AADHAAR"))
                .andExpect(jsonPath("$.applicantType").value("STUDENT"));

        Mockito.verify(documentService, times(1))
                .uploadDocument(any(), anyLong(), any(), any());
    }

    @Test
    void uploadDocument_invalidFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.exe", "application/octet-stream", "dummy".getBytes());
        Mockito.when(documentService.uploadDocument(any(), anyLong(), any(), any())).thenThrow(new InvalidFileException("Invalid file"));
        mockMvc.perform(multipart("/api/documents/upload")
                        .file(file)
                        .param("applicantId", "1")
                        .param("docType", "AADHAAR")
                        .param("studyLocationType", "DOMESTIC"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid file"));
        Mockito.verify(documentService, times(1)).uploadDocument(any(), anyLong(), any(), any());
    }
    @Test
    void uploadDocument_applicantNotAllowed() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "dummy".getBytes());
        Mockito.when(documentService.uploadDocument(any(), anyLong(), any(), any())).thenThrow(new ApplicantNotAllowedException("Not allowed"));
        mockMvc.perform(multipart("/api/documents/upload")
                        .file(file)
                        .param("applicantId", "1")
                        .param("docType", "AADHAAR")
                        .param("studyLocationType", "DOMESTIC"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Not allowed"));
        Mockito.verify(documentService, times(1))
                .uploadDocument(any(), anyLong(), any(), any());
    }

    @Test
    void uploadDocument_s3UploadException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "dummy".getBytes());
        Mockito.when(documentService.uploadDocument(any(), anyLong(), any(), any())).thenThrow(new S3UploadException("S3 upload failed"));
        mockMvc.perform(multipart("/api/documents/upload")
                        .file(file)
                        .param("applicantId", "1")
                        .param("docType", "AADHAAR")
                        .param("studyLocationType", "DOMESTIC"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("S3 upload failed"));
    }

    @Test
    void uploadDocument_missingParams() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "dummy".getBytes());
        mockMvc.perform(multipart("/api/documents/upload")
                        .file(file))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadDocument_missingFile() throws Exception {
        mockMvc.perform(multipart("/api/documents/upload")
                        .param("applicantId", "1")
                        .param("docType", "AADHAAR")
                        .param("studyLocationType", "DOMESTIC"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadDocument_invalidApplicantId() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "dummy".getBytes());
        mockMvc.perform(multipart("/api/documents/upload")
                        .file(file)
                        .param("applicantId", "abc")
                        .param("docType", "AADHAAR")
                        .param("studyLocationType", "DOMESTIC"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequiredApplicantTypes_success() throws Exception {
        Mockito.when(documentService.getRequiredApplicantTypes(anyLong(), any())).thenReturn(Set.of(ApplicantType.STUDENT, ApplicantType.CO_APPLICANT));
        mockMvc.perform(get("/api/documents/required")
                        .param("loanId", "1")
                        .param("studyLocationType", "DOMESTIC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        Mockito.verify(documentService).getRequiredApplicantTypes(anyLong(), any());
    }

    @Test
    void getRequiredApplicantTypes_loanNotFound() throws Exception {
        Mockito.when(documentService.getRequiredApplicantTypes(anyLong(), any())).thenThrow(new LoanNotFoundException("Loan not found"));
        mockMvc.perform(get("/api/documents/required")
                        .param("loanId", "1")
                        .param("studyLocationType", "DOMESTIC"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Loan not found"));
    }

    @Test
    void getDownloadUrl_success() throws Exception {
        UUID documentId = UUID.randomUUID();
        Mockito.when(documentService.getDownloadUrl(any(), anyLong())).thenReturn("http://download-url");
        mockMvc.perform(get("/api/documents/{documentId}/download-url", documentId)
                        .param("loanId", "1"))
                .andExpect(status().isOk());
        Mockito.verify(documentService).getDownloadUrl(any(), anyLong());
    }

    @Test
    void getDownloadUrl_documentNotFound() throws Exception {
        UUID documentId = UUID.randomUUID();
        Mockito.when(documentService.getDownloadUrl(any(), anyLong())).thenThrow(new DocumentNotFoundException("Document not found"));
        mockMvc.perform(get("/api/documents/{documentId}/download-url", documentId)
                        .param("loanId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Document not found"));
    }
    @Test
    void uploadDocument_s3PresignedUrlException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "dummy".getBytes());
        Mockito.when(documentService.uploadDocument(any(), anyLong(), any(), any())).thenThrow(new S3PresignedUrlException("Presigned URL failed"));
        mockMvc.perform(multipart("/api/documents/upload")
                        .file(file)
                        .param("applicantId", "1")
                        .param("docType", "AADHAAR")
                        .param("studyLocationType", "DOMESTIC"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Presigned URL failed"));
    }
    @Test
    void uploadDocument_s3KeyNotFoundException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "dummy".getBytes());
        Mockito.when(documentService.uploadDocument(any(), anyLong(), any(), any())).thenThrow(new S3KeyNotFoundException("Key not found"));
        mockMvc.perform(multipart("/api/documents/upload")
                        .file(file)
                        .param("applicantId", "1")
                        .param("docType", "AADHAAR")
                        .param("studyLocationType", "DOMESTIC"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Key not found"));
    }
}