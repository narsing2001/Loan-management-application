package com.educationloan.document.controller;

import com.educationloan.document.dto.ApplicantResponseDTO;
import com.educationloan.document.entity.ApplicantEntity;
import com.educationloan.document.repository.ApplicantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applicants")
@RequiredArgsConstructor
public class ApplicantController {

    private final ApplicantRepository applicantRepository;

    @GetMapping("/{id}")
    public ApplicantResponseDTO getApplicant(@PathVariable Long id) {

        ApplicantEntity applicant =
                applicantRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Applicant not found"));

        return ApplicantResponseDTO.builder()
                .id(applicant.getId())
                .name(applicant.getName())
                .dob(applicant.getDob())
                .aadhaarNumber(applicant.getAadhaarNumber())
                .build();
    }
}