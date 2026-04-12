package com.example.loantypecategories.service;


import com.example.loantypecategories.constant.ApplicationStatus;
import com.example.loantypecategories.constant.CourseType;
import com.example.loantypecategories.constant.Gender;
import com.example.loantypecategories.constant.LoanType;
import com.example.loantypecategories.dto.LoanApplicationRequestDTO;
import com.example.loantypecategories.dto.LoanApplicationResponseDTO;
import com.example.loantypecategories.entity.LoanApplication;
import com.example.loantypecategories.exception.DuplicateApplicationException;
import com.example.loantypecategories.exception.LoanApplicationNotFoundException;

import com.example.loantypecategories.factory.LoanStrategyFactory;
import com.example.loantypecategories.repository.LoanApplicationRepository;
import com.example.loantypecategories.strategy.LoanInterestStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class LoanAppService {

    private static final BigDecimal FEMALE_EXTRA_DISCOUNT = new BigDecimal("0.50");

    private final LoanApplicationRepository repository;

    public LoanAppService(LoanApplicationRepository repository) {

        this.repository = repository;
    }

    public LoanApplicationResponseDTO applyForLoan(LoanApplicationRequestDTO request)  {
        if (repository.existsByApplicantEmail(request.getApplicantEmail())) {
            throw new DuplicateApplicationException(request.getApplicantEmail());
        }
        LoanInterestStrategy strategy = LoanStrategyFactory.getStrategy(request.getLoanType());
        strategy.validatePreConditions(request);


        BigDecimal interestRate = strategy.calculateInterestRate(request);

        // Additional 0.5% discount for female candidates on top of the base discounted rate
        boolean femaleDiscount = (request.getGender() == Gender.FEMALE);
        if (femaleDiscount) {
            interestRate = interestRate.subtract(FEMALE_EXTRA_DISCOUNT).setScale(2, RoundingMode.HALF_UP);
        }

        // Approved amount = min(requested, strategy max cap)
        BigDecimal maxAllowed  = strategy.getMaxLoanAmount(request);
        BigDecimal approvedAmt = request.getRequestedAmount().min(maxAllowed)
                .setScale(2, RoundingMode.HALF_UP);
        int tenureYears = strategy.getMaxTenureYears();

        // Build and persist entity
        LoanApplication application = buildEntity(request, interestRate, approvedAmt, tenureYears);
        strategy.calculateCoverage(request, application);
        application.setMonthlyEmi(calculateEmi(approvedAmt, interestRate, tenureYears));
        application.setStatus(ApplicationStatus.APPROVED);

        return buildResponse(repository.save(application), femaleDiscount);
    }

    // ----------------------------------------------------------------
    // 2. Get by ID  (GET /api/loans/{id})
    // ----------------------------------------------------------------

    @Transactional(readOnly = true)
    public LoanApplicationResponseDTO getById(Long id) {
        LoanApplication app = repository.findById(id)
                .orElseThrow(() -> new LoanApplicationNotFoundException(id));
        return buildResponse(app, app.getGender() == Gender.FEMALE);
    }

    // ----------------------------------------------------------------
    // 3. Get by Email  (GET /api/loans/email/{email})
    // ----------------------------------------------------------------

    @Transactional(readOnly = true)
    public LoanApplicationResponseDTO getByEmail(String email) {
        LoanApplication app = repository.findByApplicantEmail(email)
                .orElseThrow(() -> new LoanApplicationNotFoundException(0L));
        return buildResponse(app, app.getGender() == Gender.FEMALE);
    }

    // ----------------------------------------------------------------
    // 4. Get All  (GET /api/loans)
    // ----------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<LoanApplicationResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(app -> buildResponse(app, app.getGender() == Gender.FEMALE))
                .toList();
    }

    // ----------------------------------------------------------------
    // 5. Filter by Status  (GET /api/loans?status=APPROVED)
    // ----------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<LoanApplicationResponseDTO> getByStatus(ApplicationStatus status) {
        return repository.findByStatus(status)
                .stream()
                .map(app -> buildResponse(app, app.getGender() == Gender.FEMALE))
                .toList();
    }

    // ----------------------------------------------------------------
    // 6. Filter by Loan Type  (GET /api/loans?loanType=DOMESTIC)
    // ----------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<LoanApplicationResponseDTO> getByLoanType(LoanType loanType) {
        return repository.findByLoanType(loanType)
                .stream()
                .map(app -> buildResponse(app, app.getGender() == Gender.FEMALE))
                .toList();
    }

    // ----------------------------------------------------------------
    // 7. Filter by Course Type  (GET /api/loans?courseType=UNDERGRADUATE)
    // ----------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<LoanApplicationResponseDTO> getByCourseType(CourseType courseType) {
        return repository.findByCourseType(courseType)
                .stream()
                .map(app -> buildResponse(app, app.getGender() == Gender.FEMALE))
                .toList();
    }

    // ----------------------------------------------------------------
    // 8. Update Status  (PATCH /api/loans/{id}/status)
    // ----------------------------------------------------------------

    public LoanApplicationResponseDTO updateStatus(Long id, ApplicationStatus newStatus) {
        LoanApplication app = repository.findById(id)
                .orElseThrow(() -> new LoanApplicationNotFoundException(id));
        app.setStatus(newStatus);
        return buildResponse(repository.save(app), app.getGender() == Gender.FEMALE);
    }

    // ----------------------------------------------------------------
    // 9. Cancel Application  (DELETE /api/loans/{id})
    // ----------------------------------------------------------------

    public void cancelApplication(Long id) {
        LoanApplication app = repository.findById(id)
                .orElseThrow(() -> new LoanApplicationNotFoundException(id));
        app.setStatus(ApplicationStatus.REJECTED);
        repository.save(app);
    }

    // ----------------------------------------------------------------
    // 10. Summary Stats  (GET /api/loans/stats)
    // ----------------------------------------------------------------

    @Transactional(readOnly = true)
    public LoanSummaryStats getSummaryStats() {
        return new LoanSummaryStats(
                repository.countByStatus(ApplicationStatus.APPROVED),
                repository.countByStatus(ApplicationStatus.PENDING),
                repository.countByStatus(ApplicationStatus.REJECTED),
                repository.countByStatus(ApplicationStatus.UNDER_REVIEW),
                repository.totalApprovedLoanAmount(),
                repository.averageInterestRateByLoanType(LoanType.DOMESTIC),
                repository.averageInterestRateByLoanType(LoanType.INTERNATIONAL),
                repository.findApprovedFemaleApplicants().size()
        );
    }

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

    private LoanApplication buildEntity(LoanApplicationRequestDTO req,
                                        BigDecimal interestRate,
                                        BigDecimal approvedAmt,
                                        int tenureYears) {
        LoanApplication app = new LoanApplication();
        app.setApplicantName(req.getApplicantName());
        app.setApplicantEmail(req.getApplicantEmail());
        app.setGender(req.getGender());
        app.setLoanType(req.getLoanType());
        app.setCourseType(req.getCourseType());
        app.setRequestedAmount(req.getRequestedAmount());
        app.setInterestRate(interestRate);
        app.setApprovedAmount(approvedAmt);
        app.setTenureYears(tenureYears);
        app.setAdmissionLetterVerified(req.isHasAdmissionLetter());
        app.setInstitutionApproved(req.isInstitutionApproved());
        return app;
    }

    /**
     * Standard compound EMI formula:
     *   EMI = P × r × (1+r)^n  /  ((1+r)^n − 1)
     *   r = monthly rate (annualRate% / 1200),  n = total months
     */
    private BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRatePct, int years) {
        BigDecimal r = annualRatePct.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        int n = years * 12;
        BigDecimal power = BigDecimal.ONE.add(r).pow(n, new MathContext(10));
        return principal.multiply(r).multiply(power)
                .divide(power.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
    }

    private LoanApplicationResponseDTO buildResponse(LoanApplication app, boolean femaleDiscount) {
        LoanApplicationResponseDTO res = new LoanApplicationResponseDTO();
        res.setApplicationId(app.getId());
        res.setApplicantName(app.getApplicantName());
        res.setGender(app.getGender());
        res.setLoanType(app.getLoanType());
        res.setCourseType(app.getCourseType());
        res.setRequestedAmount(app.getRequestedAmount());
        res.setApprovedAmount(app.getApprovedAmount());
        res.setInterestRate(app.getInterestRate());
        res.setFemaleDiscountApplied(femaleDiscount);
        res.setTenureYears(app.getTenureYears());
        res.setMonthlyEmi(app.getMonthlyEmi());
        res.setStatus(app.getStatus());
        res.setCreatedAt(app.getCreatedAt());
        res.setMessage(femaleDiscount
                ? "Approved with special female candidate discount applied."
                : "Loan application approved successfully.");

        LoanApplicationResponseDTO.CoverageBreakdown cb = new LoanApplicationResponseDTO.CoverageBreakdown();
        cb.setTuitionFee(app.getTuitionFeeCoverage());
        cb.setAccommodation(app.getAccommodationCoverage());
        cb.setTravelExpense(app.getTravelExpenseCoverage());
        cb.setLaptopExpense(app.getLaptopExpenseCoverage());
        cb.setExamFee(app.getExamFeeCoverage());
        cb.setOtherExpense(app.getOtherExpenseCoverage());
        cb.setTotalCoverage(sum(
                app.getTuitionFeeCoverage(), app.getAccommodationCoverage(),
                app.getTravelExpenseCoverage(), app.getLaptopExpenseCoverage(),
                app.getExamFeeCoverage(), app.getOtherExpenseCoverage()));
        res.setCoverageBreakdown(cb);
        return res;
    }

    private BigDecimal sum(BigDecimal... values) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal v : values) { if (v != null) total = total.add(v); }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    // ---- Stats record ----
    public record LoanSummaryStats(
            long totalApproved,
            long totalPending,
            long totalRejected,
            long l, BigDecimal totalApprovedAmount,
            BigDecimal avgDomesticInterestRate,
            BigDecimal avgInternationalInterestRate,
            long totalFemaleApproved
    ) {}
}