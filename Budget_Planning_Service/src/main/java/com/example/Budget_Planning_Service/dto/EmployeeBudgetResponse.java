package com.example.Budget_Planning_Service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeBudgetResponse {
    private Long employeeId;
    private BigDecimal totalBudget;
    private List<String> assets;

}
