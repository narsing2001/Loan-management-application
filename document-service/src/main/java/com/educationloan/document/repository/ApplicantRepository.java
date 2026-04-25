package com.educationloan.document.repository;

import com.educationloan.document.entity.ApplicantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ApplicantRepository extends JpaRepository<ApplicantEntity, Long> {
}