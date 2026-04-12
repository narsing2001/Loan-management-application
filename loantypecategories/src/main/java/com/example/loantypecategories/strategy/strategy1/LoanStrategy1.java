package com.example.loantypecategories.strategy.strategy1;


import com.example.loantypecategories.dto.dto1.LoanApplicationRequestDTO1;
import com.example.loantypecategories.entity.entit1.LoanApplication1;
import com.example.loantypecategories.entity.entit1.LoanStrategyConfig;

import java.math.BigDecimal;

/**
 * Enhanced strategy interface that uses database-driven configuration
 * for bank-specific, loan-type-specific, and gender-specific loan processing
 */
public interface LoanStrategy1 {

    /**
     * Validate pre-conditions before processing loan application
     * (e.g., admission letter for international, institution approval for domestic)
     */
    void validatePreConditions(LoanApplicationRequestDTO1 request) throws InstitutionNotApprovedException;

    /**
     * Calculate final interest rate based on strategy configuration
     * Applies base rate, gender discount, government bank discount
     */
    BigDecimal calculateFinalInterestRate(
            LoanApplicationRequestDTO1 request,
            LoanStrategyConfig config
    );

    /**
     * Determine approved loan amount based on:
     * - Requested amount
     * - Strategy max limit
     * - Course type
     * - Bank policies
     */
    BigDecimal determineApprovedAmount(
            LoanApplicationRequestDTO1 request,
            LoanStrategyConfig config
    );

    /**
     * Calculate coverage breakdown (tuition, accommodation, travel, etc.)
     * based on strategy configuration ratios
     */
    void calculateCoverage(
            LoanApplicationRequestDTO1 request,
            LoanApplication1 application,
            LoanStrategyConfig config
    );

    /**
     * Calculate processing fee based on approved amount and strategy config
     */
    BigDecimal calculateProcessingFee(
            BigDecimal approvedAmount,
            LoanStrategyConfig config
    );

    /**
     * Determine loan tenure based on:
     * - User preference
     * - Strategy min/max limits
     * - Course duration
     */
    Integer determineTenure(
            LoanApplicationRequestDTO1 request,
            LoanStrategyConfig config
    );

    /**
     * Check if collateral is required based on:
     * - Approved amount
     * - Strategy collateral threshold
     * - Bank policies
     */
    boolean isCollateralRequired(
            BigDecimal approvedAmount,
            LoanStrategyConfig config
    );

    /**
     * Get strategy type identifier
     */
    String getStrategyType();
}