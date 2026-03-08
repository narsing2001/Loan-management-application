package com.example.Budget_Planning_Service.utility;

import com.example.Budget_Planning_Service.model.AssetState;
import com.example.Budget_Planning_Service.model.entity.AssetBudget;
import com.example.Budget_Planning_Service.model.entity.EmployeeBudget;

import java.math.BigDecimal;
import java.time.Instant;

public final class EmployeeBudgetUtil {
    private EmployeeBudgetUtil(){

    }
    public static void addAssetBudget(AssetBudget assetBudget, EmployeeBudget employeeBudget) {
        employeeBudget.getAssetBudgets().add(assetBudget);
        assetBudget.setEmployeeBudget(employeeBudget);
        recalcTotalBudget(employeeBudget);
    }

    public static void removeAssetBudget(EmployeeBudget employeeBudget,
                                         AssetBudget assetBudget) {
        employeeBudget.getAssetBudgets().remove(assetBudget);
        assetBudget.setEmployeeBudget(null);
        recalcTotalBudget(employeeBudget);
    }
    public static void recalcTotalBudget(EmployeeBudget employeeBudget) {
        BigDecimal total = employeeBudget.getAssetBudgets().stream()
                .filter(a -> a.getState() == AssetState.ASSIGNED
                        || a.getState() == AssetState.REPLACED)
                .map(AssetBudget::getCostPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        employeeBudget.setTotalBudget(total);
        employeeBudget.setUpdatedAt(Instant.now());
    }
}
