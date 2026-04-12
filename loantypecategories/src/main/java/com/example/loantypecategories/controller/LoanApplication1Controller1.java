package com.example.loantypecategories.controller;

import com.example.loantypecategories.constant.ApplicationStatus;
import com.example.loantypecategories.constant.CourseType;
import com.example.loantypecategories.constant.LoanType;
import com.example.loantypecategories.constant.Gender;
import com.example.loantypecategories.entity.entit1.LoanApplication1;

import com.example.loantypecategories.service.serv1.LoanApplication1Service2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loan-applications1")
@RequiredArgsConstructor
public class LoanApplication1Controller1 {

    private final LoanApplication1Service2 service;

    // --- Apply for loan ---
    @PostMapping
    public ResponseEntity<LoanApplication1> apply(@RequestBody LoanApplication1 request) {
        LoanApplication1 saved = service.apply(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // --- Get all ---
    @GetMapping
    public ResponseEntity<List<LoanApplication1>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // --- Get by ID ---
    @GetMapping("/{id}")
    public ResponseEntity<LoanApplication1> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // --- Get by Email ---
    @GetMapping("/email/{email}")
    public ResponseEntity<LoanApplication1> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.getByEmail(email));
    }

    // --- Filter ---
    @GetMapping("/filter")
    public ResponseEntity<List<LoanApplication1>> filter(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) LoanType loanType,
            @RequestParam(required = false) CourseType courseType,
            @RequestParam(required = false) Gender gender) {

        if (status != null) return ResponseEntity.ok(service.getByStatus(status));
        if (loanType != null) return ResponseEntity.ok(service.getByLoanType(loanType));
        if (courseType != null) return ResponseEntity.ok(service.getByCourseType(courseType));
        if (gender != null) return ResponseEntity.ok(service.getByGender(gender));

        return ResponseEntity.ok(service.getAll());
    }

    // --- Update status ---
    @PatchMapping("/{id}/status")
    public ResponseEntity<LoanApplication1> updateStatus(
            @PathVariable Long id, @RequestBody StatusUpdateRequest body) {
        LoanApplication1 updated = service.updateStatus(id, body.status());
        return ResponseEntity.ok(updated);
    }

    // --- Delete application ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Extra: Count applications by status ---
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countByStatus(@PathVariable ApplicationStatus status) {
        return ResponseEntity.ok(service.countByStatus(status));
    }

    // DTO for status update
    public record StatusUpdateRequest(ApplicationStatus status) {}
}
