package com.example.Budget_Planning_Service.dto;

import com.example.Budget_Planning_Service.model.AssetState;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetStateUpdateRequest {
    @NotNull
    private Long employeeId;
    @NotNull
    private Long assetId;
    @NotNull
    private AssetState state;
}
