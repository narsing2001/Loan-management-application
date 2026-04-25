package com.educationloan.document.repository;
import com.educationloan.document.entity.ApplicantEntity;
import com.educationloan.document.entity.DocumentEntity;
import com.educationloan.document.entity.LoanEntity;
import com.educationloan.document.enumConst.ApplicantType;
import com.educationloan.document.enumConst.DocumentStatus;
import com.educationloan.document.enumConst.DocumentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.Instant;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DocumentRepositoryTest {
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Test
    void saveDocument_shouldPersistSuccessfully() {

        LoanEntity loan = new LoanEntity();
        loan.setId(1L);
        loan = loanRepository.save(loan);

        ApplicantEntity applicant = new ApplicantEntity();
        applicant.setName("Parveen");
        applicant.setEmail("test@gmail.com");
        applicant.setMobile("9999999999");
        applicant.setApplicantType(ApplicantType.STUDENT);
        applicant.setLoan(loan);

        applicant = applicantRepository.save(applicant);

        DocumentEntity doc = DocumentEntity.builder()
                .applicant(applicant)
                .docType(DocumentType.AADHAAR)
                .status(DocumentStatus.UPLOADED)
                .filename("aadhar.pdf")
                .s3Key("s3/key/123")
                .contentType("application/pdf")
                .fileSize(1024L)
                .uploadedAt(Instant.now())
                .build();

        DocumentEntity saved = documentRepository.save(doc);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getApplicant().getId()).isNotNull();
        assertThat(saved.getDocType()).isEqualTo(DocumentType.AADHAAR);
    }

    @Test
    void findByIdAndApplicantLoanId_shouldReturnDocument() {

        LoanEntity loan = new LoanEntity();
        loan.setId(1L);
        loan = loanRepository.save(loan);

        ApplicantEntity applicant = new ApplicantEntity();
        applicant.setName("Test User");
        applicant.setEmail("test2@gmail.com");
        applicant.setApplicantType(ApplicantType.STUDENT);
        applicant.setLoan(loan);

        applicant = applicantRepository.save(applicant);

        DocumentEntity doc = DocumentEntity.builder()
                .applicant(applicant)
                .docType(DocumentType.PAN)
                .status(DocumentStatus.UPLOADED)
                .filename("pan.pdf")
                .s3Key("s3/pan/123")
                .contentType("application/pdf")
                .fileSize(2048L)
                .uploadedAt(Instant.now())
                .build();

        doc = documentRepository.save(doc);

        Optional<DocumentEntity> result =
                documentRepository.findByIdAndApplicant_Loan_Id(doc.getId(), loan.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(doc.getId());
        assertThat(result.get().getApplicant().getLoan().getId()).isEqualTo(loan.getId());
    }
}