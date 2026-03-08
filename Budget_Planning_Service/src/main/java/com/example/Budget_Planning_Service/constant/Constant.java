package com.example.Budget_Planning_Service.constant;

public interface Constant {
    String EMPLOYEE_NOT_FOUND ="Employee not found";
    String ASSET_COST_NOT_FOUND ="Asset cost not found for assetId";
    String OLD_ASSET_NOT_FOUND = "Old assigned asset not found";
    String NEW_ASSET_COST_NOT_FOUND ="New asset cost not found";
    String ASSET_ENTRY_NOT_FOUND ="Asset entry not found";
    String ASSET_ALREADY_ASSIGNED ="Asset already assigned to this employee";
    String EMPLOYEE_EXITED_ASSIGN_NOT_ALLOWED ="Cannot assign asset to exited employee";
    String INVALID_STATE_TRANSITION ="Cannot transition from DECOMMISSIONED to ASSIGNED";
    String ASSIGN_BUDGET_EXCEEDED = "Assigning asset exceeds allowed employee budget";
}
