package com.example.Budget_Planning_Service.repository;


import com.example.Budget_Planning_Service.model.BudgetStatus;
import com.example.Budget_Planning_Service.model.entity.EmployeeBudget;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeBudgetRepositoryTest {

    @Autowired
    private EmployeeBudgetRepository repository;

    @Test
    void testSaveEmployeeBudget() {

        EmployeeBudget budget = new EmployeeBudget();
        budget.setEmployeeId(101L);
        budget.setTotalBudget(BigDecimal.valueOf(100000));
        budget.setStatus(BudgetStatus.ACTIVE);

        EmployeeBudget saved = repository.save(budget);

        assertNotNull(saved.getId());
        assertEquals(101L, saved.getEmployeeId());
    }

    @Test
    void testFindByEmployeeId() {

        EmployeeBudget budget = new EmployeeBudget();
        budget.setEmployeeId(200L);
        budget.setTotalBudget(BigDecimal.valueOf(50000));

        repository.save(budget);

        Optional<EmployeeBudget> result = repository.findByEmployeeId(200L);

        assertTrue(result.isPresent());
        assertEquals(200L, result.get().getEmployeeId());
    }

    @Test
    void testFindAllEmployeeBudgets() {

        EmployeeBudget budget1 = new EmployeeBudget();
        budget1.setEmployeeId(300L);
        budget1.setTotalBudget(BigDecimal.valueOf(70000));

        EmployeeBudget budget2 = new EmployeeBudget();
        budget2.setEmployeeId(301L);
        budget2.setTotalBudget(BigDecimal.valueOf(80000));

        repository.save(budget1);
        repository.save(budget2);

        List<EmployeeBudget> budgets = repository.findAll();

        assertFalse(budgets.isEmpty());
        assertTrue(budgets.size() >= 2);
    }

    @Test
    void testDeleteEmployeeBudget() {

        EmployeeBudget budget = new EmployeeBudget();
        budget.setEmployeeId(400L);
        budget.setTotalBudget(BigDecimal.valueOf(90000));

        EmployeeBudget saved = repository.save(budget);

        repository.deleteById(saved.getId());

        Optional<EmployeeBudget> result = repository.findById(saved.getId());

        assertTrue(result.isEmpty());
    }
}
