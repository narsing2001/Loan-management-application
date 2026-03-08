package com.example.Budget_Planning_Service.repository;


import com.example.Budget_Planning_Service.model.entity.AssetCostPurchase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AssetCostPurchaseRepositoryTest {

    @Autowired
    private AssetCostPurchaseRepository repository;

    @Test
    void testSaveAssetCostPurchase() {

        AssetCostPurchase asset = new AssetCostPurchase();
        asset.setAssetId(1L);
        asset.setAssetType("Laptop");
        asset.setCostPrice(BigDecimal.valueOf(75000));

        AssetCostPurchase saved = repository.save(asset);

        assertNotNull(saved);
        assertEquals(1L, saved.getAssetId());
        assertEquals("Laptop", saved.getAssetType());
    }

    @Test
    void testFindById() {

        AssetCostPurchase asset = new AssetCostPurchase();
        asset.setAssetId(2L);
        asset.setAssetType("Monitor");
        asset.setCostPrice(BigDecimal.valueOf(15000));

        repository.save(asset);

        Optional<AssetCostPurchase> result = repository.findById(2L);

        assertTrue(result.isPresent());
        assertEquals("Monitor", result.get().getAssetType());
    }

    @Test
    void testFindAll() {

        AssetCostPurchase asset1 = new AssetCostPurchase(3L, "Keyboard", BigDecimal.valueOf(2000));
        AssetCostPurchase asset2 = new AssetCostPurchase(4L, "Mouse", BigDecimal.valueOf(1000));

        repository.save(asset1);
        repository.save(asset2);

        List<AssetCostPurchase> assets = repository.findAll();

        assertFalse(assets.isEmpty());
        assertTrue(assets.size() >= 2);
    }

    @Test
    void testDeleteById() {

        AssetCostPurchase asset = new AssetCostPurchase();
        asset.setAssetId(5L);
        asset.setAssetType("Printer");
        asset.setCostPrice(BigDecimal.valueOf(12000));

        repository.save(asset);

        repository.deleteById(5L);

        Optional<AssetCostPurchase> result = repository.findById(5L);

        assertTrue(result.isEmpty());
    }
}