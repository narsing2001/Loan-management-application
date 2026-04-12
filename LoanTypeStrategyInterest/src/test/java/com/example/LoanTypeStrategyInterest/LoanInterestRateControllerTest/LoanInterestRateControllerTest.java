package com.example.LoanTypeStrategyInterest.LoanInterestRateControllerTest;



import com.example.LoanTypeStrategyInterest.controller.LoanInterestRateController;
import com.example.LoanTypeStrategyInterest.dto.dto1.InterestRateCalculationRequestDTO;
import com.example.LoanTypeStrategyInterest.dto.dto1.InterestRateCalculationResponseDTO;
import com.example.LoanTypeStrategyInterest.dto.dto1.mockdto.InterestRateCalculationMockRequestDTO;
import com.example.LoanTypeStrategyInterest.dto.dto1.mockdto.InterestRateCalculationMockResponseDTO;
import com.example.LoanTypeStrategyInterest.exception.BinarySearchException;
import com.example.LoanTypeStrategyInterest.exception.InterestComputationException;

import com.example.LoanTypeStrategyInterest.globalexception.GlobalExceptionHandler;
import com.example.LoanTypeStrategyInterest.globalexception.GlobalExceptionHandler1;
import com.example.LoanTypeStrategyInterest.service.serv1.InterestRateCalculationService;
import com.example.LoanTypeStrategyInterest.service.serv1.LoanInterestCalculationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;


@ExtendWith(MockitoExtension.class)
@Import( {GlobalExceptionHandler1.class, GlobalExceptionHandler1.class})
class LoanInterestRateControllerTest {

    @Mock
    private LoanInterestCalculationService loanInterestCalculationService;

    @Mock
    private InterestRateCalculationService interestRateCalculationService;

    @InjectMocks
    private LoanInterestRateController loanInterestRateController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private InterestRateCalculationMockRequestDTO requestDTO;
    private InterestRateCalculationMockResponseDTO responseDTO;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(loanInterestRateController)
                .setControllerAdvice( new GlobalExceptionHandler1(),new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();


        requestDTO = new InterestRateCalculationMockRequestDTO();
        requestDTO.setApprovedAmount(new BigDecimal("100000"));
        requestDTO.setMonthlyEmi(new BigDecimal("5000"));
        requestDTO.setTenureYears(5);

        responseDTO = new InterestRateCalculationMockResponseDTO();
        responseDTO.setInterestRate(new BigDecimal("10.5"));
        responseDTO.setCalculationMethod("Newton-Raphson Method");
        responseDTO.setPrincipalAmount(new BigDecimal("100000"));
        responseDTO.setMonthlyEmi(new BigDecimal("5000"));
        responseDTO.setTenureYears(5);
    }

    // Calculate with Strategy Tests--------------------------------

