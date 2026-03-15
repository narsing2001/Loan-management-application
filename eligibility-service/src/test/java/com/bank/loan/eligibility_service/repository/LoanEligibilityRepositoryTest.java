package com.bank.loan.eligibility_service.repository;

import com.bank.loan.eligibility_service.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class LoanEligibilityRepositoryTest {

    @Autowired
    private LoanEligibilityRepository repository;

    private LoanEligibility loanEligibility;

    @BeforeEach
    void setup(){
        StudentDetails student = StudentDetails.builder()
                .studentName("Komal")
                .age(22)
                .build();

        EducationDetails education = EducationDetails.builder()
                .courseName("Engineering")
                .collegeName("Raisoni")
                .build();

        FinancialDetails financial = FinancialDetails.builder()
                .annualIncome(600000.0)
                .existingEMI(10000.0)
                .build();

        CoApplicantDetails coApplicant = CoApplicantDetails.builder()
                .coApplicantName("Father")
                .coApplicantIncome(500000.0)
                .build();

        DecisionDetails decision = DecisionDetails.builder()
                .eligible(true)
                .build();

        loanEligibility = LoanEligibility.builder()
                .studentDetails(student)
                .educationDetails(education)
                .financialDetails(financial)
                .coApplicantDetails(coApplicant)
                .decisionDetails(decision)
                .createdAt(LocalDateTime.now())
                .createdBy("system")
                .build();

    }

    @Test
    @DisplayName("Save LoanEligibility")
    void saveLoanEligibility() {

        StudentDetails student = StudentDetails.builder()
                .studentName("komal")
                .age(25)
                .build();


        LoanEligibility loan = LoanEligibility.builder()
                .studentDetails(student)
                .build();

       LoanEligibility saved= repository.save(loan);
       assertNotNull(saved);
       assertNotNull(saved.getId());
    }

    @Test
    void shouldSaveLoanEligibility() {
        LoanEligibility saved = repository.save(loanEligibility);

        assertNotNull(saved);
        assertNotNull(saved.getId());
    }

    @Test
    void shouldFindLoanEligibilityById() {
        LoanEligibility saved = repository.save(loanEligibility);

        Optional<LoanEligibility> result = repository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("Komal",
                result.get().getStudentDetails().getStudentName());
    }
    @Test
    void shouldUpdateLoanEligibility() {

        LoanEligibility saved = repository.save(loanEligibility);

        saved.getStudentDetails().setStudentName("Priya");

        repository.save(saved);

        LoanEligibility updated = repository.findById(saved.getId()).get();

        assertEquals("Priya",
                updated.getStudentDetails().getStudentName());
    }
    @Test
    void shouldDeleteLoanEligibility() {

        LoanEligibility saved = repository.save(loanEligibility);

        repository.deleteById(saved.getId());

        Optional<LoanEligibility> result = repository.findById(saved.getId());

        assertFalse(result.isPresent());
    }
    @Test
    void shouldCheckIfLoanEligibilityExists() {

        LoanEligibility saved = repository.save(loanEligibility);

        boolean exists = repository.existsById(saved.getId());

        assertTrue(exists);
    }

    @Test
    void shouldDeleteAllLoanEligibilities() {

        repository.save(loanEligibility);

        repository.deleteAll();

        long count = repository.count();

        assertEquals(0, count);
    }

}
