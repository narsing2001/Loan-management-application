package com.example.LoanTypeStrategyInterest.repository.repo1;

import com.example.LoanTypeStrategyInterest.entity.entity1.UserApplicationDetail;
import com.example.LoanTypeStrategyInterest.enums.enum1.ApplicationStatus;
import com.example.LoanTypeStrategyInterest.enums.enum1.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserApplicationRepository extends JpaRepository<UserApplicationDetail, UUID> {

    Optional<UserApplicationDetail> findByApplicantEmail(String applicantEmail);
    List<UserApplicationDetail> findByApplicationStatus(ApplicationStatus status);
    List<UserApplicationDetail> findByLoanType(LoanType loanType);

}
