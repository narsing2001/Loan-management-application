package com.loanmanagement.controller;

import com.loanmanagement.dto.OverdraftResponseDto;
import com.loanmanagement.service.OverdraftService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OverdraftController.class)
class OverdraftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OverdraftService service;
    @Test
    void getOD_success() throws Exception {
        when(service.getOD(1L)).thenReturn(new OverdraftResponseDto());

        mockMvc.perform(get("/api/overdraft/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getOD_invalidLoanId() throws Exception {
        mockMvc.perform(get("/api/overdraft/0"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void withdraw_success() throws Exception {
        mockMvc.perform(post("/api/overdraft/withdraw")
                        .param("loanId", "1")
                        .param("amount", "1000"))
                .andExpect(status().isOk());

        verify(service).withdraw(1L, new BigDecimal("1000"));
    }

    @Test
    void withdraw_invalidAmount() throws Exception {
        mockMvc.perform(post("/api/overdraft/withdraw")
                        .param("loanId", "1")
                        .param("amount", "0"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void repay_success() throws Exception {
        mockMvc.perform(post("/api/overdraft/repay")
                        .param("loanId", "1")
                        .param("amount", "500"))
                .andExpect(status().isOk());

        verify(service).repay(1L, new BigDecimal("500"));
    }
    @Test
    void interest_success() throws Exception {
        when(service.calculateInterest(1L)).thenReturn(BigDecimal.TEN);

        mockMvc.perform(get("/api/overdraft/interest/1"))
                .andExpect(status().isOk());
    }
}