package com.example.Budget_Planning_Service.model.entity;

import com.example.Budget_Planning_Service.model.AssetState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "asset_budget")
public class  AssetBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long assetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_budget_id")
    private EmployeeBudget employeeBudget;

    @Column(name = "cost_price", precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Enumerated(EnumType.STRING)
    private AssetState state = AssetState.ASSIGNED;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void onUpdate() { updatedAt = Instant.now();
    }


}
