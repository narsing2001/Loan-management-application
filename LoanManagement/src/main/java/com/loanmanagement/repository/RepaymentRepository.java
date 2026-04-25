package com.loanmanagement.repository;

import com.loanmanagement.entity.RepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RepaymentRepository extends JpaRepository<RepaymentSchedule, Long> {

    List<RepaymentSchedule> findByLoanIdOrderByMonthAsc(Long loanId);

    Optional<RepaymentSchedule> findByLoanIdAndMonth(Long loanId, int month);

    boolean existsByLoanId(Long loanId);
}