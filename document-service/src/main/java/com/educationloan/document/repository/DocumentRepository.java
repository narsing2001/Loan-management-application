package com.educationloan.document.repository;

import com.educationloan.document.entity.DocumentEntity;
import com.educationloan.document.enumConst.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository
        extends JpaRepository<DocumentEntity, UUID> {

    List<DocumentEntity> findByApplicant_Id(Long applicantId);

    Optional<DocumentEntity> findByIdAndApplicant_Loan_Id(UUID id, Long loanId);

    boolean existsByApplicant_LoanIdAndApplicant_IdAndDocType(
            Long loanId,
            Long applicantId,
            DocumentType docType
    );
}