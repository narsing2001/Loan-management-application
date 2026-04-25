package com.loanmanagement.strategy;

import com.loanmanagement.client.LoanClient;
import com.loanmanagement.constants.MessageConstants;
import com.loanmanagement.dto.LoanDto;
import com.loanmanagement.entity.RepaymentSchedule;
import com.loanmanagement.exception.InvalidAmountException;
import com.loanmanagement.util.EmiCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HolidayStrategy implements RepaymentStrategy {

    private final LoanClient loanClient;

    @Override
    public void apply(List<RepaymentSchedule> list,
                      Long loanId,
                      int month,
                      BigDecimal amount) {

        RepaymentSchedule r = list.stream()
                .filter(x -> x.getMonth() == month)
                .findFirst()
                .orElseThrow(() -> new InvalidAmountException(MessageConstants.INVALID_MONTH));

        if (r.isPaid()) {
            throw new InvalidAmountException(MessageConstants.ALREADY_PAID);
        }

        if (r.isHoliday()) {
            throw new InvalidAmountException(MessageConstants.HOLIDAY_ALREADY_APPLIED);
        }

        LoanDto loan = loanClient.getLoan(loanId);

        BigDecimal rate = loan.getInterestRate()
                .divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);

        // Interest calculation
        BigDecimal interest = r.getBalance().multiply(rate);
        BigDecimal newBalance = r.getBalance().add(interest);

        r.setHoliday(true);
        r.setEmi(BigDecimal.ZERO);
        r.setInterest(interest);
        r.setPrincipal(BigDecimal.ZERO);
        r.setBalance(newBalance);

        int remainingMonths = list.size() - month+1;

        if (remainingMonths <= 0) {
            throw new InvalidAmountException(MessageConstants.INVALID_AMOUNT);
        }

        BigDecimal newEmi = EmiCalculator.calculate(
                newBalance,
                loan.getInterestRate(),
                remainingMonths
        );

        BigDecimal balance = newBalance;

        for (RepaymentSchedule future : list) {

            if (future.getMonth() > month) {

                BigDecimal i = balance.multiply(rate);
                BigDecimal p = newEmi.subtract(i);

                balance = balance.subtract(p);

                future.setEmi(newEmi);
                future.setInterest(i);
                future.setPrincipal(p);
                future.setBalance(balance);
            }
        }
    }
}