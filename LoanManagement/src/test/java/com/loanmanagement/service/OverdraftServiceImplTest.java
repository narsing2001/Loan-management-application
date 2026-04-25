package com.loanmanagement.service;

import com.loanmanagement.entity.Overdraft;
import com.loanmanagement.enums.Status;
import com.loanmanagement.exception.InvalidAmountException;
import com.loanmanagement.exception.ResourceNotFoundException;
import com.loanmanagement.repository.OverdraftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OverdraftServiceImplTest {

    @InjectMocks
    private OverdraftServiceImpl service;

    @Mock
    private OverdraftRepository repo;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOD_success() {
        when(repo.findByLoanId(1L)).thenReturn(Optional.empty());

        service.createOD(1L, BigDecimal.valueOf(10000));

        verify(repo, times(1)).save(any(Overdraft.class));
    }

    @Test
    void createOD_alreadyExists() {
        when(repo.findByLoanId(1L)).thenReturn(Optional.of(new Overdraft()));

        service.createOD(1L, BigDecimal.valueOf(10000));

        verify(repo, never()).save(any());
    }
    @Test
    void withdraw_success() {
        Overdraft od = mockActiveOD();

        when(repo.findByLoanId(1L)).thenReturn(Optional.of(od));

        service.withdraw(1L, BigDecimal.valueOf(1000));

        verify(repo).save(od);
    }

    @Test
    void withdraw_invalidAmount() {
        assertThrows(InvalidAmountException.class,
                () -> service.withdraw(1L, BigDecimal.ZERO));
    }

    @Test
    void withdraw_limitExceeded() {
        Overdraft od = mockActiveOD();
        when(repo.findByLoanId(1L)).thenReturn(Optional.of(od));

        assertThrows(InvalidAmountException.class,
                () -> service.withdraw(1L, BigDecimal.valueOf(100000)));
    }
    @Test
    void repay_success() {
        Overdraft od = mockActiveOD();
        od.setUsedAmount(BigDecimal.valueOf(2000));

        when(repo.findByLoanId(1L)).thenReturn(Optional.of(od));

        service.repay(1L, BigDecimal.valueOf(1000));

        verify(repo).save(od);
    }

    @Test
    void repay_invalidAmount() {
        assertThrows(InvalidAmountException.class,
                () -> service.repay(1L, BigDecimal.ZERO));
    }

    @Test
    void repay_exceedsUsed() {
        Overdraft od = mockActiveOD();
        od.setUsedAmount(BigDecimal.valueOf(1000));

        when(repo.findByLoanId(1L)).thenReturn(Optional.of(od));

        assertThrows(InvalidAmountException.class,
                () -> service.repay(1L, BigDecimal.valueOf(5000)));
    }
    @Test
    void getOD_success() {
        Overdraft od = mockActiveOD();
        when(repo.findByLoanId(1L)).thenReturn(Optional.of(od));

        assertNotNull(service.getOD(1L));
    }

    @Test
    void getOD_notFound() {
        when(repo.findByLoanId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getOD(1L));
    }
    @Test
    void calculateInterest_success() {
        Overdraft od = mockActiveOD();
        od.setUsedAmount(BigDecimal.valueOf(1000));

        when(repo.findByLoanId(1L)).thenReturn(Optional.of(od));

        assertNotNull(service.calculateInterest(1L));
    }
    private Overdraft mockActiveOD() {
        Overdraft od = new Overdraft();
        od.setLoanId(1L);
        od.setLimitAmount(BigDecimal.valueOf(10000));
        od.setUsedAmount(BigDecimal.ZERO);
        od.setInterestRate(10.0);
        od.setStatus(Status.ACTIVE);
        od.setEndDate(LocalDate.now().plusMonths(5));
        return od;
    }
}