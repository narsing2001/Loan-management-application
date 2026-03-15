package com.educationloan.document.service;

import com.educationloan.document.dto.DocumentResponseDTO;
import com.educationloan.document.enumConst.ApplicantType;
import com.educationloan.document.enumConst.DocumentType;
import com.educationloan.document.enumConst.StudyLocationType;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface DocumentUploadService {

    DocumentResponseDTO uploadDocument(
            MultipartFile file,
            Long applicantId,
            DocumentType docType,
            StudyLocationType studyLocationType
    );

    String getDownloadUrl(UUID documentId, Long loanId);

     Set<ApplicantType> getRequiredApplicantTypes(Long loanId,StudyLocationType studyLocationType);
}