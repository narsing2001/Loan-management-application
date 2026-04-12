package com.example.LoanTypeStrategyInterest.strategy;

import com.example.LoanTypeStrategyInterest.dto.dto1.UserApplicationRequestDTO;
import com.example.LoanTypeStrategyInterest.entity.entity1.UserApplicationDetail;
import com.example.LoanTypeStrategyInterest.enums.enum1.Gender;
import com.example.LoanTypeStrategyInterest.exception.AdmissionLetterVerificationException;
import com.example.LoanTypeStrategyInterest.exception.InstituteNotApprovedException;
import com.example.LoanTypeStrategyInterest.exception.InvalidTenureYearException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;



public class DomesticLoanStrategy implements InterestStrategy {


    private static final BigDecimal MIN_RATE = new BigDecimal("9.00");
    private static final BigDecimal MAX_RATE = new BigDecimal("12.00");
    private static final BigDecimal FEMALE_DISCOUNT = new BigDecimal("1.00");

    private static final BigDecimal DOMESTIC_MALE_INTEREST_RATE=new BigDecimal("12.00");
    private static final BigDecimal DOMESTIC_FEMALE_INTEREST_RATE=new BigDecimal("9.00");
    private static final BigDecimal OTHER_INTEREST_RATE=new BigDecimal("11.00");

    private static final int DOMESTIC_MAX_TENURE_YEARS=15;


    private static final BigDecimal DOMESTIC_TUITION_EXPENSE       = new BigDecimal("0.70");
    private static final BigDecimal DOMESTIC_ACCOMMODATION_EXPENSE = new BigDecimal("0.10");
    private static final BigDecimal DOMESTIC_TRAVEL_EXPENSE        = new BigDecimal("0.05");
    private static final BigDecimal DOMESTIC_LAPTOP_EXPENSE        = new BigDecimal("0.05");
    private static final BigDecimal DOMESTIC_EXAM_FEE_EXPENSE      = new BigDecimal("0.05");
    private static final BigDecimal DOMESTIC_OTHER_EXPENSE         = new BigDecimal("0.05");


    @Override
    public BigDecimal findInterestRate(UserApplicationRequestDTO requestDTO) {
//        return switch (requestDTO.getGender()) {
//            case MALE   -> DOMESTIC_MALE_INTEREST_RATE ;
//            case FEMALE -> DOMESTIC_FEMALE_INTEREST_RATE;
//            case OTHER  -> OTHER_INTEREST_RATE;
//        };
        double randomRate= ThreadLocalRandom.current().nextDouble(MIN_RATE.doubleValue(),MAX_RATE.doubleValue()+0.01);
        BigDecimal rate=BigDecimal.valueOf(randomRate).setScale(2,RoundingMode.HALF_UP);
        if (requestDTO.getGender() == Gender.FEMALE) {
            rate = rate.subtract(FEMALE_DISCOUNT);
            if (rate.compareTo(MIN_RATE) < 0) {
                rate = MIN_RATE; // clamp to minimum
            }
        }
        return rate;
    }

    @Override
    public BigDecimal getMaxLoanAmount(UserApplicationRequestDTO requestDTO) {
        return switch (requestDTO.getCourseType()) {
            case UNDERGRADUATE         -> new BigDecimal("1000000");
            case POSTGRADUATE          -> new BigDecimal("2000000");
            case DOCTORAL_PHD          -> new BigDecimal("3000000");
            case VOCATIONAL_SKILL      -> new BigDecimal("500000");
            case PROFESSIONAL          -> new BigDecimal("5000000");
            case MBA                   -> new BigDecimal("1200000");
        };
    }

    @Override
    public int getMaxTenureYears(int TenureYears) {
        if(TenureYears>DOMESTIC_MAX_TENURE_YEARS){
            throw new InvalidTenureYearException("tenure year must be less than or equalTo: "+DOMESTIC_MAX_TENURE_YEARS+" for the Domestic education Loan");
        }
        return TenureYears;
    }

    @Override
    public void validatePreConditions(UserApplicationRequestDTO requestDTO) {
        if (!requestDTO.isInstitutionApproved()) {
            throw new InstituteNotApprovedException("Institution must be UGC/AICTE approved");
        }
        if(!requestDTO.isAdmissionLetterVerified()){
            throw new AdmissionLetterVerificationException("your admission letter for domestic education loan verification is pending");
        }
    }

    @Override
    public void totalCoverage(UserApplicationRequestDTO requestDTO, UserApplicationDetail userApplicationDetail) {
     BigDecimal baseAmount=userApplicationDetail.getApprovedAmount();
     userApplicationDetail.setTuitionFeeCoverage(Ratio(requestDTO.getTuitionFeeCoverage(),baseAmount,DOMESTIC_TUITION_EXPENSE));
        userApplicationDetail.setAccommodationCoverage(Ratio(requestDTO.getAccommodationCoverage(),baseAmount,DOMESTIC_ACCOMMODATION_EXPENSE));
        userApplicationDetail.setTravelExpenseCoverage(Ratio(requestDTO.getTravelExpenseCoverage(),baseAmount,DOMESTIC_TRAVEL_EXPENSE));
        userApplicationDetail.setLaptopExpenseCoverage(Ratio(requestDTO.getLaptopExpenseCoverage(),baseAmount,DOMESTIC_LAPTOP_EXPENSE));
        userApplicationDetail.setExamFeeCoverage(Ratio(requestDTO.getExamFeeCoverage(),baseAmount,DOMESTIC_EXAM_FEE_EXPENSE));
        userApplicationDetail.setOtherExpenseCoverage(Ratio(requestDTO.getOtherExpenseCoverage(),baseAmount,DOMESTIC_OTHER_EXPENSE));
    }

    private BigDecimal Ratio(BigDecimal expenses,BigDecimal baseAmt,BigDecimal topicExpenseRate){
        if(expenses!=null && expenses.compareTo(BigDecimal.ZERO)>0){
            return expenses.setScale(2, RoundingMode.HALF_UP);
        }
        return baseAmt.multiply(topicExpenseRate).setScale(2,RoundingMode.HALF_UP);
    }


}
