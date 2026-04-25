package com.educationloan.document.repository;
import com.educationloan.document.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {
    Optional<DocumentEntity> findByIdAndApplicant_Loan_Id(UUID id, Long loanId);
}