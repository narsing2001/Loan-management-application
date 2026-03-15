package com.educationloan.document.repository;

import com.educationloan.document.entity.ApplicantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicantRepository
        extends JpaRepository<ApplicantEntity, Long> {

    List<ApplicantEntity> findByLoanId(Long loanId);
}