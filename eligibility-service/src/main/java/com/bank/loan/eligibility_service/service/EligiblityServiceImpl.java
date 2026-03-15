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
import com.bank.loan.eligibility_service.enums.RiskCategory;

import com.bank.loan.eligibility_service.nationalityStrategy.NationalityEligibilityStrategy;
import com.bank.loan.eligibility_service.nationalityStrategy.NationalityStrategyFactory;
import com.bank.loan.eligibility_service.repository.LoanEligibilityRepository;
import com.bank.loan.eligibility_service.strategy.RiskStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EligiblityServiceImpl implements EligibilityService {

    private static final double FOIR_LIMIT = 50;
    private static final double LTV_LIMIT = 90;
    private static final double MAX_LOAN_PERCENTAGE = 0.9;

    private static final String CREATED_BY_SYSTEM = "SYSTEM";
    private static final String APPROVED_MESSAGE = "Loan Approved";
    private static final String REJECTED_MESSAGE = "Loan Rejected";

    private final LoanEligibilityRepository repository;

    private final StudentValidator studentValidator = new StudentValidator();
    private final EducationValidator educationValidator = new EducationValidator();
    private final FinancialValidator financialValidator = new FinancialValidator();
    private final CoApplicantValidator coApplicantValidator = new CoApplicantValidator();

    private final FOIRCalculator foirCalculator = new FOIRCalculator();
    private final LTVCalculator ltvCalculator = new LTVCalculator();

    private final RiskStrategyFactory riskFactory = new RiskStrategyFactory();

    private final NationalityStrategyFactory nationalityFactory =
            new NationalityStrategyFactory();
    @Override
    public EligibilityResponseDTO checkEligibility(EligibilityRequestDTO request) {

        StudentDetails student = request.getStudentDetails();
        EducationDetails education = request.getEducationDetails();
        FinancialDetails financial = request.getFinancialDetails();
        CoApplicantDetails coApplicant = request.getCoApplicantDetails();

        studentValidator.validate(student);
        educationValidator.validate(education);
        financialValidator.validate(financial);
        coApplicantValidator.validate(coApplicant);

        double totalIncome = financial.getAnnualIncome();
        if (coApplicant != null && Boolean.TRUE.equals(coApplicant.getCoApplicationPresent())){
            totalIncome +=coApplicant.getCoApplicantIncome();
        }

             FinancialDetails updatedFinancial = FinancialDetails.builder()
                       .annualIncome(totalIncome)
                       .existingEMI(financial.getExistingEMI())
                       .courseFees(financial.getCourseFees())
                       .requestedLoanAmount(financial.getRequestedLoanAmount())
                       .creditScore(financial.getCreditScore())
                       .build();

        double foir = foirCalculator.calculate(updatedFinancial);
        double ltv = ltvCalculator.calculate(updatedFinancial);

        double maxEligibleAmount = financial.getCourseFees() * MAX_LOAN_PERCENTAGE;


        RiskCategory risk = riskFactory.evaluate(financial, foir);
       financial.setFoir(foir);
       financial.setLtvRatio(ltv);
       financial.setMaxEligibleAmount(maxEligibleAmount);

        NationalityEligibilityStrategy nationalityStrategy =
                nationalityFactory.getStrategy(student.getNationality());

        nationalityStrategy.validate(student, financial, coApplicant);

        boolean eligible =
                risk != RiskCategory.HIGH &&
                        foir <= FOIR_LIMIT &&
                        ltv <= LTV_LIMIT;

        LoanEligibility entity = LoanEligibility.builder()
                .studentDetails(student)
                .educationDetails(education)
                .financialDetails(financial)
                .coApplicantDetails(coApplicant)
                .decisionDetails(
                        DecisionDetails.builder()
                                .eligible(eligible)
                                .riskCategory(risk)
                                .rejectionReason(eligible ? null : "FOIR/LTV exceeded")
                                .decisionDate(LocalDate.now())
                                .build()
                )
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .createdBy(CREATED_BY_SYSTEM)
                .build();

        LoanEligibility saved = repository.save(entity);

        return EligibilityResponseDTO.builder()
                .eligible(eligible)
                .riskCategory(risk.name())
                .foir(foir)
                .ltvRatio(ltv)
                .maxEligibleAmount(financial.getCourseFees() * MAX_LOAN_PERCENTAGE)
                .message(eligible ? APPROVED_MESSAGE : REJECTED_MESSAGE)
                .build();
    }

    @Override
    public List<LoanEligibility> getAllEligibility() {
        return repository.findAll();
    }

    public LoanEligibility getEligibilityById(Long id){
        return repository.findById(id).orElseThrow(()->new RuntimeException("record not found"));
    }

    public void deleteEligibility(Long id){
        repository.deleteById(id);
    }

}