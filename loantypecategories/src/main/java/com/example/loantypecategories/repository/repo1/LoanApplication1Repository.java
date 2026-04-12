package com.example.loantypecategories.repository.repo1;



import com.example.loantypecategories.constant.ApplicationStatus;
import com.example.loantypecategories.constant.CourseType;
import com.example.loantypecategories.constant.Gender;
import com.example.loantypecategories.constant.LoanType;
import com.example.loantypecategories.entity.entit1.LoanApplication1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanApplication1Repository extends JpaRepository<LoanApplication1, Long> {

    // --- Find by applicant email ---
    Optional<LoanApplication1> findByApplicantEmail(String applicantEmail);

    // --- Filter by status ---
    List<LoanApplication1> findByStatus(ApplicationStatus status);

    // --- Filter by loan type ---
    List<LoanApplication1> findByLoanType(LoanType loanType);

    // --- Filter by course type ---
    List<LoanApplication1> findByCourseType(CourseType courseType);

    // --- Filter by gender ---
    List<LoanApplication1> findByGender(Gender gender);

    // --- Count by status ---
    long countByStatus(ApplicationStatus status);

    // --- Count by loan type ---
    long countByLoanType(LoanType loanType);

    // --- Count by course type ---
    long countByCourseType(CourseType courseType);

    // --- Count by gender ---
    long countByGender(Gender gender);

    // --- Find by bank type ---
    List<LoanApplication1> findByBankType(com.example.loantypecategories.constant.cont1.BankType bankType);

    // --- Find active applications (status = PENDING or APPROVED) ---
    List<LoanApplication1> findByStatusIn(List<ApplicationStatus> statuses);

    // --- Find applications with subsidy eligibility ---
    List<LoanApplication1> findBySubsidyEligibleTrue();

    // --- Find applications with collateral provided ---
    List<LoanApplication1> findByCollateralProvidedTrue();

    // --- Find applications created after a certain date ---
    List<LoanApplication1> findByCreatedAtAfter(java.time.LocalDateTime date);

    // --- Find applications updated after a certain date ---
    List<LoanApplication1> findByUpdatedAtAfter(java.time.LocalDateTime date);
}
