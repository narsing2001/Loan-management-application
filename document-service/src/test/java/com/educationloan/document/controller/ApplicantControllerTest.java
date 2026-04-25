package com.educationloan.document.controller;

import com.educationloan.document.dto.ApplicantResponseDTO;
import com.educationloan.document.globalExceptionHandling.CustomException.ApplicantNotFoundException;
import com.educationloan.document.service.ApplicantService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApplicantController.class)
class ApplicantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicantService applicantService;

    // ---------------------------
    // ✅ POSITIVE TEST CASE
    // ---------------------------
    @Test
    void getApplicant_success() throws Exception {

        ApplicantResponseDTO dto = ApplicantResponseDTO.builder()
                .id(1L)
                .name("John")
                .dob("2000-01-01")
                .aadhaarNumber("123456789012")
                .build();

        Mockito.when(applicantService.getApplicantById(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/applicants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.aadhaarNumber").value("123456789012"));
    }

    // ---------------------------
    // ❌ NEGATIVE TEST CASE 1
    // Not Found
    // ---------------------------
    @Test
    void getApplicant_notFound() throws Exception {

        Mockito.when(applicantService.getApplicantById(1L))
                .thenThrow(new ApplicantNotFoundException("Applicant not found with id: 1"));

        mockMvc.perform(get("/applicants/1"))
                .andExpect(status().isNotFound());
    }

    // ---------------------------
    // ❌ NEGATIVE TEST CASE 2
    // Invalid ID format
    // ---------------------------
    @Test
    void getApplicant_invalidId() throws Exception {

        mockMvc.perform(get("/applicants/abc"))
                .andExpect(status().isBadRequest());
    }
}