package com.bank.loan.eligibility_service.Validator;

import com.bank.loan.eligibility_service.entity.StudentDetails;
import com.bank.loan.eligibility_service.enums.Nationality;
import com.bank.loan.eligibility_service.exception.BusinessException;

import java.util.Optional;

public class StudentValidator {
    private static final String PAN_REGEX = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$";

    public void validate(StudentDetails student) {

        Optional.ofNullable(student)
                .orElseThrow(() -> new BusinessException("Student details cannot be null"));

        validateAge(student);
        validatePan(student);
        validateAadhaar(student);
        validateNationality(student);
    }

    private void validateAge(StudentDetails student) {

        Optional.ofNullable(student.getAge())
                .filter(age -> age >= 18 && age <= 35)
                .orElseThrow(() ->
                        new BusinessException("Student age must be between 18 and 35"));
    }

    private void validatePan(StudentDetails student) {

        Optional.ofNullable(student.getPanNumber())
                .filter(pan -> !pan.isBlank())
                .filter(pan -> pan.matches(PAN_REGEX))
                .orElseThrow(() ->
                        new BusinessException("Invalid PAN number format"));
    }

    private void validateAadhaar(StudentDetails student) {

        Optional.ofNullable(student.getAadhaarNumber())
                .filter(aadhaar -> !aadhaar.isBlank())
                .orElseThrow(() ->
                        new BusinessException("Aadhaar number is required"));
    }

    private void validateNationality(StudentDetails student) {

        Optional.ofNullable(student.getNationality())
                .orElseThrow(() ->
                        new BusinessException("Nationality is required"));
    }

    private void validateMobile(StudentDetails student) {

        String mobile = student.getStudentMobile();

        if (mobile == null || mobile.isBlank()) {
            throw new BusinessException("Mobile number is required");
        }

        if (student.getNationality() == Nationality.INDIAN) {

            if (!mobile.matches("^[6-9][0-9]{9}$")) {
                throw new BusinessException("Invalid Indian mobile number");
            }

        } else {

            if (!mobile.matches("^\\+?[1-9][0-9]{7,14}$")) {
                throw new BusinessException("Invalid international mobile number");
            }
        }
    }
}