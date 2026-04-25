package com.loanmanagement.strategy;

import com.loanmanagement.client.LoanClient;
import com.loanmanagement.dto.LoanDto;
import com.loanmanagement.entity.RepaymentSchedule;
import com.loanmanagement.exception.InvalidAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HolidayStrategyTest {

    @InjectMocks
    private HolidayStrategy strategy;

    @Mock
    private LoanClient loanClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void apply_success() {
        List<RepaymentSchedule> list = createList();

        when(loanClient.getLoan(1L)).thenReturn(mockLoan());

        strategy.apply(list, 1L, 1, BigDecimal.ZERO);

        assertTrue(list.get(0).isHoliday());
    }

    @Test
    void apply_invalidMonth() {
        List<RepaymentSchedule> list = createList();

        assertThrows(InvalidAmountException.class,
                () -> strategy.apply(list, 1L, 5, BigDecimal.ZERO));
    }

    @Test
    void apply_alreadyPaid() {
        List<RepaymentSchedule> list = createList();
        list.get(0).setPaid(true);

        assertThrows(InvalidAmountException.class,
                () -> strategy.apply(list, 1L, 1, BigDecimal.ZERO));
    }

    @Test
    void apply_alreadyHoliday() {
        List<RepaymentSchedule> list = createList();
        list.get(0).setHoliday(true);

        assertThrows(InvalidAmountException.class,
                () -> strategy.apply(list, 1L, 1, BigDecimal.ZERO));
    }
    private List<RepaymentSchedule> createList() {
        RepaymentSchedule r1 = new RepaymentSchedule();
        r1.setMonth(1);
        r1.setBalance(BigDecimal.valueOf(10000));
        r1.setPaid(false);
        r1.setHoliday(false);

        RepaymentSchedule r2 = new RepaymentSchedule();
        r2.setMonth(2);
        r2.setBalance(BigDecimal.valueOf(9000));

        return new ArrayList<>(List.of(r1, r2));
    }

    private LoanDto mockLoan() {
        LoanDto loan = new LoanDto();
        loan.setInterestRate(BigDecimal.valueOf(10));
        loan.setStartDate(LocalDate.now());
        return loan;
    }
}