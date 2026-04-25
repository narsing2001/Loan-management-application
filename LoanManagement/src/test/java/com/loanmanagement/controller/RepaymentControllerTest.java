package com.loanmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanmanagement.dto.*;
import com.loanmanagement.exception.InvalidAmountException;
import com.loanmanagement.service.RepaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RepaymentController.class)
class RepaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepaymentService service;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void generate_success() throws Exception {
        mockMvc.perform(post("/api/repayment/generate/1"))
                .andExpect(status().isOk());

        verify(service).generateSchedule(1L);
    }
    @Test
    void getSchedule_success() throws Exception {
        when(service.getSchedule(1L)).thenReturn(new ScheduleResponseDto());

        mockMvc.perform(get("/api/repayment/1"))
                .andExpect(status().isOk());
    }
    @Test
    void getEmi_success() throws Exception {
        when(service.getEmi(1L, 1)).thenReturn(new RepaymentScheduleDto());

        mockMvc.perform(get("/api/repayment/1/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getEmi_invalidMonth() throws Exception {
        mockMvc.perform(get("/api/repayment/1/0"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void pay_success() throws Exception {
        mockMvc.perform(post("/api/repayment/pay")
                        .param("loanId", "1")
                        .param("month", "1")
                        .param("amount", "1000"))
                .andExpect(status().isOk());

        verify(service).payEmi(1L, 1, new BigDecimal("1000"));
    }
    @Test
    void partPayment_success() throws Exception {
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setLoanId(1L);
        dto.setMonth(1);
        dto.setAmount(BigDecimal.valueOf(1000));

        mockMvc.perform(post("/api/repayment/part")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(service).partPayment(any());
    }
    @Test
    void holiday_success() throws Exception {
        mockMvc.perform(post("/api/repayment/holiday")
                        .param("loanId", "1")
                        .param("month", "1"))
                .andExpect(status().isOk());

        verify(service).markHoliday(1L, 1);
    }
    @Test
    void preview_success() throws Exception {
        when(service.prepaymentPreview(1L)).thenReturn(
                new PrepaymentPreview(
                        BigDecimal.valueOf(1000),
                        BigDecimal.valueOf(100),
                        BigDecimal.valueOf(1100)
                )
        );

        mockMvc.perform(get("/api/repayment/prepay-preview/1"))
                .andExpect(status().isOk());
    }
    @Test
    void prepay_success() throws Exception {
        mockMvc.perform(post("/api/repayment/prepay/1"))
                .andExpect(status().isOk());

        verify(service).prepayment(1L);
    }
    @Test
    void partPayment_invalidRequest() throws Exception {

        String json = """
        {
            "loanId": null,
            "amount": -100,
            "month": 0
        }
    """;

        mockMvc.perform(post("/api/repayment/part")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
    @Test
    void handleInvalidAmountException() throws Exception {

        when(service.getSchedule(1L))
                .thenThrow(new InvalidAmountException("error"));

        mockMvc.perform(get("/api/repayment/1"))
                .andExpect(status().isBadRequest());
    }
}