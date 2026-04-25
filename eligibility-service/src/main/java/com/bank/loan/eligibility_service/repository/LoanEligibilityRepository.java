package com.bank.loan.eligibility_service.repository;

import com.bank.loan.eligibility_service.entity.LoanEligibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoanEligibilityRepository  extends JpaRepository<LoanEligibility, Long> {
}
