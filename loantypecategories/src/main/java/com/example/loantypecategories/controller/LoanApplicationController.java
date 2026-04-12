package com.example.loantypecategories.controller;



import com.example.loantypecategories.constant.ApplicationStatus;
import com.example.loantypecategories.constant.CourseType;
import com.example.loantypecategories.constant.LoanType;
import com.example.loantypecategories.dto.LoanApplicationRequestDTO;
import com.example.loantypecategories.dto.LoanApplicationResponseDTO;

import com.example.loantypecategories.service.LoanAppService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanApplicationController {

    private final LoanAppService service;

    @PostMapping
    public ResponseEntity<LoanApplicationResponseDTO> applyForLoan(@Valid @RequestBody LoanApplicationRequestDTO request) {
        LoanApplicationResponseDTO response = service.applyForLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<List<LoanApplicationResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanApplicationResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<LoanApplicationResponseDTO> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.getByEmail(email));
    }
    @GetMapping("/filter")
    public ResponseEntity<List<LoanApplicationResponseDTO>> filter(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) LoanType loanType,
            @RequestParam(required = false) CourseType courseType) {


        if (status != null) {
            return ResponseEntity.ok(service.getByStatus(status));
        }
        if (loanType != null) {
            return ResponseEntity.ok(service.getByLoanType(loanType));
        }
        if (courseType != null) {
            return ResponseEntity.ok(service.getByCourseType(courseType));
        }

        return ResponseEntity.ok(service.getAll());
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<LoanApplicationResponseDTO> updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest body) {

        return ResponseEntity.ok(service.updateStatus(id, body.status()));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelApplication(@PathVariable Long id) {
        service.cancelApplication(id);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/stats")
    public ResponseEntity<LoanAppService.LoanSummaryStats> getStats() {
        return ResponseEntity.ok(service.getSummaryStats());
    }


    public record StatusUpdateRequest(ApplicationStatus status) {

    }
}
