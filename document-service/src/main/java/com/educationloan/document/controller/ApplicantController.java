package com.educationloan.document.controller;
import com.educationloan.document.dto.ApplicantResponseDTO;
import com.educationloan.document.service.ApplicantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applicants")
@RequiredArgsConstructor
public class ApplicantController {
    private final ApplicantService applicantService;

    @GetMapping("/{id}")
    public ApplicantResponseDTO getApplicant(@PathVariable Long id) {
        return applicantService.getApplicantById(id);
    }
}