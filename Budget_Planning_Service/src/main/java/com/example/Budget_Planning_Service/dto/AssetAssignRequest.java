package com.example.Budget_Planning_Service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetAssignRequest {
    @NotNull
    private Long employeeId;
    @NotNull
    private Long assetId;
}