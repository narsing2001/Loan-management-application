package com.example.loantypecategories.service;

import com.example.loantypecategories.constant.ApplicationStatus;
import com.example.loantypecategories.constant.Gender;
import com.example.loantypecategories.dto.LoanApplicationRequestDTO;
import com.example.loantypecategories.dto.LoanApplicationResponseDTO;
import com.example.loantypecategories.entity.LoanApplication;

import com.example.loantypecategories.exception.RequestAmountZeroException;
import com.example.loantypecategories.factory.LoanStrategyFactory;
import com.example.loantypecategories.strategy.LoanInterestStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


@Service
public class LoanApplicationService {

    private static final BigDecimal FEMALE_DISCOUNT_PERCENT = new BigDecimal("0.50"); // 0.5% extra discount


    public LoanApplicationResponseDTO applyForLoan(LoanApplicationRequestDTO request)  {

        // Step 1 — Basic validation
        validateRequest(request);

        // Step 2 — Factory resolves strategy based on loan type chosen by applicant
        LoanInterestStrategy strategy = LoanStrategyFactory.getStrategy(request.getLoanType());

        // Step 3 — Pre-condition check (institution approval / admission letter)
        strategy.validatePreConditions(request);

        // Step 4 — Calculate interest rate (strategy handles gender-based rates)
        BigDecimal interestRate = strategy.calculateInterestRate(request);

        // Step 5 — Apply additional female discount on top of already-discounted rate
        boolean femaleDiscount = (request.getGender() == Gender.FEMALE);
        if (femaleDiscount) {
            interestRate = interestRate.subtract(FEMALE_DISCOUNT_PERCENT)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Step 6 — Determine approved amount (capped at strategy maximum)
        BigDecimal maxAllowed   = strategy.getMaxLoanAmount(request);
        BigDecimal approvedAmt  = request.getRequestedAmount().min(maxAllowed)
                .setScale(2, RoundingMode.HALF_UP);
        int tenureYears = strategy.getMaxTenureYears();

        // Step 7 — Build and populate entity
        LoanApplication application = buildEntity(request, interestRate, approvedAmt, tenureYears);

        // Step 8 — Coverage breakdown
        strategy.calculateCoverage(request, application);

        // Step 9 — Calculate monthly EMI
        BigDecimal monthlyEmi = calculateEmi(approvedAmt, interestRate, tenureYears);
        application.setMonthlyEmi(monthlyEmi);
        application.setStatus(ApplicationStatus.APPROVED);

        return buildResponse(application, femaleDiscount);
    }



    private void validateRequest(LoanApplicationRequestDTO request) {
        if (request.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RequestAmountZeroException("Requested amount must be greater than zero");
        }
    }

    private LoanApplication buildEntity(LoanApplicationRequestDTO request, BigDecimal interestRate, BigDecimal approvedAmt, int tenureYears) {
        LoanApplication app = new LoanApplication();
        app.setApplicantName(request.getApplicantName());
        app.setApplicantEmail(request.getApplicantEmail());
        app.setGender(request.getGender());
        app.setLoanType(request.getLoanType());
        app.setCourseType(request.getCourseType());
        app.setRequestedAmount(request.getRequestedAmount());
        app.setInterestRate(interestRate);
        app.setApprovedAmount(approvedAmt);
        app.setTenureYears(tenureYears);
        app.setAdmissionLetterVerified(request.isHasAdmissionLetter());
        app.setInstitutionApproved(request.isInstitutionApproved());
        return app;
    }

    /**
     * Standard EMI formula:
     *   EMI = P * r * (1+r)^n / ((1+r)^n - 1)
     *   where r = monthly interest rate, n = total months
     */
    private BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRatePercent, int years) {
        BigDecimal monthlyRate = annualRatePercent
                .divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);

        int months = years * 12;

        // (1 + r)^n
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal power    = onePlusR.pow(months, new MathContext(10, RoundingMode.HALF_UP));

        // P * r * (1+r)^n
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(power);

        // (1+r)^n - 1
        BigDecimal denominator = power.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private LoanApplicationResponseDTO buildResponse(LoanApplication app, boolean femaleDiscount) {
        LoanApplicationResponseDTO response = new LoanApplicationResponseDTO();
        response.setApplicationId(app.getId());
        response.setApplicantName(app.getApplicantName());
        response.setGender(app.getGender());
        response.setLoanType(app.getLoanType());
        response.setCourseType(app.getCourseType());
        response.setRequestedAmount(app.getRequestedAmount());
        response.setApprovedAmount(app.getApprovedAmount());
        response.setInterestRate(app.getInterestRate());
        response.setFemaleDiscountApplied(femaleDiscount);
        response.setTenureYears(app.getTenureYears());
        response.setMonthlyEmi(app.getMonthlyEmi());
        response.setStatus(app.getStatus());
        response.setCreatedAt(app.getCreatedAt());

        response.setMessage(femaleDiscount
                ? "Congratulations! Your application is approved with a special female candidate discount."
                : "Congratulations! Your loan application has been approved.");

        LoanApplicationResponseDTO.CoverageBreakdown breakdown = new LoanApplicationResponseDTO.CoverageBreakdown();
        breakdown.setTuitionFee(app.getTuitionFeeCoverage());
        breakdown.setAccommodation(app.getAccommodationCoverage());
        breakdown.setTravelExpense(app.getTravelExpenseCoverage());
        breakdown.setLaptopExpense(app.getLaptopExpenseCoverage());
        breakdown.setExamFee(app.getExamFeeCoverage());
        breakdown.setOtherExpense(app.getOtherExpenseCoverage());

        BigDecimal total = sum(
                app.getTuitionFeeCoverage(),
                app.getAccommodationCoverage(),
                app.getTravelExpenseCoverage(),
                app.getLaptopExpenseCoverage(),
                app.getExamFeeCoverage(),
                app.getOtherExpenseCoverage()
        );
        breakdown.setTotalCoverage(total);
        response.setCoverageBreakdown(breakdown);

        return response;
    }

    private BigDecimal sum(BigDecimal... values) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal v : values) {
            if (v != null) total = total.add(v);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
