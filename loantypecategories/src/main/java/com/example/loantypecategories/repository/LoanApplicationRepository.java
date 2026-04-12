package com.example.loantypecategories.repository;

import com.example.loantypecategories.constant.ApplicationStatus;
import com.example.loantypecategories.constant.CourseType;
import com.example.loantypecategories.constant.Gender;
import com.example.loantypecategories.constant.LoanType;
import com.example.loantypecategories.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {



    Optional<LoanApplication> findByApplicantEmail(String applicantEmail);

    List<LoanApplication> findByStatus(ApplicationStatus status);

    List<LoanApplication> findByLoanType(LoanType loanType);

    List<LoanApplication> findByCourseType(CourseType courseType);

    List<LoanApplication> findByGender(Gender gender);

    List<LoanApplication> findByStatusAndLoanType(ApplicationStatus status, LoanType loanType);



    boolean existsByApplicantEmail(String applicantEmail);
    // ---- Amount-based queries ----

    List<LoanApplication> findByApprovedAmountBetween(BigDecimal min, BigDecimal max);

    @Query("SELECT a FROM LoanApplication a WHERE a.requestedAmount > a.approvedAmount")
    List<LoanApplication> findPartiallyApprovedLoans();



    List<LoanApplication> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);



    @Query("SELECT COUNT(a) FROM LoanApplication a WHERE a.status = :status")
    long countByStatus(@Param("status") ApplicationStatus status);

    @Query("SELECT SUM(a.approvedAmount) FROM LoanApplication a WHERE a.status = 'APPROVED'")
    BigDecimal totalApprovedLoanAmount();

    @Query("SELECT AVG(a.interestRate) FROM LoanApplication a WHERE a.loanType = :loanType")
    BigDecimal averageInterestRateByLoanType(@Param("loanType") LoanType loanType);

    // ---- Female discount tracking ----

    @Query("SELECT a FROM LoanApplication a WHERE a.gender = 'FEMALE' AND a.status = 'APPROVED'")
    List<LoanApplication> findApprovedFemaleApplicants();
}
