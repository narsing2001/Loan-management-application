package com.loanmanagement.repository;

import com.loanmanagement.entity.Overdraft;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OverdraftRepository extends JpaRepository<Overdraft, Long> {
    Optional<Overdraft> findByLoanId(Long loanId);
}
