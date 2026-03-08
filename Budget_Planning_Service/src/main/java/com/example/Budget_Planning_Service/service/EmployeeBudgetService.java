package com.example.Budget_Planning_Service.service;

import com.example.Budget_Planning_Service.dto.*;

public interface EmployeeBudgetService {

    EmployeeBudgetResponse assignAsset(AssetAssignRequest request);

    EmployeeBudgetResponse replaceAsset(AssetReplaceRequest request);

    EmployeeBudgetResponse updateAssetState(AssetStateUpdateRequest request);

    EmployeeBudgetResponse getEmployeeBudget(Long employeeId);

    EmployeeBudgetResponse exitEmployee(Long employeeId);

    CompanySummaryResponse getCompanySummary();

    AssetSummaryResponse getAssetSummary(Long assetId);

    CategorySummaryResponse getCategorySummary(String assetType);
}
