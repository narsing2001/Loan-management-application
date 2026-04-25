package com.educationloan.document.controller;

import com.educationloan.document.dto.DocumentResponseDTO;
import com.educationloan.document.enumConst.ApplicantType;
import com.educationloan.document.enumConst.DocumentType;
import com.educationloan.document.enumConst.StudyLocationType;
import com.educationloan.document.service.DocumentUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentUploadService documentService;

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponseDTO> uploadDocument(
            @RequestParam MultipartFile file,
            @RequestParam Long applicantId,
            @RequestParam DocumentType docType,
            @RequestParam StudyLocationType studyLocationType) {
        DocumentResponseDTO response = documentService.uploadDocument(
                file,
                applicantId,
                docType,
                studyLocationType);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/required")
    public ResponseEntity<List<ApplicantType>> getRequiredApplicantTypes(
            @RequestParam Long loanId,@RequestParam StudyLocationType studyLocationType) {

        Set<ApplicantType> types = documentService.getRequiredApplicantTypes(loanId,studyLocationType);
        return ResponseEntity.ok(new ArrayList<>(types));
    }

    @GetMapping("/{documentId}/download-url")
    public ResponseEntity<String> getDownloadUrl(@PathVariable UUID documentId, @RequestParam Long loanId) {
        String url = documentService.getDownloadUrl(documentId, loanId);
        return ResponseEntity.ok(url);
    }
}
