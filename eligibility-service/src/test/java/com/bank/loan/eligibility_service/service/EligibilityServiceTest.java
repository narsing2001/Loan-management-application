package com.bank.loan.eligibility_service.service;

import com.bank.loan.eligibility_service.Calculator.FOIRCalculator;
import com.bank.loan.eligibility_service.Calculator.LTVCalculator;
import com.bank.loan.eligibility_service.Validator.CoApplicantValidator;
import com.bank.loan.eligibility_service.Validator.EducationValidator;
import com.bank.loan.eligibility_service.Validator.FinancialValidator;
import com.bank.loan.eligibility_service.Validator.StudentValidator;
import com.bank.loan.eligibility_service.dto.EligibilityRequestDTO;
import com.bank.loan.eligibility_service.dto.EligibilityResponseDTO;
import com.bank.loan.eligibility_service.entity.*;
import com.bank.loan.eligibility_service.enums.CollegeCategory;
import com.bank.loan.eligibility_service.enums.Nationality;
import com.bank.loan.eligibility_service.enums.RiskCategory;
import com.bank.loan.eligibility_service.nationalityStrategy.NationalityStrategyFactory;
import com.bank.loan.eligibility_service.repository.LoanEligibilityRepository;
import com.bank.loan.eligibility_service.strategy.RiskStrategyFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class EligibilityServiceImplTest {

    @InjectMocks
    private EligiblityServiceImpl service;

    @Mock
    private LoanEligibilityRepository repository;

    @Mock
    private StudentValidator studentValidator;

    @Mock
    private EducationValidator educationValidator;

    @Mock
    private FinancialValidator financialValidator;

    @Mock
    private CoApplicantValidator coApplicantValidator;

    @Mock
    private FOIRCalculator foirCalculator;

    @Mock
    private LTVCalculator ltvCalculator;

    @Mock
    private RiskStrategyFactory riskFactory;

    @Mock
    private NationalityStrategyFactory nationalityFactory;

    @Mock
    private NationalityEligibilityStrategy nationalityStrategy;

    private EligibilityRequestDTO getValidRequest() {

        StudentDetails student = StudentDetails.builder()
                .age(25)
                .studentName("Komal")
                .studentEmail("test@gmail.com")
                .studentMobile("9876543210")
                .panNumber("ABCDE1234F")
                .aadhaarNumber("123456789012")
                .nationality(Nationality.INDIAN)
                .build();

        EducationDetails education = EducationDetails.builder()
                .courseName("BTech")
                .courseDurationMonths(48)
                .collegeName("ABC College")
                .universityName("XYZ University")
                .admissionConfirmed(true)
                .expectedGraduationYear(2028)
                .academicPercentage(75.0)
                .admissionReferenceNumber("ABC12345")
                .collegeCategory(CollegeCategory.TIER_1)
                .build();

        FinancialDetails financial = FinancialDetails.builder()
                .annualIncome(600000.0)
                .existingEMI(5000.0)
                .courseFees(500000.0)
                .requestedLoanAmount(300000.0)
                .creditScore(780)
                .build();

        EligibilityRequestDTO request = new EligibilityRequestDTO();
        request.setStudentDetails(student);
        request.setEducationDetails(education);
        request.setFinancialDetails(financial);

        return request;
    }

    @Test
    void shouldReturnEligibleForLowRisk() {

        EligibilityRequestDTO request = getValidRequest();

        when(foirCalculator.calculate(any())).thenReturn(30.0);
        when(ltvCalculator.calculate(any())).thenReturn(70.0);
        when(riskFactory.evaluate(any(), anyDouble(), any()))
                .thenReturn(RiskCategory.LOW);

        when(nationalityFactory.getStrategy(any()))
                .thenReturn(nationalityStrategy);

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        EligibilityResponseDTO response = service.checkEligibility(request);

        assertTrue(response.getEligible());
        assertEquals("LOW", response.getRiskCategory());
    }

    @Test
    void shouldReturnEligibleForMediumRisk() {

        EligibilityRequestDTO request = getValidRequest();

        when(foirCalculator.calculate(any())).thenReturn(45.0);
        when(ltvCalculator.calculate(any())).thenReturn(85.0);
        when(riskFactory.evaluate(any(), anyDouble(), any()))
                .thenReturn(RiskCategory.MEDIUM);

        when(nationalityFactory.getStrategy(any()))
                .thenReturn(nationalityStrategy);

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        EligibilityResponseDTO response = service.checkEligibility(request);

        assertTrue(response.getEligible());
        assertEquals("MEDIUM", response.getRiskCategory());
    }

    @Test
    void shouldReturnEligibleForHighRisk() {

        EligibilityRequestDTO request = getValidRequest();

        when(foirCalculator.calculate(any())).thenReturn(55.0);
        when(ltvCalculator.calculate(any())).thenReturn(90.0);
        when(riskFactory.evaluate(any(), anyDouble(), any()))
                .thenReturn(RiskCategory.HIGH);

        when(nationalityFactory.getStrategy(any()))
                .thenReturn(nationalityStrategy);

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        EligibilityResponseDTO response = service.checkEligibility(request);

        assertTrue(response.getEligible());
    }

    @Test
    void shouldRejectLoanWhenFoirHigh() {

        EligibilityRequestDTO request = getValidRequest();

        when(foirCalculator.calculate(any())).thenReturn(70.0);
        when(ltvCalculator.calculate(any())).thenReturn(70.0);
        when(riskFactory.evaluate(any(), anyDouble(), any()))
                .thenReturn(RiskCategory.LOW);

        when(nationalityFactory.getStrategy(any()))
                .thenReturn(nationalityStrategy);

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        EligibilityResponseDTO response = service.checkEligibility(request);

        assertFalse(response.getEligible());
        assertEquals("Loan Rejected", response.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStudentInvalid() {

        EligibilityRequestDTO request = getValidRequest();

        doThrow(new RuntimeException("Invalid student"))
                .when(studentValidator).validate(any());

        assertThrows(RuntimeException.class,
                () -> service.checkEligibility(request));
    }

    @Test
    void shouldThrowExceptionForNriWithoutCoApplicant() {

        EligibilityRequestDTO request = getValidRequest();
        request.getStudentDetails().setNationality(Nationality.NRI);

        when(foirCalculator.calculate(any())).thenReturn(30.0);
        when(ltvCalculator.calculate(any())).thenReturn(70.0);
        when(riskFactory.evaluate(any(), anyDouble(), any()))
                .thenReturn(RiskCategory.LOW);

        when(nationalityFactory.getStrategy(any()))
                .thenReturn(nationalityStrategy);

        doThrow(new RuntimeException("Co-applicant required"))
                .when(nationalityStrategy)
                .validate(any(), any(), any());

        assertThrows(RuntimeException.class,
                () -> service.checkEligibility(request));
    }

    @Test
    void shouldAddCoApplicantIncome() {

        EligibilityRequestDTO request = getValidRequest();

        CoApplicantDetails coApplicant = CoApplicantDetails.builder()
                .coApplicationPresent(true)
                .coApplicantIncome(400000.0)
                .coApplicantCreditScore(750)
                .build();

        request.setCoApplicantDetails(coApplicant);

        when(foirCalculator.calculate(any())).thenReturn(20.0);
        when(ltvCalculator.calculate(any())).thenReturn(60.0);
        when(riskFactory.evaluate(any(), anyDouble(), any()))
                .thenReturn(RiskCategory.LOW);

        when(nationalityFactory.getStrategy(any()))
                .thenReturn(nationalityStrategy);

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        EligibilityResponseDTO response = service.checkEligibility(request);

        assertTrue(response.getEligible());
    }

    @Test
    void shouldReturnAllRecords() {

        when(repository.findAll()).thenReturn(List.of(new LoanEligibility()));

        List<LoanEligibility> list = service.getAllEligibility();

        assertEquals(1, list.size());
    }

    @Test
    void shouldReturnRecordById() {

        LoanEligibility entity = new LoanEligibility();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        LoanEligibility result = service.getEligibilityById(1L);

        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenRecordNotFound() {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.getEligibilityById(1L));
    }

    @Test
    void shouldDeleteRecord() {

        doNothing().when(repository).deleteById(1L);

        service.deleteEligibility(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}