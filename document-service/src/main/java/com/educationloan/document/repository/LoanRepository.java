package com.educationloan.document.repository;

import com.educationloan.document.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<LoanEntity , Long> {
}
