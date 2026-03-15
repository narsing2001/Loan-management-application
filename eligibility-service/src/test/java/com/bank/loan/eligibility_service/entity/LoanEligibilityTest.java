package com.bank.loan.eligibility_service.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LoanEligibilityTest {

    @Test
    void testNOArgsConstructor(){
        LoanEligibility loan = new LoanEligibility();
        assertNotNull(loan);

    }

    @Test
    void testAllArgsConstructor(){
        LocalDateTime now = LocalDateTime.now();

        LoanEligibility loan = new LoanEligibility(
                1L,
                null,
                null,
                null,
                null,
                null,
                now,
                now,
                "SYSTEM"

        );

        assertEquals(1L, loan.getId());
        assertEquals("SYSTEM" ,loan.getCreatedBy());
        assertEquals(now,loan.getCreatedAt());
        assertEquals(now,loan.getUpdatedAt());

    }

    @Test
    void testBuilder(){
        LocalDateTime now = LocalDateTime.now();

        LoanEligibility loan = LoanEligibility.builder()
                .id(10L)
                .createdAt(now)
                .createdBy("ADMIN")
                .updatedAt(now)
                .build();

        assertNotNull(loan);
        assertEquals(10L,loan.getId());
        assertEquals("ADMIN",loan.getCreatedBy());
    }

    @Test
    void testSettersAndGetters(){

        LoanEligibility loan = new LoanEligibility();
        LocalDateTime now = LocalDateTime.now();
        loan.setId(5L);
        loan.setCreatedBy("SYSTEM");
        loan.setCreatedAt(now);
        loan.setUpdatedAt(now);

   assertEquals(5L,loan.getId());
   assertEquals("SYSTEM",loan.getCreatedBy());
   assertEquals(now,loan.getCreatedAt());
   assertEquals(now,loan.getUpdatedAt());

    }

    @Test
    void testEmbeddedObjectsAssignment(){
        LoanEligibility loan = new LoanEligibility();

        StudentDetails student = new StudentDetails();
        EducationDetails education = new EducationDetails();
        loan.setStudentDetails(student);
        loan.setEducationDetails(education);

         assertEquals(student,loan.getStudentDetails());
         assertEquals(education,loan.getEducationDetails());
  }

}
