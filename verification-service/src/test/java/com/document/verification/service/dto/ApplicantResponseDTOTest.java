package com.document.verification.service.dto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicantResponseDTOTest {

    @Test
    void testGetterSetter() {
        ApplicantResponseDTO dto = new ApplicantResponseDTO();
        dto.setId(1L);
        dto.setName("Parveen");
        dto.setDob("1999-01-01");
        dto.setAadhaarNumber("1234");

        assertEquals(1L, dto.getId());
        assertEquals("Parveen", dto.getName());
    }

    @Test
    void testAllArgsConstructor() {
        ApplicantResponseDTO dto =
                new ApplicantResponseDTO(1L, "Parveen", "1999-01-01", "1234");

        assertEquals("Parveen", dto.getName());
    }

    @Test
    void testBuilder() {
        ApplicantResponseDTO dto = ApplicantResponseDTO.builder()
                .id(1L)
                .name("Parveen")
                .build();

        assertEquals(1L, dto.getId());
    }
}