package com.example.Budget_Planning_Service.repository;

import com.example.Budget_Planning_Service.model.AssetState;
import com.example.Budget_Planning_Service.model.entity.AssetBudget;
import com.example.Budget_Planning_Service.model.entity.EmployeeBudget;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AssetBudgetRepositoryTest {

    @Autowired
    private AssetBudgetRepository assetBudgetRepository;

    @Autowired
    private EmployeeBudgetRepository employeeBudgetRepository;

    @Test
    void testSaveAssetBudget() {

        EmployeeBudget employeeBudget = new EmployeeBudget();
        employeeBudget = employeeBudgetRepository.save(employeeBudget);

        AssetBudget assetBudget = new AssetBudget();
        assetBudget.setAssetId(10L);
        assetBudget.setEmployeeBudget(employeeBudget);
        assetBudget.setCostPrice(BigDecimal.valueOf(5000));
        assetBudget.setState(AssetState.ASSIGNED);

        AssetBudget saved = assetBudgetRepository.save(assetBudget);

        assertNotNull(saved.getId());
        assertEquals(10L, saved.getAssetId());
    }

    @Test
    void testFindByAssetId() {

        EmployeeBudget employeeBudget = employeeBudgetRepository.save(new EmployeeBudget());

        AssetBudget assetBudget = new AssetBudget();
        assetBudget.setAssetId(20L);
        assetBudget.setEmployeeBudget(employeeBudget);
        assetBudget.setCostPrice(BigDecimal.valueOf(7000));

        assetBudgetRepository.save(assetBudget);

        List<AssetBudget> result = assetBudgetRepository.findByAssetId(20L);

        assertFalse(result.isEmpty());
        assertEquals(20L, result.get(0).getAssetId());
    }

    @Test
    void testFindByEmployeeBudgetId() {

        EmployeeBudget employeeBudget = employeeBudgetRepository.save(new EmployeeBudget());

        AssetBudget assetBudget = new AssetBudget();
        assetBudget.setAssetId(30L);
        assetBudget.setEmployeeBudget(employeeBudget);
        assetBudget.setCostPrice(BigDecimal.valueOf(9000));

        assetBudgetRepository.save(assetBudget);

        List<AssetBudget> result =
                assetBudgetRepository.findByEmployeeBudgetId(employeeBudget.getId());

        assertFalse(result.isEmpty());
    }
}