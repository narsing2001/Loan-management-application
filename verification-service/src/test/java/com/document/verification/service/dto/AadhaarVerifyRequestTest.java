package com.document.verification.service.dto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AadhaarVerifyRequestTest {

    @Test
    void testGetterSetter() {
        AadhaarVerifyRequest dto = new AadhaarVerifyRequest();
        dto.setAadhaarNumber("123456789012");
        assertEquals("123456789012", dto.getAadhaarNumber());
    }
}