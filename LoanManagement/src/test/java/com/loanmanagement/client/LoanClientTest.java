package com.loanmanagement.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanmanagement.dto.LoanDto;
import com.loanmanagement.exception.InvalidAmountException;
import com.loanmanagement.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoanClientTest {

    private LoanClient loanClient;

    @BeforeEach
    void setup() {
        loanClient = new LoanClient(new ObjectMapper());
    }

    @Test
    void getLoan_invalidId() {
        assertThrows(InvalidAmountException.class,
                () -> loanClient.getLoan(null));
    }

    @Test
    void getLoan_notFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> loanClient.getLoan(999L));

    }
    @Test
    void getLoan_success() {

        LoanDto loan = loanClient.getLoan(1L);

        assertNotNull(loan);
    }

}