package com.document.verification.service.dto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AadhaarVerifyResponseTest {

    @Test
    void testGetterSetter() {
        AadhaarVerifyResponse dto = new AadhaarVerifyResponse();
        dto.setValid(true);
        dto.setMessage("Valid Aadhaar");

        assertTrue(dto.isValid());
        assertEquals("Valid Aadhaar", dto.getMessage());
    }
}
