package com.example.Budget_Planning_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanySummaryResponse {
    BigDecimal totalBudget;
    int totalAssets;
}
