package com.example.LoanTypeStrategyInterest.service.serv1;

import com.example.LoanTypeStrategyInterest.dto.dto1.InterestRateCalculationRequestDTO;
import com.example.LoanTypeStrategyInterest.dto.dto1.InterestRateCalculationResponseDTO;
import com.example.LoanTypeStrategyInterest.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class InterestRateCalculationService {

    private static final double TOLERANCE = 0.0000001;
    private static final int MAX_ITERATIONS = 100;

    /**
     * Calculate interest rate from EMI, principal, and tenure using Newton-Raphson method
     */
    public InterestRateCalculationResponseDTO calculateInterestRate(InterestRateCalculationRequestDTO request) {
        BigDecimal principal = request.getApprovedAmount();
        BigDecimal emi = request.getMonthlyEmi();
        int tenureYears = request.getTenureYears();
        int totalMonths = tenureYears * 12;

        // Validate input
        validateInput(principal, emi, totalMonths);

        // Calculate using Newton-Raphson method
        double monthlyRate = calculateMonthlyRateNewtonRaphson(principal.doubleValue(), emi.doubleValue(), totalMonths);

        // Convert to annual rate
        BigDecimal annualRate = BigDecimal.valueOf(monthlyRate * 1200).setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyRatePercent = BigDecimal.valueOf(monthlyRate * 100).setScale(4, RoundingMode.HALF_UP);

        // Calculate total payment and interest
        BigDecimal totalPayment = emi.multiply(new BigDecimal(totalMonths)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalInterest = totalPayment.subtract(principal).setScale(2, RoundingMode.HALF_UP);

        log.info("Interest rate calculated - Principal: {}, EMI: {}, Tenure: {} years, Rate: {}%",
                principal, emi, tenureYears, annualRate);

        return InterestRateCalculationResponseDTO.builder()
                .approvedAmount(principal)
                .monthlyEmi(emi)
                .tenureYears(tenureYears)
                .totalMonths(totalMonths)
                .calculatedAnnualInterestRate(annualRate)
                .calculatedMonthlyInterestRate(monthlyRatePercent)
                .totalPayment(totalPayment)
                .totalInterestPaid(totalInterest)
                .calculationMethod("Newton Raphson statistical approach")
                .build();
    }

    //Newton-Raphson method to find monthly interest rate Formula: EMI = P × r × (1+r)^n / [(1+r)^n - 1]
    //We need to solve for r given EMI, P, and n

    private double calculateMonthlyRateNewtonRaphson(double principal, double emi, int months) {
        // Initial guess: 10% annual rate = 0.00833 monthly
        double r = 0.00833;

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            double powerTerm = Math.pow(1 + r, months);

            // f(r) = P × r × (1+r)^n - EMI × [(1+r)^n - 1]
            double f = principal * r * powerTerm - emi * (powerTerm - 1);

            // f'(r) = derivative of f(r)
            // f'(r) = P × (1+r)^n + P × r × n × (1+r)^(n-1) - EMI × n × (1+r)^(n-1)
            double powerTermMinusOne = Math.pow(1 + r, months - 1);
            double fPrime = principal * powerTerm + principal * r * months * powerTermMinusOne - emi * months * powerTermMinusOne;

            // Newton-Raphson formula: r_new = r_old - f(r)/f'(r)
            double rNew = r - f / fPrime;

            // Check for convergence
            if (Math.abs(rNew - r) < TOLERANCE) {
                return rNew;
            }

            r = rNew;

            // Ensure rate stays positive and reasonable
            if (r <= 0) {
                r = 0.00001;
            } else if (r > 0.05) { // Cap at 60% annual
                r = 0.05;
            }
        }

        log.warn("Newton-Raphson did not converge within {} iterations. Using last value: {}", MAX_ITERATIONS, r);
        return r;
    }

    /**
     * Alternative method using Binary Search (more stable for edge cases)
     */
    public InterestRateCalculationResponseDTO calculateInterestRateBinarySearch(InterestRateCalculationRequestDTO request) {
        BigDecimal principal = request.getApprovedAmount();
        BigDecimal emi = request.getMonthlyEmi();
        int tenureYears = request.getTenureYears();
        int totalMonths = tenureYears * 12;

        validateInput(principal, emi, totalMonths);

        double monthlyRate = binarySearchForRate(principal.doubleValue(), emi.doubleValue(), totalMonths);

        BigDecimal annualRate = BigDecimal.valueOf(monthlyRate * 1200).setScale(2, RoundingMode.HALF_UP);
        BigDecimal monthlyRatePercent = BigDecimal.valueOf(monthlyRate * 100).setScale(4, RoundingMode.HALF_UP);
        BigDecimal totalPayment = emi.multiply(new BigDecimal(totalMonths)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalInterest = totalPayment.subtract(principal).setScale(2, RoundingMode.HALF_UP);

        return InterestRateCalculationResponseDTO.builder()
                .approvedAmount(principal)
                .monthlyEmi(emi)
                .tenureYears(tenureYears)
                .totalMonths(totalMonths)
                .calculatedAnnualInterestRate(annualRate)
                .calculatedMonthlyInterestRate(monthlyRatePercent)
                .totalPayment(totalPayment)
                .totalInterestPaid(totalInterest)
                .calculationMethod("Binary Search")
                .build();
    }

    /**
     * Binary search implementation to find interest rate
     */
    private double binarySearchForRate(double principal, double emi, int months) {
        double low = 0.0;
        double high = 0.05; // 60% annual rate
        double mid;

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            mid = (low + high) / 2;
            double calculatedEmi = calculateEMI(principal, mid, months);

            if (Math.abs(calculatedEmi - emi) < TOLERANCE) {
                return mid;
            }

            if (calculatedEmi < emi) {
                low = mid;
            } else {
                high = mid;
            }
        }

        log.warn("Binary search did not converge within {} iterations. Using mid value.", MAX_ITERATIONS);
        return (low + high) / 2;
    }

    /**
     * Calculate EMI for given principal, rate, and tenure
     */
    private double calculateEMI(double principal, double monthlyRate, int months) {
        if (monthlyRate == 0) {
            return principal / months;
        }

        double powerTerm = Math.pow(1 + monthlyRate, months);
        return principal * monthlyRate * powerTerm / (powerTerm - 1);
    }

    /**
     * Validate input parameters
     */
    private void validateInput(BigDecimal principal, BigDecimal emi, int totalMonths) {
        if (principal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPrincipalException("Principal must be greater than 0");
        }

        if (emi.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidEmiException("EMI must be greater than 0");
        }

        if (totalMonths <= 0) {
            throw new InvalidTenureException("Tenure must be greater than 0");
        }

        BigDecimal totalPayment = emi.multiply(new BigDecimal(totalMonths));
        if (totalPayment.compareTo(principal) <= 0) {
            throw new InvalidPaymentException("Total payment (" + totalPayment + ") must be greater than principal (" + principal + ")");
        }

        // Check if EMI is too low (would result in negative interest rate)
        BigDecimal minEmi = principal.divide(new BigDecimal(totalMonths), 2, RoundingMode.HALF_UP);
        if (emi.compareTo(minEmi) < 0) {
            throw new EmiTooLowException("EMI (" + emi + ") is too low. Minimum EMI for zero interest: " + minEmi);
        }
    }

    /**
     * Verify the calculated rate by recalculating EMI
     */
    public boolean verifyCalculation(BigDecimal principal, BigDecimal calculatedRate, int tenureYears, BigDecimal originalEmi) {
        BigDecimal monthlyRate = calculatedRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        int months = tenureYears * 12;
        BigDecimal powerTerm = BigDecimal.ONE.add(monthlyRate).pow(months, new java.math.MathContext(10));
        BigDecimal calculatedEmi = principal.multiply(monthlyRate).multiply(powerTerm).divide(powerTerm.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
        // Allow 0.1% tolerance
        BigDecimal tolerance = originalEmi.multiply(new BigDecimal("0.001"));
        BigDecimal difference = calculatedEmi.subtract(originalEmi).abs();
        return difference.compareTo(tolerance) <= 0;
    }

}


















