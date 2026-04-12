package com.example.loantypecategories.strategy;


import com.example.loantypecategories.dto.LoanApplicationRequestDTO;
import com.example.loantypecategories.entity.LoanApplication;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;


public class InternationalLoanStrategy implements LoanInterestStrategy {

    private static final BigDecimal MIN_RATE = new BigDecimal("10.00");
    private static final BigDecimal MAX_RATE = new BigDecimal("15.00");
    private static final BigDecimal FEMALE_DISCOUNT = new BigDecimal("1.00");
    private static final BigDecimal INTERNATIONAL_MALE_INTEREST_RATE   = new BigDecimal("15.00");
    private static final BigDecimal INTERNATIONAL_FEMALE_INTEREST_RATE = new BigDecimal("10.00");
    private static final BigDecimal INTERNATIONAL_OTHER_INTEREST_RATE  = new BigDecimal("13.00");

    private static final BigDecimal INTERNATIONAL_MAX_LOAN_AMOUNT  = new BigDecimal("15000000");
    private static final int        INTERNATIONAL_MAX_TENURE_YEARS = 20;


    private static final BigDecimal INTERNATIONAL_TUITION_EXPENSE_RATIO            = new BigDecimal("0.60");
    private static final BigDecimal INTERNATIONAL_ACCOMMODATION_EXPENSE_RATIO = new BigDecimal("0.15");
    private static final BigDecimal INTERNATIONAL_TRAVEL_EXPENSE_RATIO        = new BigDecimal("0.10");
    private static final BigDecimal INTERNATIONAL_LAPTOP_EXPENSE_RATIO        = new BigDecimal("0.05");
    private static final BigDecimal INTERNATIONAL_EXAM_FEE_EXPENSE_RATIO      = new BigDecimal("0.05");
    private static final BigDecimal INTERNATIONAL_OTHER_EXPENSE_RATIO         = new BigDecimal("0.05");

    @Override
    public BigDecimal calculateInterestRate(LoanApplicationRequestDTO request) {
//        return switch (request.getGender()) {
//            case MALE   -> INTERNATIONAL_MALE_INTEREST_RATE ;
//            case FEMALE -> INTERNATIONAL_FEMALE_INTEREST_RATE ;
//            case OTHER  -> INTERNATIONAL_OTHER_INTEREST_RATE ;
//        };
        // Generate a random rate between 10 and 15
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
            //application.setFemaleDiscountApplied(true); // track discount in entity if field exists
        }
        return rate;


    }

    @Override
    public BigDecimal getMaxLoanAmount(LoanApplicationRequestDTO request) {
        return INTERNATIONAL_MAX_LOAN_AMOUNT;
    }

    @Override
    public int getMaxTenureYears() {
        return INTERNATIONAL_MAX_TENURE_YEARS;
    }

    @Override
    public void validatePreConditions(LoanApplicationRequestDTO request) {
        if (!request.isHasAdmissionLetter()) {
            throw new IllegalStateException(
                    "Loan rejected: Admission letter from the foreign institution is mandatory " +
                            "for international education loans. Please provide a valid admission letter."
            );
        }
    }

    @Override
    public void calculateCoverage(LoanApplicationRequestDTO request, LoanApplication application) {
        BigDecimal approved = application.getApprovedAmount();
        application.setTuitionFeeCoverage(orRatio(request.getTuitionFee(), approved, INTERNATIONAL_TUITION_EXPENSE_RATIO   ));
        application.setAccommodationCoverage(orRatio(request.getAccommodationCost(), approved, INTERNATIONAL_ACCOMMODATION_EXPENSE_RATIO ));
        application.setTravelExpenseCoverage(orRatio(request.getTravelExpense(), approved,  INTERNATIONAL_TRAVEL_EXPENSE_RATIO ));
        application.setLaptopExpenseCoverage(orRatio(request.getLaptopExpense(), approved, INTERNATIONAL_LAPTOP_EXPENSE_RATIO ));
        application.setExamFeeCoverage(orRatio(request.getExamFee(), approved, INTERNATIONAL_EXAM_FEE_EXPENSE_RATIO ));
        application.setOtherExpenseCoverage(orRatio(request.getOtherExpense(), approved, INTERNATIONAL_OTHER_EXPENSE_RATIO ));
    }

    private BigDecimal orRatio(BigDecimal explicit, BigDecimal base, BigDecimal ratio) {
        if (explicit != null && explicit.compareTo(BigDecimal.ZERO) > 0) {
            return explicit.setScale(2, RoundingMode.HALF_UP);
        }
        return base.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
    }
}