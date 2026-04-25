package com.document.verification.service.controller;
import com.document.verification.service.dto.AadhaarVerifyRequest;
import com.document.verification.service.globalExceptionHandling.customException.ParserNotFoundException;
import com.document.verification.service.service.VerificationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MockAadhaarController.class)
class MockAadhaarControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void verify_validAadhaar_shouldReturnValidTrue() throws Exception {
        AadhaarVerifyRequest request = new AadhaarVerifyRequest();
        request.setAadhaarNumber("123456789012");
        mockMvc.perform(post("/aadhaar/verify")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Aadhaar valid"));
    }
    @Test
    void verify_invalidAadhaar_shouldReturnValidFalse() throws Exception {
        AadhaarVerifyRequest request = new AadhaarVerifyRequest();
        request.setAadhaarNumber("12345");
        mockMvc.perform(post("/aadhaar/verify")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Invalid Aadhaar"));
    }
    @Test
    void verify_nullAadhaar_shouldReturnInvalid() throws Exception {
        AadhaarVerifyRequest request = new AadhaarVerifyRequest();

        mockMvc.perform(post("/aadhaar/verify")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }
    @Test
    void verify_alphabetsAadhaar_shouldReturnInvalid() throws Exception {
        AadhaarVerifyRequest request = new AadhaarVerifyRequest();
        request.setAadhaarNumber("abcd1234xyz");

        mockMvc.perform(post("/aadhaar/verify")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }
    @Test
    void verify_moreThan12Digits_shouldReturnInvalid() throws Exception {
        AadhaarVerifyRequest request = new AadhaarVerifyRequest();
        request.setAadhaarNumber("1234567890123");

        mockMvc.perform(post("/aadhaar/verify")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }
    @Test
    void verify_emptyAadhaar_shouldReturnInvalid() throws Exception {
        AadhaarVerifyRequest request = new AadhaarVerifyRequest();
        request.setAadhaarNumber("");

        mockMvc.perform(post("/aadhaar/verify")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }
}