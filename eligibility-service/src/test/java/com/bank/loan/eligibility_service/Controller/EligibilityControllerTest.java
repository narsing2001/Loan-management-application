package com.bank.loan.eligibility_service.Controller;

import com.bank.loan.eligibility_service.controller.EligibilityController;
import com.bank.loan.eligibility_service.dto.EligibilityRequestDTO;
import com.bank.loan.eligibility_service.dto.EligibilityResponseDTO;
import com.bank.loan.eligibility_service.entity.CoApplicantDetails;
import com.bank.loan.eligibility_service.entity.EducationDetails;
import com.bank.loan.eligibility_service.entity.FinancialDetails;
import com.bank.loan.eligibility_service.entity.StudentDetails;
import com.bank.loan.eligibility_service.enums.RiskCategory;
import com.bank.loan.eligibility_service.service.EligibilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EligibilityController.class)
public class EligibilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EligibilityService eligibilityService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void checkEligibility_shouldReturnApprovedResponse() throws Exception {

        EligibilityRequestDTO request = buildRequest();

        EligibilityResponseDTO response = EligibilityResponseDTO.builder()
                .eligible(true)
                .riskCategory(RiskCategory.LOW.name())
                .foir(20.0)
                .ltvRatio(80.0)
                .maxEligibleAmount(450000.0)
                .message("Loan Approved")
                .build();

        Mockito.when(eligibilityService.checkEligibility(Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/eligibility/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value(true))
                .andExpect(jsonPath("$.riskCategory").value("LOW"))
                .andExpect(jsonPath("$.message").value("Loan Approved"));
    }

    @Test
    void checkEligibility_shouldReturnRejectedResponse() throws Exception {

        EligibilityRequestDTO request = buildRequest();

        EligibilityResponseDTO response = EligibilityResponseDTO.builder()
                .eligible(false)
                .riskCategory(RiskCategory.HIGH.name())
                .foir(70.0)
                .ltvRatio(95.0)
                .maxEligibleAmount(200000.0)
                .message("Loan Rejected")
                .build();

        Mockito.when(eligibilityService.checkEligibility(Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/eligibility/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value(false))
                .andExpect(jsonPath("$.riskCategory").value("HIGH"))
                .andExpect(jsonPath("$.message").value("Loan Rejected"));
    }




    private EligibilityRequestDTO buildRequest() {


        StudentDetails student = StudentDetails.builder()
                .age(22)
                .studentName("komal")
                .build();

        EducationDetails education = EducationDetails.builder()
                .courseName("engineering")
                .collegeName("raisoni")
                .build();

        FinancialDetails financial = FinancialDetails.builder()
                .annualIncome(600000.0)
                .existingEMI(10000.0)
                .courseFees(500000.0)
                .requestedLoanAmount(400000.0)
                .creditScore(750)
                .build();

        CoApplicantDetails coApplicant = CoApplicantDetails.builder()
                .coApplicationPresent(true)
                .coApplicantName("ramesh sharma")
                .coApplicantRelation("father")
                .coApplicantIncome(400000.0)
                .coApplicantCreditScore(720)
                .build();

        EligibilityRequestDTO request = new EligibilityRequestDTO();
        request.setStudentDetails(student);
        request.setEducationDetails(education);
        request.setFinancialDetails(financial);
        request.setCoApplicantDetails(coApplicant);

        return request;
    }



}