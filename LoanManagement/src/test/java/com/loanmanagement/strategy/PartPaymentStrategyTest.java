package com.loanmanagement.strategy;

import com.loanmanagement.client.LoanClient;
import com.loanmanagement.dto.LoanDto;
import com.loanmanagement.entity.RepaymentSchedule;
import com.loanmanagement.exception.InvalidAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PartPaymentStrategyTest {

    @InjectMocks
    private PartPaymentStrategy strategy;

    @Mock
    private LoanClient loanClient;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        Field field = PartPaymentStrategy.class.getDeclaredField("minPartPayment");
        field.setAccessible(true);
        field.set(strategy, BigDecimal.valueOf(500));
    }

    @Test
    void apply_success() {
        List<RepaymentSchedule> list = createList();

        when(loanClient.getLoan(1L)).thenReturn(mockLoan());

        strategy.apply(list, 1L, 1, BigDecimal.valueOf(1000));

        assertNotNull(list.get(1).getEmi());
    }

    @Test
    void apply_invalidMonth() {
        List<RepaymentSchedule> list = createList();

        assertThrows(InvalidAmountException.class,
                () -> strategy.apply(list, 1L, 5, BigDecimal.valueOf(1000)));
    }

    @Test
    void apply_invalidAmount() {
        List<RepaymentSchedule> list = createList();

        assertThrows(InvalidAmountException.class,
                () -> strategy.apply(list, 1L, 1, BigDecimal.ZERO));
    }

    @Test
    void apply_lessThanMin() {
        List<RepaymentSchedule> list = createList();

        assertThrows(InvalidAmountException.class,
                () -> strategy.apply(list, 1L, 1, BigDecimal.valueOf(100)));
    }

    @Test
    void apply_amountExceedsBalance() {
        List<RepaymentSchedule> list = createList();

        assertThrows(InvalidAmountException.class,
                () -> strategy.apply(list, 1L, 1, BigDecimal.valueOf(20000)));
    }

    @Test
    void apply_alreadyPaid() {
        List<RepaymentSchedule> list = createList();
        list.get(0).setPaid(true);

        assertThrows(InvalidAmountException.class,
                () -> strategy.apply(list, 1L, 1, BigDecimal.valueOf(1000)));
    }
    private List<RepaymentSchedule> createList() {
        RepaymentSchedule r1 = new RepaymentSchedule();
        r1.setMonth(1);
        r1.setBalance(BigDecimal.valueOf(10000));
        r1.setPaid(false);

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