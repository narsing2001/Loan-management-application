package com.example.Budget_Planning_Service.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "asset_cost_saver")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetCostPurchase {

    @Id
    private Long assetId;

    private String assetType;

    @Column(name = "cost_price", precision = 12, scale = 2)
    private BigDecimal costPrice;

}
