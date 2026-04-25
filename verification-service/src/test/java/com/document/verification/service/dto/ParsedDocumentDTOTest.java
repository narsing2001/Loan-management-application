package com.document.verification.service.dto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParsedDocumentDTOTest {

    @Test
    void testGetterSetter() {
        ParsedDocumentDTO dto = new ParsedDocumentDTO();
        dto.setName("John");
        dto.setDob("01-01-2000");
        dto.setDocumentNumber("ABC123");

        assertEquals("John", dto.getName());
        assertEquals("01-01-2000", dto.getDob());
        assertEquals("ABC123", dto.getDocumentNumber());
    }

    @Test
    void testAllArgsConstructor() {
        ParsedDocumentDTO dto = new ParsedDocumentDTO("John", "01-01-2000", "ABC123");

        assertEquals("John", dto.getName());
        assertEquals("01-01-2000", dto.getDob());
        assertEquals("ABC123", dto.getDocumentNumber());
    }

    @Test
    void testBuilder() {
        ParsedDocumentDTO dto = ParsedDocumentDTO.builder()
                .name("John")
                .dob("01-01-2000")
                .documentNumber("ABC123")
                .build();

        assertEquals("John", dto.getName());
    }
}
