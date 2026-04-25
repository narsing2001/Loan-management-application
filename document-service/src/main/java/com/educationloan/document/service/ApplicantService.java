package com.educationloan.document.service;
import com.educationloan.document.dto.ApplicantResponseDTO;

public interface ApplicantService {
    ApplicantResponseDTO getApplicantById(Long id);
}
