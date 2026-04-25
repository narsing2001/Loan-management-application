package com.bank.loan.eligibility_service.validators;

import com.bank.loan.eligibility_service.Validator.StudentValidator;
import com.bank.loan.eligibility_service.entity.StudentDetails;
import com.bank.loan.eligibility_service.enums.Nationality;
import com.bank.loan.eligibility_service.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentValidatorTest {

    private StudentValidator validator;

    @BeforeEach
    void setup() {
        validator = new StudentValidator();
    }

    private StudentDetails validStudent() {
        return StudentDetails.builder()
                .age(22)
                .studentName("Komal")
                .studentEmail("test@gmail.com")
                .studentMobile("9876543210")
                .panNumber("ABCDE1234F")
                .aadhaarNumber("123456789012")
                .nationality(Nationality.INDIAN)
                .build();
    }

    @Test
    void shouldPassForValidStudent() {
        assertDoesNotThrow(() -> validator.validate(validStudent()));
    }

    @Test
    void shouldThrowWhenAgeInvalid() {
        StudentDetails s = validStudent();
        s.setAge(10);
        assertThrows(InvalidAgeException.class, () -> validator.validate(s));
    }

    @Test
    void shouldThrowWhenPanInvalid() {
        StudentDetails s = validStudent();
        s.setPanNumber("123");
        assertThrows(ValidationException.class, () -> validator.validate(s));
    }

    @Test
    void shouldThrowWhenEmailInvalid() {
        StudentDetails s = validStudent();
        s.setStudentEmail("wrong");
        assertThrows(ValidationException.class, () -> validator.validate(s));
    }

    @Test
    void shouldThrowWhenMobileInvalid() {
        StudentDetails s = validStudent();
        s.setStudentMobile("123");
        assertThrows(InvalidPhoneNumber.class, () -> validator.validate(s));
    }

    @Test
    void shouldThrowWhenAadhaarInvalidForIndian() {
        StudentDetails s = validStudent();
        s.setAadhaarNumber("123");
        assertThrows(ValidationException.class, () -> validator.validate(s));
    }
}