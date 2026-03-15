package com.bank.loan.eligibility_service.service;

import com.bank.loan.eligibility_service.dto.EligibilityRequestDTO;
import com.bank.loan.eligibility_service.dto.EligibilityResponseDTO;
import com.bank.loan.eligibility_service.entity.CoApplicantDetails;
import com.bank.loan.eligibility_service.entity.EducationDetails;
import com.bank.loan.eligibility_service.entity.FinancialDetails;
import com.bank.loan.eligibility_service.entity.StudentDetails;
import com.bank.loan.eligibility_service.enums.CollegeCategory;
import com.bank.loan.eligibility_service.enums.Nationality;
import com.bank.loan.eligibility_service.repository.LoanEligibilityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EligibilityServiceTest {

    @Mock
    private LoanEligibilityRepository repository;

    @InjectMocks
    private EligiblityServiceImpl eligibilityService;

    @Test
    void checkEligibility_shouldReturnApprovedLoan() {

        StudentDetails student = StudentDetails.builder()
                .age(22)
                .panNumber("ABCDE1234F")
                .aadhaarNumber("123456789012")
                .nationality(Nationality.INDIAN)
                .build();

        EducationDetails education = EducationDetails.builder()
                .courseName("Engineering")
                .courseDurationMonths(48)
                .collegeName("Raisoni")
                .universityName("RTMNU")
                .admissionConfirmed(true)
                .expectedGraduationYear(2028)
                .academicPercentage(70.0)
                .admissionReferenceNumber("ADM12345")
                .collegeCategory(CollegeCategory.PRIVATE)
                .build();


        FinancialDetails financial = FinancialDetails.builder()
                .annualIncome(600000.0)
                .existingEMI(10000.0)
                .courseFees(500000.0)
                .requestedLoanAmount(400000.0)
                .creditScore(750)
                .build();

        CoApplicantDetails coApplicant = CoApplicantDetails.builder()
                .coApplicationPresent(true)
                .coApplicantName("Ramesh")
                .coApplicantRelation("Father")
                .coApplicantIncome(400000.0)
                .coApplicantCreditScore(720)
                .build();

        EligibilityRequestDTO request = new EligibilityRequestDTO();
        request.setStudentDetails(student);
        request.setEducationDetails(education);
        request.setFinancialDetails(financial);
        request.setCoApplicantDetails(coApplicant);

        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        EligibilityResponseDTO response = eligibilityService.checkEligibility(request);

        assertNotNull(response);
        assertEquals("Loan Approved", response.getMessage());
    }

    @Test
    void checkEligibility_shouldRejectLoan_whenCreditScoreLow() {
        StudentDetails student = StudentDetails.builder()
                .age(22)
                .studentName("Komal")
                .panNumber("ABCDE1234F")
                .aadhaarNumber("123456789012")
                .nationality(Nationality.INDIAN)
                .build();

        EducationDetails education = EducationDetails.builder()
                .courseName("Engineering")
                .courseDurationMonths(48)
                .collegeName("Raisoni")
                .universityName("RTMNU")
                .admissionConfirmed(true)
                .expectedGraduationYear(Year.now().getValue() + 4)
                .academicPercentage(70.0)
                .admissionReferenceNumber("ADM12345")
                .collegeCategory(CollegeCategory.PRIVATE)
                .build();

        FinancialDetails financial = FinancialDetails.builder()
                .annualIncome(600000.0)
                .existingEMI(10000.0)
                .courseFees(500000.0)
                .requestedLoanAmount(400000.0)
                .creditScore(500) // Low credit score
                .build();


        EligibilityRequestDTO request = new EligibilityRequestDTO();
        request.setStudentDetails(student);
        request.setEducationDetails(education);
        request.setFinancialDetails(financial);

        assertThrows(RuntimeException.class, () -> {
            eligibilityService.checkEligibility(request);
        });

    }

    @Test
    void checkEligibility_shouldRejectLoan_whenFOIRHigh() {

        StudentDetails student = StudentDetails.builder()
                .age(22)
                .studentName("Komal")
                .panNumber("ABCDE1234F")
                .aadhaarNumber("123456789012")
                .nationality(Nationality.INDIAN)
                .build();

        EducationDetails education = EducationDetails.builder()
                .courseName("Engineering")
                .courseDurationMonths(48)
                .collegeName("Raisoni")
                .universityName("RTMNU")
                .admissionConfirmed(true)
                .expectedGraduationYear(Year.now().getValue() + 4)
                .academicPercentage(70.0)
                .admissionReferenceNumber("ADM12345")
                .collegeCategory(CollegeCategory.PRIVATE)
                .build();

        FinancialDetails financial = FinancialDetails.builder()
                .annualIncome(200000.0)
                .existingEMI(50000.0) // High EMI
                .courseFees(500000.0)
                .requestedLoanAmount(400000.0)
                .creditScore(750)
                .build();

        CoApplicantDetails coApplicant = CoApplicantDetails.builder()
                .coApplicationPresent(false)
                .build();

        EligibilityRequestDTO request = new EligibilityRequestDTO();
        request.setStudentDetails(student);
        request.setEducationDetails(education);
        request.setFinancialDetails(financial);
        request.setCoApplicantDetails(coApplicant);

        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        EligibilityResponseDTO response = eligibilityService.checkEligibility(request);

        assertNotNull(response);
      //  assertFalse(response.isEligible());
        assertEquals("Loan Rejected", response.getMessage());
    }
}