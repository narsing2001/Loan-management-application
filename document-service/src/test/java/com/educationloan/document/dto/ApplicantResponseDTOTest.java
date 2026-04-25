package com.educationloan.document.dto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApplicantResponseDTOTest {
    @Test
    void testBuilder() {
        ApplicantResponseDTO dto = ApplicantResponseDTO.builder()
                .id(1L)
                .name("Parveen")
                .dob("01-01-2000")
                .aadhaarNumber("123456789012")
                .build();
        assertEquals(1L, dto.getId());
        assertEquals("Parveen", dto.getName());
        assertEquals("01-01-2000", dto.getDob());
    }
}