package com.document.verification.service.client;

import com.document.verification.service.dto.ApplicantResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "document-service", url = "http://localhost:8080")
public interface ApplicantClient {

    @GetMapping("/applicants/{id}")
    ApplicantResponseDTO getApplicant(@PathVariable Long id);
}