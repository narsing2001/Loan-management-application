package com.example.loantypecategories.service.serv1;


import com.example.loantypecategories.constant.ApplicationStatus;
import com.example.loantypecategories.constant.CourseType;
import com.example.loantypecategories.constant.Gender;
import com.example.loantypecategories.constant.LoanType;
import com.example.loantypecategories.dto.dto1.LoanApplicationRequestDTO1;
import com.example.loantypecategories.dto.dto1.LoanApplicationResponseDTO1;
import com.example.loantypecategories.entity.LoanApplication;
import com.example.loantypecategories.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanAppService1 {

    private final LoanApplicationRepository repository;

    // --- Apply for loan ---
    public LoanApplicationResponseDTO1 applyForLoan(LoanApplicationRequestDTO1 request) {
        LoanApplication entity = new LoanApplication();
        entity.setApplicantName(request.getApplicantName());
        entity.setApplicantEmail(request.getApplicantEmail());
        entity.setGender(request.getGender());
        //entity.setBankType(request.getBankType());
        entity.setLoanType(request.getLoanType());
        entity.setCourseType(request.getCourseType());
        entity.setRequestedAmount(request.getRequestedAmount());
        entity.setStatus(ApplicationStatus.PENDING);
        entity.setCreatedAt(LocalDateTime.now());

        LoanApplication saved = repository.save(entity);
        return toResponseDTO(saved);
    }

    // --- Get all ---
    public List<LoanApplicationResponseDTO1> getAll() {
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public LoanApplicationResponseDTO1 getById(Long id) {
        return repository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    public LoanApplicationResponseDTO1 getByEmail(String email) {
        return repository.findByApplicantEmail(email)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    // --- Filters ---
    public List<LoanApplicationResponseDTO1> getByStatus(ApplicationStatus status) {
        return repository.findByStatus(status).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<LoanApplicationResponseDTO1> getByLoanType(LoanType loanType) {
        return repository.findByLoanType(loanType).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<LoanApplicationResponseDTO1> getByCourseType(CourseType courseType) {
        return repository.findByCourseType(courseType).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<LoanApplicationResponseDTO1> getByGender(Gender gender) {
        return repository.findByGender(gender).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // --- Custom queries ---
    public List<LoanApplicationResponseDTO1> getPartiallyApprovedLoans() {
        return repository.findPartiallyApprovedLoans().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<LoanApplicationResponseDTO1> getApprovedFemaleApplicants() {
        return repository.findApprovedFemaleApplicants().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<LoanApplicationResponseDTO1> getByApprovedAmountRange(BigDecimal min, BigDecimal max) {
        return repository.findByApprovedAmountBetween(min, max).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<LoanApplicationResponseDTO1> getByCreatedAtRange(LocalDateTime from, LocalDateTime to) {
        return repository.findByCreatedAtBetween(from, to).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // --- Update status ---
    public LoanApplicationResponseDTO1 updateStatus(Long id, ApplicationStatus status) {
        LoanApplication entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        entity.setStatus(status);
        LoanApplication updated = repository.save(entity);
        return toResponseDTO(updated);
    }

    public void cancelApplication(Long id) {
        repository.deleteById(id);
    }

    // --- Stats ---
    public LoanSummaryStats getSummaryStats() {
        long totalApplications = repository.count();
        long approvedCount = repository.countByStatus(ApplicationStatus.APPROVED);
        BigDecimal totalApprovedAmount = repository.totalApprovedLoanAmount();
        BigDecimal avgDomesticRate = repository.averageInterestRateByLoanType(LoanType.DOMESTIC);
        BigDecimal avgInternationalRate = repository.averageInterestRateByLoanType(LoanType.INTERNATIONAL);

        return new LoanSummaryStats(totalApplications, approvedCount,
                totalApprovedAmount, avgDomesticRate, avgInternationalRate);
    }

    // --- DTO Mapper ---
    private LoanApplicationResponseDTO1 toResponseDTO(LoanApplication entity) {
        LoanApplicationResponseDTO1 dto = new LoanApplicationResponseDTO1();
        dto.setApplicationId(entity.getId());
        dto.setApplicantName(entity.getApplicantName());
        dto.setApplicantEmail(entity.getApplicantEmail());
        dto.setGender(entity.getGender());
     //   dto.setBankType(entity.getBankType());
        dto.setLoanType(entity.getLoanType());
        dto.setCourseType(entity.getCourseType());
        dto.setRequestedAmount(entity.getRequestedAmount());
        dto.setApprovedAmount(entity.getApprovedAmount());
      //  dto.setBaseInterestRate(entity.getBaseInterestRate());
        //dto.setFinalInterestRate(entity.getFinalInterestRate());
       // dto.setFemaleDiscountApplied(entity.getFemaleDiscountApplied());
       // dto.setGovtBankDiscountApplied(entity.getGovtBankDiscountApplied());
       // dto.setTenureYears(entity.getTenureYears());
       // dto.setMonthlyEmi(entity.getMonthlyEmi());
      //  dto.setProcessingFee(entity.getProcessingFee());
      //  dto.setStatus(entity.getStatus());
       // dto.setRemarks(entity.getRemarks());
       // dto.setCreatedAt(entity.getCreatedAt());
       // dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public record LoanSummaryStats(
            long totalApplications,
            long approvedCount,
            BigDecimal totalApprovedAmount,
            BigDecimal avgDomesticRate,
            BigDecimal avgInternationalRate
    ) {}
}