package com.example.Budget_Planning_Service.controller;

import com.example.Budget_Planning_Service.dto.*;
import com.example.Budget_Planning_Service.exception.BudgetExceededException;
import com.example.Budget_Planning_Service.exception.GlobalExeptionHandler.GlobalExceptionHandler;
import com.example.Budget_Planning_Service.exception.ResourceNotFoundException;
import com.example.Budget_Planning_Service.model.AssetState;
import com.example.Budget_Planning_Service.service.EmployeeBudgetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeBudgetController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeBudgetControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EmployeeBudgetService service;

    @Autowired
    ObjectMapper mapper;

    private EmployeeBudgetResponse mockBudget() {
        return new EmployeeBudgetResponse(
                1001L,
                new BigDecimal("50000"),
                List.of("2001:ASSIGNED:50000")
        );
    }

    // ---------------- ASSIGN ----------------

    @Test
    void assignAsset_success() throws Exception {
        Mockito.when(service.assignAsset(Mockito.any()))
                .thenReturn(mockBudget());

        mockMvc.perform(post("/api/v1/budgets/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new AssetAssignRequest(1001L, 2001L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(1001));
    }

    @Test
    void assignAsset_validationFail() throws Exception {
        mockMvc.perform(post("/api/v1/budgets/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void assignAsset_notFound() throws Exception {
        Mockito.when(service.assignAsset(Mockito.any()))
                .thenThrow(new ResourceNotFoundException("Asset cost not found"));

        mockMvc.perform(post("/api/v1/budgets/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new AssetAssignRequest(1L, 99L))))
                .andExpect(status().isNotFound());
    }

    @Test
    void assignAsset_budgetExceeded() throws Exception {
        Mockito.when(service.assignAsset(Mockito.any()))
                .thenThrow(new BudgetExceededException("Budget exceeded"));

        mockMvc.perform(post("/api/v1/budgets/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new AssetAssignRequest(1L, 2L))))
                .andExpect(status().isBadRequest());
    }

    // ---------------- REPLACE ----------------

    @Test
    void replaceAsset_success() throws Exception {
        Mockito.when(service.replaceAsset(Mockito.any()))
                .thenReturn(mockBudget());

        mockMvc.perform(post("/api/v1/budgets/replace")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new AssetReplaceRequest(1L, 10L, 20L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBudget").value(50000));
    }

    // ---------------- UPDATE STATE ----------------

    @Test
    void updateAssetState_success() throws Exception {
        Mockito.when(service.updateAssetState(Mockito.any()))
                .thenReturn(mockBudget());

        mockMvc.perform(put("/api/v1/budgets/asset/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(
                                new AssetStateUpdateRequest(1L, 2001L, AssetState.MAINTENANCE))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(1001));
    }

    // ---------------- GET EMPLOYEE ----------------

    @Test
    void getEmployee_success() throws Exception {
        Mockito.when(service.getEmployeeBudget(1001L))
                .thenReturn(mockBudget());

        mockMvc.perform(get("/api/v1/budgets/employee/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBudget").value(50000));
    }

    @Test
    void getEmployee_notFound() throws Exception {
        Mockito.when(service.getEmployeeBudget(9L))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/budgets/employee/9"))
                .andExpect(status().isNotFound());
    }

    // ---------------- EXIT ----------------

    @Test
    void exitEmployee_success() throws Exception {
        Mockito.when(service.exitEmployee(1L)).thenReturn(mockBudget());

        mockMvc.perform(post("/api/v1/budgets/employee/1/exit"))
                .andExpect(status().isOk());
    }

    // ---------------- SUMMARY ----------------

    @Test
    void getCompanySummary_success() throws Exception {
        Mockito.when(service.getCompanySummary())
                .thenReturn(new CompanySummaryResponse(new BigDecimal("100000"), 5));

        mockMvc.perform(get("/api/v1/budgets/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAssets").value(5));
    }

    @Test
    void getAssetSummary_success() throws Exception {
        Mockito.when(service.getAssetSummary(1L))
                .thenReturn(new AssetSummaryResponse(1L, new BigDecimal("5000"), 2));

        mockMvc.perform(get("/api/v1/budgets/asset/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(2));
    }

    @Test
    void getCategorySummary_success() throws Exception {
        Mockito.when(service.getCategorySummary("LAPTOP"))
                .thenReturn(new CategorySummaryResponse("LAPTOP", 3, new BigDecimal("9000")));

        mockMvc.perform(get("/api/v1/budgets/category/LAPTOP/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetType").value("LAPTOP"));
    }
}
