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

public class InternationalLoanStrategy implements InterestStrategy{

   private static final BigDecimal MIN_RATE=new BigDecimal("12.00");
   private static final BigDecimal MAX_RATE=new BigDecimal("15.00");
   private static final BigDecimal FEMALE_DISCOUNT=new BigDecimal("1.00");


    private static final BigDecimal INTERNATIONAL_LOAN_MALE_INTEREST   = new BigDecimal("15.00");
    private static final BigDecimal INTERNATIONAL_LOAN_FEMALE_INTEREST = new BigDecimal("10.00");
    private static final BigDecimal INTERNATIONAL_LOAN_OTHER_INTEREST = new BigDecimal("13.00");

    private static final BigDecimal INTERNATIONAL_MAX_LOAN_AMOUNT  = new BigDecimal("15000000");
    private static final int        INTERNATIONAL_MAX_TENURE_YEARS = 20;


    private static final BigDecimal INTERNATIONAL_TUITION_EXPENSE             = new BigDecimal("0.60");
    private static final BigDecimal INTERNATIONAL_ACCOMMODATION_EXPENSE       = new BigDecimal("0.15");
    private static final BigDecimal INTERNATIONAL_TRAVEL_EXPENSE              = new BigDecimal("0.10");
    private static final BigDecimal INTERNATIONAL_LAPTOP_EXPENSE              = new BigDecimal("0.05");
    private static final BigDecimal INTERNATIONAL_EXAM_FEE_EXPENSE            = new BigDecimal("0.05");
    private static final BigDecimal INTERNATIONAL_OTHER_EXPENSE               = new BigDecimal("0.05");



    @Override
    public BigDecimal findInterestRate(UserApplicationRequestDTO requestDTO) {
//        return switch (requestDTO.getGender()){
//            case  MALE  ->INTERNATIONAL_LOAN_MALE_INTEREST;
//            case FEMALE ->INTERNATIONAL_LOAN_FEMALE_INTEREST;
//            case OTHER  ->INTERNATIONAL_LOAN_OTHER_INTEREST;
//        };
        double randomRate= ThreadLocalRandom.current().nextDouble(MIN_RATE.doubleValue(),MAX_RATE.doubleValue()+0.01);
        BigDecimal rate=BigDecimal.valueOf(randomRate).setScale(2,RoundingMode.HALF_UP);
        if (requestDTO.getGender() == Gender.FEMALE) {
            rate = rate.subtract(FEMALE_DISCOUNT);
            if (rate.compareTo(MIN_RATE) < 0) {
                rate = MIN_RATE;
            }

        }
        return rate;
    }

    @Override
    public BigDecimal getMaxLoanAmount(UserApplicationRequestDTO requestDTO) {
        return INTERNATIONAL_MAX_LOAN_AMOUNT;
    }

    @Override
    public int getMaxTenureYears(int TenureYears) {
        if(TenureYears>INTERNATIONAL_MAX_TENURE_YEARS){
            throw new InvalidTenureYearException("tenure year must be less than or equalTo: "+INTERNATIONAL_MAX_TENURE_YEARS+" for the International education Loan");
        }
        return TenureYears;
    }

    @Override
    public void validatePreConditions(UserApplicationRequestDTO requestDTO) {
        if (!requestDTO.isInstitutionApproved()) {
            throw new InstituteNotApprovedException("loan request for current International foreign University under current policy is not valid.");
        }
        if(!requestDTO.isAdmissionLetterVerified()){
            throw new AdmissionLetterVerificationException("your admission letter for international education loan verification is pending");
        }
    }

    @Override
    public void totalCoverage(UserApplicationRequestDTO requestDTO, UserApplicationDetail userApplicationDetail) {
        BigDecimal baseAmount=userApplicationDetail.getApprovedAmount();
        userApplicationDetail.setTuitionFeeCoverage(Ratio(userApplicationDetail.getTuitionFeeCoverage(),baseAmount,INTERNATIONAL_TUITION_EXPENSE));
        userApplicationDetail.setAccommodationCoverage(Ratio(userApplicationDetail.getAccommodationCoverage(),baseAmount,INTERNATIONAL_ACCOMMODATION_EXPENSE));
        userApplicationDetail.setTravelExpenseCoverage(Ratio(userApplicationDetail.getTravelExpenseCoverage(),baseAmount,INTERNATIONAL_TRAVEL_EXPENSE));
        userApplicationDetail.setLaptopExpenseCoverage(Ratio(userApplicationDetail.getLaptopExpenseCoverage(),baseAmount,INTERNATIONAL_LAPTOP_EXPENSE));
        userApplicationDetail.setExamFeeCoverage(Ratio(userApplicationDetail.getExamFeeCoverage(),baseAmount,INTERNATIONAL_EXAM_FEE_EXPENSE));
        userApplicationDetail.setOtherExpenseCoverage(Ratio(userApplicationDetail.getOtherExpenseCoverage(),baseAmount,INTERNATIONAL_OTHER_EXPENSE));
    }

    private BigDecimal Ratio(BigDecimal expenses,BigDecimal baseAmt,BigDecimal topicExpenseRate){
        if(expenses!=null && expenses.compareTo(BigDecimal.ZERO)>0){
            return expenses.setScale(2, RoundingMode.HALF_UP);
        }
        return baseAmt.multiply(topicExpenseRate).setScale(2,RoundingMode.HALF_UP);

    }
}
