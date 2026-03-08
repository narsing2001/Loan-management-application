package com.example.Budget_Planning_Service.repository;


import com.example.Budget_Planning_Service.model.entity.EmployeeBudget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeBudgetRepository extends JpaRepository<EmployeeBudget, Long> {
    Optional<EmployeeBudget> findByEmployeeId(Long employeeId);
}