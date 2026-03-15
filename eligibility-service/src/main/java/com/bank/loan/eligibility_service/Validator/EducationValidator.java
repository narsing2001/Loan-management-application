package com.bank.loan.eligibility_service.Validator;

import com.bank.loan.eligibility_service.entity.EducationDetails;
import com.bank.loan.eligibility_service.exception.BusinessException;

import java.time.Year;
import java.util.Optional;

public class EducationValidator {

    public void validate(EducationDetails education) {

        Optional.ofNullable(education)
                .orElseThrow(() ->
                        new BusinessException("Education details cannot be null"));

        validateCourseName(education);
        validateCourseDuration(education);
        validateCollegeName(education);
        validateUniversityName(education);
        validateAdmissionConfirmed(education);
        validateGraduationYear(education);
        validateAcademicPercentage(education);
        validateAdmissionReference(education);
        validateCollegeCategory(education);
    }

    private void validateCourseName(EducationDetails education) {

        Optional.ofNullable(education.getCourseName())
                .filter(name -> !name.isBlank())
                .orElseThrow(() ->
                        new BusinessException("Course name is required"));
    }

    private void validateCourseDuration(EducationDetails education) {

        Optional.ofNullable(education.getCourseDurationMonths())
                .filter(duration -> duration > 0)
                .orElseThrow(() ->
                        new BusinessException("Invalid course duration"));
    }

    private void validateCollegeName(EducationDetails education) {

        Optional.ofNullable(education.getCollegeName())
                .filter(name -> !name.isBlank())
                .orElseThrow(() ->
                        new BusinessException("College name is required"));
    }

    private void validateUniversityName(EducationDetails education) {

        Optional.ofNullable(education.getUniversityName())
                .filter(name -> !name.isBlank())
                .orElseThrow(() ->
                        new BusinessException("University name is required"));
    }

    private void validateAdmissionConfirmed(EducationDetails education) {

        Optional.ofNullable(education.getAdmissionConfirmed())
                .filter(Boolean::booleanValue)
                .orElseThrow(() ->
                        new BusinessException("Admission must be confirmed"));
    }

    private void validateGraduationYear(EducationDetails education) {

        Optional.ofNullable(education.getExpectedGraduationYear())
                .filter(year -> year >= Year.now().getValue())
                .orElseThrow(() ->
                        new BusinessException("Invalid graduation year"));
    }

    private void validateAcademicPercentage(EducationDetails education) {

        Optional.ofNullable(education.getAcademicPercentage())
                .filter(p -> p >= 50)
                .orElseThrow(() ->
                        new BusinessException("Minimum 50% academic percentage required"));
    }

    private void validateAdmissionReference(EducationDetails education) {

        Optional.ofNullable(education.getAdmissionReferenceNumber())
                .filter(ref -> ref.matches("^[A-Za-z0-9]{6,20}$"))
                .orElseThrow(() ->
                        new BusinessException("Invalid admission reference number"));
    }

    private void validateCollegeCategory(EducationDetails education) {

        Optional.ofNullable(education.getCollegeCategory())
                .orElseThrow(() ->
                        new BusinessException("College category is required"));
    }
}