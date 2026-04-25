package com.educationloan.document.entity;
import com.educationloan.document.enumConst.ApplicantType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicantEntityTest {
    @Test
    void testSettersAndGetters() {
        ApplicantEntity entity = new ApplicantEntity();
        LoanEntity loan = new LoanEntity();
        loan.setId(10L);

        entity.setId(1L);
        entity.setLoan(loan);
        entity.setApplicantType(ApplicantType.STUDENT);
        entity.setName("Parveen");
        entity.setEmail("test@gmail.com");
        entity.setMobile("9999999999");
        entity.setAddress("Pune");
        entity.setDob("01-01-2000");
        entity.setAadhaarNumber("123456789012");

        assertEquals(1L, entity.getId());
        assertEquals(loan, entity.getLoan());
        assertEquals(ApplicantType.STUDENT, entity.getApplicantType());
        assertEquals("Parveen", entity.getName());
        assertEquals("test@gmail.com", entity.getEmail());
        assertEquals("9999999999", entity.getMobile());
        assertEquals("Pune", entity.getAddress());
        assertEquals("01-01-2000", entity.getDob());
        assertEquals("123456789012", entity.getAadhaarNumber());
    }

    @Test
    void testNullValues() {
        ApplicantEntity entity = new ApplicantEntity();
        entity.setName(null);
        entity.setEmail(null);
        entity.setAadhaarNumber(null);

        assertNull(entity.getName());
        assertNull(entity.getEmail());
        assertNull(entity.getAadhaarNumber());
    }
}