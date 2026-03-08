package com.example.Budget_Planning_Service.controller;

import com.example.Budget_Planning_Service.dto.AssetAssignRequest;
import com.example.Budget_Planning_Service.dto.AssetReplaceRequest;
import com.example.Budget_Planning_Service.dto.AssetStateUpdateRequest;
import com.example.Budget_Planning_Service.dto.AssetSummaryResponse;
import com.example.Budget_Planning_Service.dto.CategorySummaryResponse;
import com.example.Budget_Planning_Service.dto.CompanySummaryResponse;
import com.example.Budget_Planning_Service.dto.EmployeeBudgetResponse;
import com.example.Budget_Planning_Service.service.EmployeeBudgetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/budgets")
public class EmployeeBudgetController {

    private final EmployeeBudgetService service;

    public EmployeeBudgetController(EmployeeBudgetService service)
    {
        this.service = service;
    }

    @PostMapping("/assign")
    public ResponseEntity<EmployeeBudgetResponse> assignAsset(@Valid @RequestBody AssetAssignRequest request) {
        return ResponseEntity.ok(service.assignAsset(request));
    }

    @PostMapping("/replace")
    public ResponseEntity<EmployeeBudgetResponse> replaceAsset(@Valid @RequestBody AssetReplaceRequest request) {
        return ResponseEntity.ok(service.replaceAsset(request));
    }

    @PutMapping("/asset/state")
    public ResponseEntity<EmployeeBudgetResponse> updateAssetState(@Valid @RequestBody AssetStateUpdateRequest request) {
        return ResponseEntity.ok(service.updateAssetState(request));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<EmployeeBudgetResponse> getEmployeeBudget(@PathVariable Long employeeId) {
        return ResponseEntity.ok(service.getEmployeeBudget(employeeId));
    }

    @PostMapping("/employee/{employeeId}/exit")
    public ResponseEntity<EmployeeBudgetResponse> exitEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(service.exitEmployee(employeeId));
    }
    @GetMapping("/summary")
    public CompanySummaryResponse getCompanySummary() {
        return service.getCompanySummary();
    }

    @GetMapping("/asset/{assetId}/summary")
    public AssetSummaryResponse getAssetSummary(@PathVariable Long assetId) {
        return service.getAssetSummary(assetId);
    }

    @GetMapping("/category/{assetType}/summary")
    public CategorySummaryResponse getCategorySummary(@PathVariable String assetType) {
        return service.getCategorySummary(assetType);
    }

}