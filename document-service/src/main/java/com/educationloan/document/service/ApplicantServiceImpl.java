package com.educationloan.document.service;
import com.educationloan.document.dto.ApplicantResponseDTO;
import com.educationloan.document.entity.ApplicantEntity;
import com.educationloan.document.globalExceptionHandling.CustomException.ApplicantNotFoundException;
import com.educationloan.document.repository.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicantServiceImpl implements ApplicantService {
    private final ApplicantRepository applicantRepository;

    @Override
    public ApplicantResponseDTO getApplicantById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Applicant ID cannot be null");
        }
        ApplicantEntity applicant = applicantRepository.findById(id)
                .orElseThrow(() -> new ApplicantNotFoundException("Applicant not found with id: " + id));
        return mapToDTO(applicant);
    }
    private ApplicantResponseDTO mapToDTO(ApplicantEntity applicant) {
        return ApplicantResponseDTO.builder()
                .id(applicant.getId())
                .name(applicant.getName())
                .dob(applicant.getDob())
                .aadhaarNumber(applicant.getAadhaarNumber())
                .build();
    }
}