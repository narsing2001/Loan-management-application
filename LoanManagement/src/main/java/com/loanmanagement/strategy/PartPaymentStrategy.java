package com.loanmanagement.strategy;

import com.loanmanagement.client.LoanClient;
import com.loanmanagement.constants.MessageConstants;
import com.loanmanagement.dto.LoanDto;
import com.loanmanagement.entity.RepaymentSchedule;
import com.loanmanagement.exception.InvalidAmountException;
import com.loanmanagement.util.EmiCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PartPaymentStrategy implements RepaymentStrategy {

    private final LoanClient loanClient;
    @Value("${min.part.payment}")
    private BigDecimal minPartPayment;

    @Override
    public void apply(List<RepaymentSchedule> list,
                      Long loanId,
                      int month,
                      BigDecimal amount) {

        RepaymentSchedule current = list.stream()
                .filter(x -> x.getMonth() == month)
                .findFirst()
                .orElseThrow(() -> new InvalidAmountException(MessageConstants.INVALID_MONTH));

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(MessageConstants.INVALID_AMOUNT);
        }

        if (amount.compareTo(minPartPayment) < 0) {
            throw new InvalidAmountException(MessageConstants.MIN_PART_PAYMENT);
        }

        if (amount.compareTo(current.getBalance()) > 0) {
            throw new InvalidAmountException(MessageConstants.AMOUNT_EXCEEDS);
        }

        if (current.isPaid()) {
            throw new InvalidAmountException(MessageConstants.EMI_ALREADY_PAID);
        }

        BigDecimal newBalance = current.getBalance().subtract(amount);

        LoanDto loan = loanClient.getLoan(loanId);

        int remainingMonths = list.size() - month+1;

        if (remainingMonths <= 0) {
            throw new InvalidAmountException(MessageConstants.NO_TENURE_LEFT);
        }
        BigDecimal newEmi = EmiCalculator.calculate(
                newBalance,
                loan.getInterestRate(),
                remainingMonths
        );

        BigDecimal balance = newBalance;

        BigDecimal rate = loan.getInterestRate()
                .divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);

        for (RepaymentSchedule r : list) {

            if (r.getMonth() > month) {

                BigDecimal interest = balance.multiply(rate);
                BigDecimal principal = newEmi.subtract(interest);

                balance = balance.subtract(principal);

                r.setEmi(newEmi);
                r.setInterest(interest);
                r.setPrincipal(principal);
                r.setBalance(balance);
            }
        }
    }
}