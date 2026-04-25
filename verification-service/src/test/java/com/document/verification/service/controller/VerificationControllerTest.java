package com.document.verification.service.controller;
import com.document.verification.service.dto.VerificationResponseDTO;
import com.document.verification.service.globalExceptionHandling.customException.VerificationNotFoundException;
import com.document.verification.service.service.VerificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(VerificationController.class)
class VerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VerificationServiceImpl verificationService;

    @Test
    void getVerificationStatus_shouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();

        VerificationResponseDTO responseDTO = new VerificationResponseDTO();
        responseDTO.setVerificationStatus("VERIFIED");

        when(verificationService.getVerificationStatus(id))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/api/verification/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus").value("VERIFIED"));
    }
    @Test
    void getVerificationStatus_notFound_shouldReturn404() throws Exception {
        UUID id = UUID.randomUUID();

        when(verificationService.getVerificationStatus(id))
                .thenThrow(new VerificationNotFoundException("Not found"));

        mockMvc.perform(get("/api/verification/{id}", id))
                .andExpect(status().isNotFound());
    }
    @Test
    void getVerificationStatus_invalidUUID_shouldReturn400() throws Exception {
        mockMvc.perform(get("/api/verification/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest());
    }
}