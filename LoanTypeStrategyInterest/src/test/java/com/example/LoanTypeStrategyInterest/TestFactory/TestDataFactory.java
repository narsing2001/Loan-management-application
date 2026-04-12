package com.example.LoanTypeStrategyInterest.TestFactory;

import com.example.LoanTypeStrategyInterest.dto.dto1.*;
import com.example.LoanTypeStrategyInterest.enums.enum1.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


//Utility class for creating test data objects aligned with actual DTOs-------------------------------

public class TestDataFactory {

    // Interest Rate Calculation DTOs --------------------------------------------------

    public static InterestRateCalculationRequestDTO createInterestRateRequest() {
        InterestRateCalculationRequestDTO requestDTO = new InterestRateCalculationRequestDTO();
        requestDTO.setApprovedAmount(new BigDecimal("100000"));
        requestDTO.setMonthlyEmi(new BigDecimal("5000"));
        requestDTO.setTenureYears(5);
        return requestDTO;
    }

    public static InterestRateCalculationRequestDTO createInterestRateRequest(
            BigDecimal amount, BigDecimal emi, int tenure) {
        InterestRateCalculationRequestDTO requestDTO = new InterestRateCalculationRequestDTO();
        requestDTO.setApprovedAmount(amount);
        requestDTO.setMonthlyEmi(emi);
        requestDTO.setTenureYears(tenure);
        return requestDTO;
    }

    public static InterestRateCalculationResponseDTO createInterestRateResponse() {
        InterestRateCalculationResponseDTO responseDTO = new InterestRateCalculationResponseDTO();
        responseDTO.setApprovedAmount(new BigDecimal("100000"));
        responseDTO.setMonthlyEmi(new BigDecimal("5000"));
        responseDTO.setTenureYears(5);
        responseDTO.setTotalMonths(60);
        responseDTO.setCalculatedAnnualInterestRate(new BigDecimal("10.5"));
        responseDTO.setCalculatedMonthlyInterestRate(new BigDecimal("0.875"));
        responseDTO.setTotalPayment(new BigDecimal("300000"));
        responseDTO.setTotalInterestPaid(new BigDecimal("200000"));
        responseDTO.setCalculationMethod("Newton-Raphson Method");
        return responseDTO;
    }

    public static InterestRateCalculationResponseDTO createInterestRateResponse(
            BigDecimal interestRate, String method) {
        InterestRateCalculationResponseDTO responseDTO = createInterestRateResponse();
        responseDTO.setCalculatedAnnualInterestRate(interestRate);
        responseDTO.setCalculationMethod(method);
        return responseDTO;
    }

    // User Application Request DTOs ----------------------------------------------------------

    public static UserApplicationRequestDTO createDomesticUserApplicationRequest() {
        UserApplicationRequestDTO requestDTO = new UserApplicationRequestDTO();
        requestDTO.setApplicantName("John Doe");
        requestDTO.setApplicantEmail("john.doe@example.com");
        requestDTO.setGender(Gender.MALE);
        requestDTO.setLoanType(LoanType.DOMESTIC);
        requestDTO.setCourseType(CourseType.UNDERGRADUATE);
        requestDTO.setDomesticCourseType(DomesticCourseType.ENGINEERING);
        requestDTO.setInternationalCourseType(null);
        requestDTO.setRequestedAmount(new BigDecimal("500000"));
        requestDTO.setTenureYears(5);
        requestDTO.setTuitionFeeCoverage(new BigDecimal("300000"));
        requestDTO.setAccommodationCoverage(new BigDecimal("100000"));
        requestDTO.setTravelExpenseCoverage(new BigDecimal("50000"));
        requestDTO.setLaptopExpenseCoverage(new BigDecimal("30000"));
        requestDTO.setExamFeeCoverage(new BigDecimal("10000"));
        requestDTO.setOtherExpenseCoverage(new BigDecimal("10000"));
        requestDTO.setAdmissionLetterVerified(true);
        requestDTO.setInstitutionApproved(true);
        return requestDTO;
    }

    public static UserApplicationRequestDTO createInternationalUserApplicationRequest() {
        UserApplicationRequestDTO requestDTO = new UserApplicationRequestDTO();
        requestDTO.setApplicantName("Jane Smith");
        requestDTO.setApplicantEmail("jane.smith@example.com");
        requestDTO.setGender(Gender.FEMALE);
        requestDTO.setLoanType(LoanType.INTERNATIONAL);
        requestDTO.setCourseType(CourseType.POSTGRADUATE);
        requestDTO.setInternationalCourseType(InternationalCourseType.MBA);
        requestDTO.setDomesticCourseType(null);
        requestDTO.setRequestedAmount(new BigDecimal("1500000"));
        requestDTO.setTenureYears(10);
        requestDTO.setTuitionFeeCoverage(new BigDecimal("1000000"));
        requestDTO.setAccommodationCoverage(new BigDecimal("200000"));
        requestDTO.setTravelExpenseCoverage(new BigDecimal("150000"));
        requestDTO.setLaptopExpenseCoverage(new BigDecimal("80000"));
        requestDTO.setExamFeeCoverage(new BigDecimal("40000"));
        requestDTO.setOtherExpenseCoverage(new BigDecimal("30000"));
        requestDTO.setAdmissionLetterVerified(true);
        requestDTO.setInstitutionApproved(true);
        return requestDTO;
    }

