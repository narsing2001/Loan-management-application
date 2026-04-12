package com.example.loantypecategories.strategy;

import com.example.loantypecategories.dto.LoanApplicationRequestDTO;
import com.example.loantypecategories.entity.LoanApplication;
import com.example.loantypecategories.exception.InstituteNotApprovedException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

public class DomesticLoanStrategy implements LoanInterestStrategy {

    private static final BigDecimal DOMESTIC_LOAN_MALE_INTEREST_RATE   = new BigDecimal("12.00");
    private static final BigDecimal DOMESTIC_LOAN_FEMALE_INTEREST_RATE = new BigDecimal("9.00");
    private static final BigDecimal OTHER_INTEREST_RATE  = new BigDecimal("11.00");

    private static final BigDecimal MIN_RATE = new BigDecimal("9.00");
    private static final BigDecimal MAX_RATE = new BigDecimal("12.00");
    private static final BigDecimal FEMALE_DISCOUNT = new BigDecimal("1.00"); // 1% discount


    private static final int MAX_TENURE_YEARS = 15;


    private static final BigDecimal DOMESTIC_TUITION_EXPENSE       = new BigDecimal("0.70");
    private static final BigDecimal DOMESTIC_ACCOMMODATION_EXPENSE = new BigDecimal("0.10");
    private static final BigDecimal DOMESTIC_TRAVEL_EXPENSE        = new BigDecimal("0.05");
    private static final BigDecimal DOMESTIC_LAPTOP_EXPENSE        = new BigDecimal("0.05");
    private static final BigDecimal DOMESTIC_EXAM_FEE_EXPENSE      = new BigDecimal("0.05");
    private static final BigDecimal DOMESTIC_OTHER_EXPENSE         = new BigDecimal("0.05");

    @Override
    public BigDecimal calculateInterestRate(LoanApplicationRequestDTO request) {
//        return switch (request.getGender()) {
//            case MALE   -> DOMESTIC_LOAN_MALE_INTEREST_RATE ;
//            case FEMALE -> DOMESTIC_LOAN_FEMALE_INTEREST_RATE;
//            case OTHER  -> OTHER_INTEREST_RATE;
//        };
        // Generate a random rate between 9 and 12
        double randomRate = ThreadLocalRandom.current().nextDouble(
                MIN_RATE.doubleValue(), MAX_RATE.doubleValue() + 0.01
        );
        BigDecimal rate = BigDecimal.valueOf(randomRate).setScale(2, RoundingMode.HALF_UP);

        // Apply special discount for female applicants
        if (request.getGender() == com.example.loantypecategories.constant.Gender.FEMALE) {
            rate = rate.subtract(FEMALE_DISCOUNT);
            if (rate.compareTo(MIN_RATE) < 0) {
                rate = MIN_RATE; // clamp to minimum
            }
        }
        return rate;
    }

    @Override
    public BigDecimal getMaxLoanAmount(LoanApplicationRequestDTO request) {

        return switch (request.getCourseType()) {
            case UNDERGRADUATE         -> new BigDecimal("1000000");
            case POSTGRADUATE          -> new BigDecimal("2000000");
            case DOCTORAL_PHD          -> new BigDecimal("3000000");
            case VOCATIONAL_SKILL      -> new BigDecimal("500000");
            case PROFESSIONAL          -> new BigDecimal("5000000");
            case MBA                   -> new BigDecimal("1200000");
        };
    }

    @Override
    public int getMaxTenureYears() {
        return MAX_TENURE_YEARS;
    }

    @Override
    public void validatePreConditions(LoanApplicationRequestDTO request) {
        if (!request.isInstitutionApproved()) {
            throw new InstituteNotApprovedException("Loan rejected: Institution is not UGC/AICTE approved. " +
                            "Only approved institutions qualify for domestic education loans."
            );
        }
    }

    @Override
    public void calculateCoverage(LoanApplicationRequestDTO request, LoanApplication application) {
        BigDecimal approved = application.getApprovedAmount();

        application.setTuitionFeeCoverage(orRatio(request.getTuitionFee(), approved, DOMESTIC_TUITION_EXPENSE));
        application.setAccommodationCoverage(orRatio(request.getAccommodationCost(), approved, DOMESTIC_ACCOMMODATION_EXPENSE));
        application.setTravelExpenseCoverage(orRatio(request.getTravelExpense(), approved, DOMESTIC_TRAVEL_EXPENSE ));
        application.setLaptopExpenseCoverage(orRatio(request.getLaptopExpense(), approved, DOMESTIC_LAPTOP_EXPENSE ));
        application.setExamFeeCoverage(orRatio(request.getExamFee(), approved, DOMESTIC_EXAM_FEE_EXPENSE  ));
        application.setOtherExpenseCoverage(orRatio(request.getOtherExpense(), approved, DOMESTIC_OTHER_EXPENSE ));
    }

    private BigDecimal orRatio(BigDecimal explicit, BigDecimal base, BigDecimal ratio) {
        if (explicit != null && explicit.compareTo(BigDecimal.ZERO) > 0) {
            return explicit.setScale(2, RoundingMode.HALF_UP);
        }
        return base.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
    }
}