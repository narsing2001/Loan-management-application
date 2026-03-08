package com.example.Budget_Planning_Service.repository;

import java.util.*;
import com.example.Budget_Planning_Service.model.entity.AssetBudget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetBudgetRepository extends JpaRepository<AssetBudget, Long> {
    List<AssetBudget> findByAssetId(Long assetId);
    List<AssetBudget> findByEmployeeBudgetId(Long employeeBudgetId);
}
