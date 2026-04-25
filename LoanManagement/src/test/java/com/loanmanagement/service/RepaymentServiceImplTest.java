package com.loanmanagement.service;

import com.loanmanagement.client.LoanClient;
import com.loanmanagement.dto.LoanDto;
import com.loanmanagement.dto.PaymentRequestDto;
import com.loanmanagement.entity.RepaymentSchedule;
import com.loanmanagement.exception.InvalidAmountException;
import com.loanmanagement.exception.ResourceNotFoundException;
import com.loanmanagement.repository.RepaymentRepository;
import com.loanmanagement.strategy.RepaymentStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RepaymentServiceImplTest {

    @InjectMocks
    private RepaymentServiceImpl service;

    @Mock
    private RepaymentRepository repository;

    @Mock
    private LoanClient loanClient;

    @Mock
    private OverdraftService overdraftService;

    @Mock(name = "holidayStrategy")
    private RepaymentStrategy holidayStrategy;

    @Mock(name = "partPaymentStrategy")
    private RepaymentStrategy partPaymentStrategy;

    @BeforeEach
    void setup() {
        service = new RepaymentServiceImpl(
                repository,
                overdraftService,
                loanClient,
                holidayStrategy,
                partPaymentStrategy
        );
        ReflectionTestUtils.setField(service, "prepaymentPercent", BigDecimal.valueOf(2.0));
    }
    @Test
    void markHoliday_success() {

        Long loanId = 1L;
        int month = 1;

        List<RepaymentSchedule> list = new ArrayList<>();

        RepaymentSchedule r = new RepaymentSchedule();
        r.setMonth(1);
        r.setPaid(false);

        list.add(r);

        when(repository.findByLoanIdOrderByMonthAsc(loanId))
                .thenReturn(list);

        service.markHoliday(loanId, month);

        verify(holidayStrategy).apply(list, loanId, month, BigDecimal.ZERO);
        verify(repository).saveAll(list);
    }
    @Test
    void partPayment_success() {

        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setLoanId(1L);
        dto.setMonth(1);
        dto.setAmount(BigDecimal.valueOf(2000));

        List<RepaymentSchedule> list = new ArrayList<>();

        RepaymentSchedule r = new RepaymentSchedule();
        r.setMonth(1);
        r.setPaid(false);

        list.add(r);

        when(repository.findByLoanIdOrderByMonthAsc(1L))
                .thenReturn(list);

        service.partPayment(dto);

        verify(partPaymentStrategy).apply(list, 1L, 1, BigDecimal.valueOf(2000));
        verify(repository).saveAll(list);
    }
    @Test
    void getSchedule_notFound() {

        when(repository.findByLoanIdOrderByMonthAsc(1L))
                .thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getSchedule(1L);
        });
    }

    @Test
    void partPayment_invalidAmount() {

        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setLoanId(1L);
        dto.setMonth(1);
        dto.setAmount(BigDecimal.ZERO);

        when(repository.findByLoanIdOrderByMonthAsc(1L))
                .thenReturn(List.of(new RepaymentSchedule()));

        assertThrows(InvalidAmountException.class, () -> {
            service.partPayment(dto);
        });
    }
    @Test
    void payEmi_notFound() {

        when(repository.findByLoanIdAndMonth(1L, 1))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.payEmi(1L, 1, BigDecimal.valueOf(1000));
        });
    }
    @Test
    void payEmi_lessAmount() {

        RepaymentSchedule r = new RepaymentSchedule();
        r.setPaid(false);
        r.setEmi(BigDecimal.valueOf(1000));

        when(repository.findByLoanIdAndMonth(1L, 1))
                .thenReturn(Optional.of(r));

        assertThrows(InvalidAmountException.class, () -> {
            service.payEmi(1L, 1, BigDecimal.valueOf(500));
        });
    }
    @Test
    void payEmi_alreadyPaid() {

        RepaymentSchedule r = new RepaymentSchedule();
        r.setPaid(true);

        when(repository.findByLoanIdAndMonth(1L, 1))
                .thenReturn(Optional.of(r));

        assertThrows(InvalidAmountException.class, () -> {
            service.payEmi(1L, 1, BigDecimal.valueOf(1000));
        });
    }
    @Test
    void getEmi_notFound() {

        when(repository.findByLoanIdAndMonth(1L, 1))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getEmi(1L, 1);
        });
    }
    @Test
    void prepayment_success() {

        List<RepaymentSchedule> list = new ArrayList<>();

        RepaymentSchedule r = new RepaymentSchedule();
        r.setPaid(false);
        r.setPrincipal(BigDecimal.valueOf(1000));
        r.setInterest(BigDecimal.valueOf(100));

        r.setBalance(BigDecimal.valueOf(10000));

        list.add(r);

        when(repository.findByLoanIdOrderByMonthAsc(1L))
                .thenReturn(list);

        LoanDto loan = new LoanDto();
        loan.setLockInMonths(0);
        loan.setStartDate(LocalDate.now().minusMonths(10));

        when(loanClient.getLoan(1L)).thenReturn(loan);

        service.prepayment(1L);

        assertTrue(list.get(0).isPaid());
    }
    @Test
    void prepayment_lockInFail() {

        List<RepaymentSchedule> list = new ArrayList<>();
        list.add(new RepaymentSchedule());

        when(repository.findByLoanIdOrderByMonthAsc(1L))
                .thenReturn(list);

        LoanDto loan = new LoanDto();
        loan.setLockInMonths(10);
        loan.setStartDate(LocalDate.now());

        when(loanClient.getLoan(1L)).thenReturn(loan);

        assertThrows(InvalidAmountException.class, () -> {
            service.prepayment(1L);
        });
    }
    @Test
    void generateSchedule_alreadyExists() {

        when(repository.existsByLoanId(1L)).thenReturn(true);

        assertThrows(InvalidAmountException.class, () -> {
            service.generateSchedule(1L);
        });
    }
    @Test
    void markHoliday_invalidMonth() {

        RepaymentSchedule r = new RepaymentSchedule();
        r.setMonth(1);

        when(repository.findByLoanIdOrderByMonthAsc(1L))
                .thenReturn(List.of(r));

        assertThrows(InvalidAmountException.class, () -> {
            service.markHoliday(1L, 5);
        });
    }
    @Test
    void partPayment_alreadyPaid() {

        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setLoanId(1L);
        dto.setMonth(1);
        dto.setAmount(BigDecimal.valueOf(5000));

        RepaymentSchedule r = new RepaymentSchedule();
        r.setMonth(1);
        r.setPaid(true);

        when(repository.findByLoanIdOrderByMonthAsc(1L))
                .thenReturn(List.of(r));
        service.partPayment(dto);
        verify(partPaymentStrategy).apply(any(), eq(1L), eq(1), eq(BigDecimal.valueOf(5000)));
    }
    @Test
    void generateSchedule_success() {

        when(repository.existsByLoanId(1L)).thenReturn(false);

        LoanDto loan = new LoanDto();
        loan.setPrincipal(BigDecimal.valueOf(10000));
        loan.setInterestRate(BigDecimal.valueOf(10));
        loan.setTenure(12);

        when(loanClient.getLoan(1L)).thenReturn(loan);

        service.generateSchedule(1L);

        verify(repository, atLeastOnce()).save(any());
    }
    @Test
    void prepayment_scheduleNotFound() {

        when(repository.findByLoanIdOrderByMonthAsc(1L))
                .thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.prepayment(1L);
        });
    }
    @Test
    void getEmi_success() {

        RepaymentSchedule r = new RepaymentSchedule();
        r.setMonth(1);

        when(repository.findByLoanIdAndMonth(1L, 1))
                .thenReturn(Optional.of(r));

        assertNotNull(service.getEmi(1L, 1));
    }

}