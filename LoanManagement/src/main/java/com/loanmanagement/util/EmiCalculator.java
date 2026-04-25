package com.loanmanagement.util;

import com.loanmanagement.constants.MessageConstants;
import com.loanmanagement.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EmiCalculator {

    public static BigDecimal calculate(BigDecimal p, BigDecimal r, int n) {

        if (p.compareTo(BigDecimal.ZERO) <= 0 ||
                r.compareTo(BigDecimal.ZERO) <= 0 || n <= 0) {
            throw new InvalidAmountException(MessageConstants.INVALID_AMOUNT);
        }

        BigDecimal rate = r.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);

        BigDecimal onePlusRate = BigDecimal.ONE.add(rate);
        BigDecimal power = onePlusRate.pow(n);

        BigDecimal numerator = p.multiply(rate).multiply(power);
        BigDecimal denominator = power.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}
