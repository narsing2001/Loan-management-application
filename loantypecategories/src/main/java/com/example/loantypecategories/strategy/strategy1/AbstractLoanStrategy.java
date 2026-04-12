package com.example.loantypecategories.strategy.strategy1;

import com.example.loantypecategories.constant.Gender;
import com.example.loantypecategories.dto.dto1.LoanApplicationRequestDTO1;
import com.example.loantypecategories.entity.entit1.LoanApplication1;
import com.example.loantypecategories.entity.entit1.LoanStrategyConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Abstract base class providing common implementation for loan strategies
 */
public abstract class AbstractLoanStrategy implements LoanStrategy1 {

    @Override
    public BigDecimal calculateFinalInterestRate(LoanApplicationRequestDTO1 request, LoanStrategyConfig config) {

        BigDecimal finalRate = config.getBaseInterestRate();

        // Apply female discount
        if (request.getGender() == Gender.FEMALE && config.getFemaleDiscount() != null) {
            finalRate = finalRate.subtract(config.getFemaleDiscount());
        }

        // Apply government bank discount
        if (config.getBankType().isGovernmentBank() && config.getGovtBankDiscount() != null) {
            finalRate = finalRate.subtract(config.getGovtBankDiscount());
        }

        return finalRate.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal determineApprovedAmount(LoanApplicationRequestDTO1 request, LoanStrategyConfig config) {

        BigDecimal requested = request.getRequestedAmount();
        BigDecimal maxAllowed = config.getMaxLoanAmount();
        BigDecimal minAllowed = config.getMinLoanAmount();

        // Approved amount = min(requested, max allowed)
        BigDecimal approved = requested.min(maxAllowed);

        // Ensure it meets minimum
        if (approved.compareTo(minAllowed) < 0) {
            approved = minAllowed;
        }

        return approved.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void calculateCoverage(
            LoanApplicationRequestDTO1 request,
            LoanApplication1 application,
            LoanStrategyConfig config) {

        BigDecimal approvedAmount = application.getApprovedAmount();

        // Use explicit amounts if provided, otherwise use ratios from config
        application.setTuitionFeeCoverage(
                calculateCoverageAmount(request.getTuitionFee(), approvedAmount,
                        config.getTuitionCoverageRatio())
        );

        application.setAccommodationCoverage(
                calculateCoverageAmount(request.getAccommodationCost(), approvedAmount,
                        config.getAccommodationCoverageRatio())
        );

        application.setTravelExpenseCoverage(
                calculateCoverageAmount(request.getTravelExpense(), approvedAmount,
                        config.getTravelCoverageRatio())
        );

        application.setLaptopExpenseCoverage(
                calculateCoverageAmount(request.getLaptopExpense(), approvedAmount,
                        config.getLaptopCoverageRatio())
        );

        application.setExamFeeCoverage(
                calculateCoverageAmount(request.getExamFee(), approvedAmount,
                        config.getExamFeeCoverageRatio())
        );

        application.setOtherExpenseCoverage(
                calculateCoverageAmount(request.getOtherExpense(), approvedAmount,
                        config.getOtherCoverageRatio())
        );
    }

    @Override
    public BigDecimal calculateProcessingFee(
            BigDecimal approvedAmount,
            LoanStrategyConfig config) {

        BigDecimal feePercent = config.getProcessingFeePercent();
        if (feePercent == null) {
            feePercent = new BigDecimal("2.00"); // Default 2%
        }

        return approvedAmount
                .multiply(feePercent)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    @Override
    public Integer determineTenure(
            LoanApplicationRequestDTO1 request,
            LoanStrategyConfig config) {

        Integer preferred = request.getPreferredTenureYears();
        Integer maxTenure = config.getMaxTenureYears();
        Integer minTenure = config.getMinTenureYears();

        if (preferred != null) {
            // Use preferred if within limits
            if (preferred >= minTenure && preferred <= maxTenure) {
                return preferred;
            }
        }

        // Default to maximum tenure
        return maxTenure;
    }

    @Override
    public boolean isCollateralRequired(
            BigDecimal approvedAmount,
            LoanStrategyConfig config) {

        if (!config.getCollateralRequired()) {
            return false;
        }

        BigDecimal threshold = config.getCollateralThreshold();
        if (threshold == null) {
            return config.getCollateralRequired();
        }

        return approvedAmount.compareTo(threshold) > 0;
    }

    /**
     * Helper method to calculate coverage amount
     * Uses explicit amount if provided, otherwise applies ratio to approved amount
     */
    protected BigDecimal calculateCoverageAmount(
            BigDecimal explicitAmount,
            BigDecimal approvedAmount,
            BigDecimal ratio) {

        if (explicitAmount != null && explicitAmount.compareTo(BigDecimal.ZERO) > 0) {
            return explicitAmount.setScale(2, RoundingMode.HALF_UP);
        }

        if (ratio != null) {
            return approvedAmount
                    .multiply(ratio)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }
}