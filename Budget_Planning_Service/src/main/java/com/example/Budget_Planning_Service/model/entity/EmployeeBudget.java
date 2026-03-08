package com.example.Budget_Planning_Service.model.entity;


import com.example.Budget_Planning_Service.model.BudgetStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
    @Table(name = "employee_budget")
    public class EmployeeBudget {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long employeeId;

        @Column(name = "total_budget", precision = 12, scale = 2)
        private BigDecimal totalBudget = BigDecimal.ZERO;

        @Enumerated(EnumType.STRING)
        private BudgetStatus status = BudgetStatus.ACTIVE;

        private Instant createdAt = Instant.now();
        private Instant updatedAt = Instant.now();

        @OneToMany(mappedBy = "employeeBudget", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<AssetBudget> assetBudgets = new ArrayList<>();


}