    public static UserApplicationRequestDTO createDomesticUserApplicationRequest(
            String name, String email, BigDecimal amount, DomesticCourseType courseType) {
        UserApplicationRequestDTO requestDTO = createDomesticUserApplicationRequest();
        requestDTO.setApplicantName(name);
        requestDTO.setApplicantEmail(email);
        requestDTO.setRequestedAmount(amount);
        requestDTO.setDomesticCourseType(courseType);
        return requestDTO;
    }

    public static UserApplicationRequestDTO createInternationalUserApplicationRequest(
            String name, String email, BigDecimal amount, InternationalCourseType courseType) {
        UserApplicationRequestDTO requestDTO = createInternationalUserApplicationRequest();
        requestDTO.setApplicantName(name);
        requestDTO.setApplicantEmail(email);
        requestDTO.setRequestedAmount(amount);
        requestDTO.setInternationalCourseType(courseType);
        return requestDTO;
    }

    // User Application Response DTOs ------------------------------------------------

    public static UserApplicationResponseDTO createUserApplicationResponse() {
        return UserApplicationResponseDTO.builder()
                .id(UUID.randomUUID())
                .applicantName("John Doe")
                .applicantEmail("john.doe@example.com")
                .gender(Gender.MALE)
                .loanType(LoanType.DOMESTIC)
                .courseType(CourseType.UNDERGRADUATE)
                .domesticCourseType(DomesticCourseType.ENGINEERING)
                .requestedAmount(new BigDecimal("500000"))
                .approvedAmount(new BigDecimal("450000"))
                .interestRate(new BigDecimal("10.5"))
                .tenureYears(5)
                .monthlyEmi(new BigDecimal("9688.00"))
                .tuitionFeeCoverage(new BigDecimal("300000"))
                .accommodationCoverage(new BigDecimal("100000"))
                .travelExpenseCoverage(new BigDecimal("50000"))
                .laptopExpenseCoverage(new BigDecimal("30000"))
                .examFeeCoverage(new BigDecimal("10000"))
                .otherExpenseCoverage(new BigDecimal("10000"))
                .admissionLetterVerified(true)
                .institutionApproved(true)
                .applicationStatus(ApplicationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static UserApplicationResponseDTO createUserApplicationResponse(
            UUID id, ApplicationStatus status) {
        UserApplicationResponseDTO responseDTO = createUserApplicationResponse();
        responseDTO.setId(id);
        responseDTO.setApplicationStatus(status);
        return responseDTO;
    }

    public static UserApplicationResponseDTO createUserApplicationResponse(
            UUID id, ApplicationStatus status, LoanType loanType) {
        UserApplicationResponseDTO responseDTO = createUserApplicationResponse(id, status);
        responseDTO.setLoanType(loanType);
        return responseDTO;
    }

    // Edge Case Data ------------------------------------------------------

    public static InterestRateCalculationRequestDTO createMinimumLoanRequest() {
        return createInterestRateRequest(new BigDecimal("100000"), new BigDecimal("2000"), 1);
    }

    public static InterestRateCalculationRequestDTO createMaximumLoanRequest() {
        return createInterestRateRequest(new BigDecimal("10000000"), new BigDecimal("200000"), 15);
    }

    public static UserApplicationRequestDTO createInvalidEmailRequest() {
        UserApplicationRequestDTO request = createDomesticUserApplicationRequest();
        request.setApplicantEmail("invalid-email");
        return request;
    }

    public static UserApplicationRequestDTO createBelowMinimumAmountRequest() {
        UserApplicationRequestDTO request = createDomesticUserApplicationRequest();
        request.setRequestedAmount(new BigDecimal("50000"));
        return request;
    }

    public static UserApplicationRequestDTO createExceedingTenureRequest() {
        UserApplicationRequestDTO request = createDomesticUserApplicationRequest();
        request.setTenureYears(20);
        return request;
    }

    public static UserApplicationRequestDTO createInvalidCourseSelectionRequest() {
        // DOMESTIC loan with both course types set — fails @AssertTrue validation
        UserApplicationRequestDTO request = createDomesticUserApplicationRequest();
        request.setInternationalCourseType(InternationalCourseType.MBA); // invalid combination
        return request;
    }

    //Loan Type Variations------------------------------------------------------
    public static UserApplicationRequestDTO createDomesticEngineeringLoanRequest() {
        return createDomesticUserApplicationRequest("Alice Johnson",
                "alice@example.com", new BigDecimal("400000"),
                DomesticCourseType.ENGINEERING
        );
    }

    public static UserApplicationRequestDTO createDomesticMedicalLoanRequest() {
        return createDomesticUserApplicationRequest(
                "Bob Smith",
                "bob@example.com",
                new BigDecimal("600000"),
                DomesticCourseType.MEDICAL
        );
    }

    public static UserApplicationRequestDTO createInternationalMBALoanRequest() {
        return createInternationalUserApplicationRequest(
                "Charlie Brown",
                "charlie@example.com",
                new BigDecimal("2000000"),
                InternationalCourseType.MBA
        );
    }

    public static UserApplicationRequestDTO createInternationalMSLoanRequest() {
        return createInternationalUserApplicationRequest(
                "Diana Prince", "diana@example.com",
                new BigDecimal("1800000"),
                InternationalCourseType.MEDICINE
        );
    }
}