package com.loanmanagement.repository;

import com.loanmanagement.entity.RepaymentSchedule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class RepaymentRepositoryTest {

    @Autowired
    private RepaymentRepository repository;

    @Test
    void findByLoanId_success() {

        RepaymentSchedule r = RepaymentSchedule.builder()
                .loanId(1L)
                .month(1)
                .emi(BigDecimal.valueOf(1000))
                .build();

        repository.save(r);

        List<RepaymentSchedule> result =
                repository.findByLoanIdOrderByMonthAsc(1L);

        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getLoanId());
    }
    @Test
    void findByLoanId_notFound() {

        List<RepaymentSchedule> result =
                repository.findByLoanIdOrderByMonthAsc(999L);

        assertTrue(result.isEmpty());
    }
    @Test
    void findByLoanIdAndMonth_success() {

        RepaymentSchedule r = RepaymentSchedule.builder()
                .loanId(1L)
                .month(1)
                .emi(BigDecimal.valueOf(1000))
                .build();

        repository.save(r);

        Optional<RepaymentSchedule> result =
                repository.findByLoanIdAndMonth(1L, 1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getMonth());
    }
}