    @Test
    void testCalculateLoanInterestWithStrategySuccess() throws Exception {
        InterestRateCalculationResponseDTO responseDTO = new InterestRateCalculationResponseDTO();
        responseDTO.setCalculatedAnnualInterestRate(BigDecimal.valueOf(10.5));
        responseDTO.setCalculationMethod("Newton-Raphson Method");
        responseDTO.setApprovedAmount(new BigDecimal("100000"));
        responseDTO.setMonthlyEmi(new BigDecimal("5000"));
        responseDTO.setTenureYears(5);

        when(loanInterestCalculationService.computeLoanInterestRate(any(InterestRateCalculationRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/interest/calculate-with-strategy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.calculatedAnnualInterestRate", is(10.5)))
                .andExpect(jsonPath("$.calculationMethod", is("Newton-Raphson Method")))
                .andExpect(jsonPath("$.approvedAmount", is(100000)))
                .andExpect(jsonPath("$.monthlyEmi", is(5000)))
                .andExpect(jsonPath("$.tenureYears", is(5)));

        verify(loanInterestCalculationService, times(1))
                .computeLoanInterestRate(any(InterestRateCalculationRequestDTO.class));
    }


    @Test
    void testCalculateLoanInterestWithStrategyInvalidRequest() throws Exception {

        InterestRateCalculationRequestDTO invalidRequest = new InterestRateCalculationRequestDTO();
        mockMvc.perform(post("/api/v1/interest/calculate-with-strategy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("INVALID_EMPTY_LOAN_PARAMETERS")))
                .andExpect(jsonPath("$.message", is("Missing required fields in loan request")));

    }

    @Test
    void testCalculateLoanInterestWithStrategyServiceThrowsInterestComputationException() throws Exception {

        when(loanInterestCalculationService.computeLoanInterestRate(any(InterestRateCalculationRequestDTO.class)))
                .thenThrow(new InterestComputationException("Calculation failed"));

        mockMvc.perform(post("/api/v1/interest/calculate-with-strategy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Calculation failed")))
                .andExpect(jsonPath("$.errorCode", is("INTEREST_COMPUTATION_FAILED")));

        verify(loanInterestCalculationService, times(1))
                .computeLoanInterestRate(any(InterestRateCalculationRequestDTO.class));
    }

    //Calculate Interest Rate (NRM) Tests-------------------------------------------------
    @Test
    void testCalculateInterestRateSuccess() throws Exception {
        InterestRateCalculationResponseDTO responseDTO = InterestRateCalculationResponseDTO.builder()
                .calculatedAnnualInterestRate(BigDecimal.valueOf(10.5))
                .calculationMethod("Newton-Raphson Method")
                .approvedAmount(new BigDecimal("100000"))
                .monthlyEmi(new BigDecimal("5000"))
                .tenureYears(5)
                .build();

        when(interestRateCalculationService.calculateInterestRate(any(InterestRateCalculationRequestDTO.class)))
                .thenReturn(responseDTO);
        mockMvc.perform(post("/api/v1/interest/calculate-interest-rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.calculatedAnnualInterestRate", is(10.5)))
                .andExpect(jsonPath("$.calculationMethod", is("Newton-Raphson Method")));

        verify(interestRateCalculationService, times(1))
                .calculateInterestRate(any(InterestRateCalculationRequestDTO.class));
    }

    @Test
    void testCalculateInterestRateWithNegativeAmount() throws Exception {
        requestDTO.setApprovedAmount(new BigDecimal("-100000"));
        mockMvc.perform(post("/api/v1/interest/calculate-interest-rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(interestRateCalculationService, never())
                .calculateInterestRate(any(InterestRateCalculationRequestDTO.class));
    }

    @Test
    void testCalculateInterestRateWithZeroEmi() throws Exception {
        requestDTO.setMonthlyEmi(BigDecimal.ZERO);
        mockMvc.perform(post("/api/v1/interest/calculate-interest-rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(interestRateCalculationService, never())
                .calculateInterestRate(any(InterestRateCalculationRequestDTO.class));
    }

    @Test
    void testCalculateInterestRateWithInvalidTenure() throws Exception {

        requestDTO.setTenureYears(0);
        mockMvc.perform(post("/api/v1/interest/calculate-interest-rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(interestRateCalculationService, never())
                .calculateInterestRate(any(InterestRateCalculationRequestDTO.class));
    }

    // Calculate Interest Rate Binary Search Tests--------------------------

    @Test
    void testCalculateInterestRateBinarySearchSuccess() throws Exception {

        InterestRateCalculationResponseDTO responseDTO = InterestRateCalculationResponseDTO.builder()
                .calculatedAnnualInterestRate(BigDecimal.valueOf(10.5))
                .totalMonths(60)
                .calculationMethod("Binary Search Method")
                .approvedAmount(new BigDecimal("100000"))
                .monthlyEmi(new BigDecimal("5000"))
                .tenureYears(5)
                .build();

        // Given
        responseDTO.setCalculationMethod("Binary Search Method");
        when(interestRateCalculationService.calculateInterestRateBinarySearch(any(InterestRateCalculationRequestDTO.class)))
                .thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/interest/calculate-interest-rate-binary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.calculatedAnnualInterestRate", is(10.5)))
                .andExpect(jsonPath("$.calculationMethod", is("Binary Search Method")));

        verify(interestRateCalculationService, times(1))
                .calculateInterestRateBinarySearch(any(InterestRateCalculationRequestDTO.class));
    }

    @Test
    void testCalculateInterestRateBinarySearchInvalidRequest() throws Exception {
        InterestRateCalculationRequestDTO invalidRequest = new InterestRateCalculationRequestDTO();
        mockMvc.perform(post("/api/v1/interest/calculate-interest-rate-binary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(interestRateCalculationService, never())
                .calculateInterestRateBinarySearch(any(InterestRateCalculationRequestDTO.class));
    }

    @Test
    void testCalculateInterestRateBinarySearchServiceThrowsException() throws Exception {
        when(interestRateCalculationService.calculateInterestRateBinarySearch(any(InterestRateCalculationRequestDTO.class)))
                .thenThrow(new BinarySearchException("Binary search calculation failed"));
        mockMvc.perform(post("/api/v1/interest/calculate-interest-rate-binary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        verify(interestRateCalculationService, times(1))
                .calculateInterestRateBinarySearch(any(InterestRateCalculationRequestDTO.class));
    }

    @Test
    void testCalculateInterestRateBinarySearchLargeAmount() throws Exception {
        requestDTO.setApprovedAmount(new BigDecimal("10000000"));
        requestDTO.setMonthlyEmi(new BigDecimal("200000"));
        InterestRateCalculationResponseDTO responseDTO = InterestRateCalculationResponseDTO.builder()
                .calculatedAnnualInterestRate(BigDecimal.valueOf(10.5))
                .calculationMethod("Binary Search Method")
                .approvedAmount(new BigDecimal("10000000"))
                .monthlyEmi(new BigDecimal("200000"))
                .tenureYears(10)
                .build();
        when(interestRateCalculationService.calculateInterestRateBinarySearch(any(InterestRateCalculationRequestDTO.class)))
                .thenReturn(responseDTO);
        mockMvc.perform(post("/api/v1/interest/calculate-interest-rate-binary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        verify(interestRateCalculationService, times(1))
                .calculateInterestRateBinarySearch(any(InterestRateCalculationRequestDTO.class));
    }

    //Health Check Tests ------------------------------------

    @Test
    void testHealthCheckSuccess() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/interest/health")
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Loan Interest Calculation Service is running"));

        verifyNoInteractions(loanInterestCalculationService);
        verifyNoInteractions(interestRateCalculationService);
    }

    // Integration Tests for  NRM and BSM-----------------------------------------------------------

    @Test
    void testMultipleCalculationMethodsCompareResults() throws Exception {
        InterestRateCalculationResponseDTO nrmResponse = new InterestRateCalculationResponseDTO();
        nrmResponse.setCalculatedAnnualInterestRate(new BigDecimal("10.5"));
        nrmResponse.setCalculationMethod("Newton-Raphson Method");

        InterestRateCalculationResponseDTO bsResponse = new InterestRateCalculationResponseDTO();
        bsResponse.setCalculatedAnnualInterestRate(new BigDecimal("10.52"));
        bsResponse.setCalculationMethod("Binary Search Method");

        when(interestRateCalculationService.calculateInterestRate(any(InterestRateCalculationRequestDTO.class)))
                .thenReturn(nrmResponse);
        when(interestRateCalculationService.calculateInterestRateBinarySearch(any(InterestRateCalculationRequestDTO.class)))
                .thenReturn(bsResponse);

        mockMvc.perform(post("/api/v1/interest/calculate-interest-rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calculationMethod", is("Newton-Raphson Method")));

        mockMvc.perform(post("/api/v1/interest/calculate-interest-rate-binary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calculationMethod", is("Binary Search Method")));

        verify(interestRateCalculationService, times(1)).calculateInterestRate(any());
        verify(interestRateCalculationService, times(1)).calculateInterestRateBinarySearch(any());
    }

    @Test
    void testDifferentTenurePeriods() throws Exception {
        InterestRateCalculationResponseDTO responseDTO = InterestRateCalculationResponseDTO.builder()
                .calculatedAnnualInterestRate(BigDecimal.valueOf(10.5))
                .calculationMethod("Newton-Raphson Method")
                .approvedAmount(new BigDecimal("100000"))
                .monthlyEmi(new BigDecimal("5000"))
                .tenureYears(5)
                .build();

        when(interestRateCalculationService.calculateInterestRate(any(InterestRateCalculationRequestDTO.class)))
                .thenReturn(responseDTO);

        // Test with different tenure periods if the tenure year is greater than 20 will throw exception
        int[] tenures = {1, 5, 10, 20};

        for (Integer tenure : tenures) {
            requestDTO.setTenureYears(tenure);
            mockMvc.perform(post("/api/v1/interest/calculate-interest-rate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        verify(interestRateCalculationService, times(tenures.length))
                .calculateInterestRate(any(InterestRateCalculationRequestDTO.class));
    }

    // Edge Cases--------------------------------------------------------------------------
    @Test
    void testCalculateInterestRateWithVerySmallAmount() throws Exception {
        requestDTO.setApprovedAmount(new BigDecimal("1000"));
        requestDTO.setMonthlyEmi(new BigDecimal("100"));
        InterestRateCalculationResponseDTO responseDTO = InterestRateCalculationResponseDTO.builder()
                .calculatedAnnualInterestRate(BigDecimal.valueOf(10.5))
                .calculationMethod("Newton-Raphson Method")
                .approvedAmount(new BigDecimal("1000"))
                .monthlyEmi(new BigDecimal("100"))
                .tenureYears(1)
                .build();
        when(interestRateCalculationService.calculateInterestRate(any(InterestRateCalculationRequestDTO.class)))
                .thenReturn(responseDTO);
        mockMvc.perform(post("/api/v1/interest/calculate-interest-rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(interestRateCalculationService, times(1))
                .calculateInterestRate(any(InterestRateCalculationRequestDTO.class));
    }

    @Test
    void testCalculateInterestRateMissingContentType() throws Exception {
        mockMvc.perform(post("/api/v1/interest/calculate-interest-rate")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
        verify(interestRateCalculationService, never())
                .calculateInterestRate(any(InterestRateCalculationRequestDTO.class));
    }

    @Test
    void testCalculateInterestRateMalformedJson() throws Exception {
        mockMvc.perform(post("/api/v1/interest/calculate-interest-rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(interestRateCalculationService, never())
                .calculateInterestRate(any(InterestRateCalculationRequestDTO.class));
    }
}
