package com.bank.loan.eligibility_service.Validator;

import com.bank.loan.eligibility_service.entity.StudentDetails;
import com.bank.loan.eligibility_service.enums.Nationality;
import com.bank.loan.eligibility_service.exception.BusinessException;
import com.bank.loan.eligibility_service.exception.InvalidAgeException;
import com.bank.loan.eligibility_service.exception.InvalidPhoneNumber;
import com.bank.loan.eligibility_service.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class
StudentValidator {

    private static final String PAN_REGEX = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public void validate(StudentDetails student) {

        Optional.ofNullable(student)
                .orElseThrow(() -> new BusinessException("Student details cannot be null"));

        validateNationality(student);
        validateAge(student);
        validateName(student);
        validateEmail(student);
        validatePan(student);
        validateAadhaar(student);
        validateMobile(student.getStudentMobile(), student.getNationality());
    }


    private void validateName(StudentDetails student) {
        Optional.ofNullable(student.getStudentName())
                .filter(name -> !name.isBlank())
                .filter(name -> name.length() >= 3)
                .orElseThrow(() ->
                        new ValidationException("Valid student name is required"));
    }


    private void validateAge(StudentDetails student) {
        Optional.ofNullable(student.getAge())
                .filter(age -> age >= 18 && age <= 35)
                .orElseThrow(() ->
                        new InvalidAgeException("Student age must be between 16 and 35"));
    }


    private void validateNationality(StudentDetails student) {
        Optional.ofNullable(student.getNationality())
                .orElseThrow(() ->
                        new ValidationException("nationality field should be required"));
    }


    private void validatePan(StudentDetails student) {
        Optional.ofNullable(student.getPanNumber())
                .filter(pan -> !pan.isBlank())
                .filter(pan -> pan.matches(PAN_REGEX))
                .orElseThrow(() ->
                        new ValidationException("Invalid PAN number format"));
    }


    private void validateAadhaar(StudentDetails student) {
        Optional.ofNullable(student.getAadhaarNumber())
                .filter(aadhar -> !aadhar.isBlank())
                .filter(aadhar -> aadhar.matches("^\\d{12}$"))
                .orElseThrow(() ->
                        new ValidationException("Valid Aadhaar is required"));
    }


    private void validateEmail(StudentDetails student) {
        Optional.ofNullable(student.getStudentEmail())
                .filter(email -> !email.isBlank())
                .filter(email -> email.matches(EMAIL_REGEX))
                .orElseThrow(() ->
                        new ValidationException("Invalid email format"));
    }


    private void validateMobile(String mobile, Nationality nationality) {

        Optional.ofNullable(mobile)
                .filter(m -> !m.isBlank())
                .orElseThrow(() ->
                        new InvalidPhoneNumber("Mobile number is required"));

        Optional.ofNullable(nationality)
                .ifPresent(n -> {

                    if (n == Nationality.INDIAN &&
                            !mobile.matches("^[6-9][0-9]{9}$")) {
                        throw new InvalidPhoneNumber("Invalid Indian mobile number");
                    }

                    if (n == Nationality.FOREIGN &&
                            !mobile.matches("^\\+[1-9][0-9]{7,14}$")) {
                        throw new InvalidPhoneNumber("Invalid international mobile number");
                    }
                });
    }
}