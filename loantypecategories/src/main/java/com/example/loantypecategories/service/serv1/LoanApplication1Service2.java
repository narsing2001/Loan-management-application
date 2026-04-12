package com.example.loantypecategories.service.serv1;

import com.example.loantypecategories.constant.ApplicationStatus;
import com.example.loantypecategories.constant.CourseType;
import com.example.loantypecategories.constant.Gender;
import com.example.loantypecategories.constant.LoanType;
import com.example.loantypecategories.entity.entit1.LoanApplication1;

import com.example.loantypecategories.repository.LoanApplicationRepository;
import com.example.loantypecategories.repository.repo1.LoanApplication1Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanApplication1Service2 {

    private final LoanApplication1Repository repository;

    // --- Apply for loan ---
    public LoanApplication1 apply(LoanApplication1 entity) {
        return repository.save(entity);
    }

    // --- Get all ---
    @Transactional(readOnly = true)
    public List<LoanApplication1> getAll() {
        return repository.findAll();
    }

    // --- Get by ID ---
    @Transactional(readOnly = true)
    public LoanApplication1 getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan application not found with id: " + id));
    }

    // --- Get by Email ---
    @Transactional(readOnly = true)
    public LoanApplication1 getByEmail(String email) {
        return repository.findByApplicantEmail(email)
                .orElseThrow(() -> new RuntimeException("Loan application not found with email: " + email));
    }

    // --- Filter by status ---
    @Transactional(readOnly = true)
    public List<LoanApplication1> getByStatus(ApplicationStatus status) {
        return repository.findByStatus(status);
    }

    // --- Filter by loan type ---
    @Transactional(readOnly = true)
    public List<LoanApplication1> getByLoanType(LoanType loanType) {
        return repository.findByLoanType(loanType);
    }

    // --- Filter by course type ---
    @Transactional(readOnly = true)
    public List<LoanApplication1> getByCourseType(CourseType courseType) {
        return repository.findByCourseType(courseType);
    }

    // --- Filter by gender ---
    @Transactional(readOnly = true)
    public List<LoanApplication1> getByGender(Gender gender) {
        return repository.findByGender(gender);
    }

    // --- Update status ---
    public LoanApplication1 updateStatus(Long id, ApplicationStatus status) {
        LoanApplication1 app = getById(id);
        app.setStatus(status);
        return repository.save(app);
    }

    // --- Delete application ---
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // --- Count applications by status ---
    @Transactional(readOnly = true)
    public long countByStatus(ApplicationStatus status) {
        return repository.countByStatus(status);
    }
}
