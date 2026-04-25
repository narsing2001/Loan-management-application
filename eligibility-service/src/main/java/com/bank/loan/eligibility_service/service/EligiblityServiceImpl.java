package com.bank.loan.eligibility_service.service;

import com.bank.loan.eligibility_service.Calculator.FOIRCalculator;
import com.bank.loan.eligibility_service.Calculator.LTVCalculator;
import com.bank.loan.eligibility_service.Validator.*;
import com.bank.loan.eligibility_service.dto.EligibilityRequestDTO;
import com.bank.loan.eligibility_service.dto.EligibilityResponseDTO;
import com.bank.loan.eligibility_service.entity.*;
import com.bank.loan.eligibility_service.enums.LoanType;
import com.bank.loan.eligibility_service.enums.RiskCategory;
import com.bank.loan.eligibility_service.exception.BusinessException;
import com.bank.loan.eligibility_service.loanEligibilityStrategy.LoanStrategyFactory;
import com.bank.loan.eligibility_service.repository.LoanEligibilityRepository;
import com.bank.loan.eligibility_service.strategy.RiskStrategyFactory;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EligiblityServiceImpl implements EligibilityService {

    private static final Logger log = LoggerFactory.getLogger(EligiblityServiceImpl.class);
    private static final double MAX_LOAN_PERCENTAGE = 0.9;

    private static final double MAX_FOIR_DOMESTIC = 50.0;
    private static final double MAX_FOIR_INTERNATIONAL = 45.0;

    private static final double MAX_LTV_DOMESTIC = 90.0;
    private static final double MAX_LTV_INTERNATIONAL = 85.0;

    private static final int MIN_CREDIT_SCORE_INTERNATIONAL = 700;

    private static final String APPROVED_MSG = "Loan Approved";
    private static final String REJECTED_MSG = "Loan Rejected due to eligibility criteria";

    private static final int MIN_CREDIT_SCORE = 650;

    private final LoanEligibilityRepository repository;

    private final StudentValidator studentValidator;
    private final EducationValidator educationValidator;
    private final FinancialValidator financialValidator;
    private final CoApplicantValidator coApplicantValidator;
    private final DocumentValidator documentValidator;

    private final FOIRCalculator foirCalculator;
    private final LTVCalculator ltvCalculator;

    private final RiskStrategyFactory riskFactory;
    private final LoanStrategyFactory loanStrategyFactory;
    //private final LoanType loanType;

   public EligiblityServiceImpl(LoanEligibilityRepository repository,
                                StudentValidator studentValidator,
                                EducationValidator educationValidator,
                                FinancialValidator financialValidator,
                                CoApplicantValidator coApplicantValidator,
                                DocumentValidator documentValidator,
                                FOIRCalculator foirCalculator,
                                LTVCalculator ltvCalculator,
                                RiskStrategyFactory riskFactory,
                                LoanStrategyFactory loanStrategyFactory) {

       this.repository = repository;
       this.studentValidator = studentValidator;
       this.educationValidator = educationValidator;
       this.financialValidator = financialValidator;
       this.coApplicantValidator = coApplicantValidator;
       this.documentValidator = documentValidator;
       this.foirCalculator = foirCalculator;
       this.ltvCalculator = ltvCalculator;
       this.riskFactory = riskFactory;
       this.loanStrategyFactory = loanStrategyFactory;
   }

    @Override
    @Transactional
    public EligibilityResponseDTO checkEligibility(EligibilityRequestDTO request) {

       log.info("starting eligibility check");

        StudentDetails student = request.getStudentDetails();
        EducationDetails education = request.getEducationDetails();
        FinancialDetails financial = request.getFinancialDetails();
        CoApplicantDetails coApplicant = request.getCoApplicantDetails();
        DocumentDetails document = request.getDocumentDetails();
        LoanType loanType = request.getLoanType();
        log.info("Loan Type: {}",loanType);
        log.info("Requested Loan Amount : {}",financial.getRequestedLoanAmount());

        studentValidator.validate(student);
        educationValidator.validate(education);
        financialValidator.validate(financial);
        coApplicantValidator.validateBasic(coApplicant);
        documentValidator.validate(document,loanType);

        Double loanAmount = Optional.ofNullable(financial.getRequestedLoanAmount())
                        .orElseThrow(()->new BusinessException("loan amount required"));


        double foir = foirCalculator.calculate(coApplicant, financial);
        double ltv = ltvCalculator.calculate(financial);

        log.info("FOIR: {}", foir);
        log.info("LTV: {}", ltv);

        double maxEligibleAmount = Optional.ofNullable(financial.getCourseFees())
                .map(fees -> fees * MAX_LOAN_PERCENTAGE)
                .orElse(0.0);

        financial.setFoir(foir);
        financial.setLtvRatio(ltv);
        financial.setMaxEligibleAmount(maxEligibleAmount);

        RiskCategory risk = riskFactory.evaluate(
                financial,
                foir,
                education.getCollegeCategory()
        );
        log.info("Risk Category: {}", risk);


        boolean strategyPassed = true;
        String rejectionReson = null;

        try {
            loanStrategyFactory.getStrategy(loanType)
                    .validate(student, education, financial, coApplicant);
            log.info("Strategy validation PASSED");
        } catch (BusinessException ex) {
            strategyPassed = false;
            rejectionReson = ex.getMessage();

            log.error("Strategy validation FAILED: {}", rejectionReson);
        }

        boolean eligible;

        if (loanType == LoanType.DOMESTIC) {

            eligible =  strategyPassed &&
                    foir <= MAX_FOIR_DOMESTIC &&
                            ltv <= MAX_LTV_DOMESTIC &&
                            Optional.ofNullable(financial.getCreditScore()).orElse(0) >= MIN_CREDIT_SCORE &&
                            risk != RiskCategory.HIGH;
        } else if (loanType == LoanType.INTERNATIONAL){

            eligible = strategyPassed &&
                    foir <= MAX_FOIR_INTERNATIONAL &&
                            ltv <= MAX_LTV_INTERNATIONAL &&
                            Optional.ofNullable(financial.getCreditScore()).orElse(0) >= MIN_CREDIT_SCORE_INTERNATIONAL &&
                            risk == RiskCategory.LOW;

            log.info("Eligibility Conditions -> strategy: {}, foir: {}, ltv: {}, creditScore: {}, risk: {}",
                    strategyPassed,
                    foir <= MAX_FOIR_INTERNATIONAL,
                    ltv <= MAX_LTV_INTERNATIONAL,
                    financial.getCreditScore(),
                    risk);// stricter rule

        } else {
            throw new BusinessException("Invalid Loan Type");
        }

        LoanEligibility entity = LoanEligibility.builder()
                .studentDetails(student)
                .educationDetails(education)
                .financialDetails(financial)
                .coApplicantDetails(coApplicant)
                .documentDetails(document)
                .decisionDetails(
                        DecisionDetails.builder()
                                .eligible(eligible)
                                .riskCategory(risk)
                                .decisionDate(LocalDate.now())
                                .rejectionReason(eligible ? null : REJECTED_MSG)
                                .build()
                )
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        repository.save(entity);

        log.info("Final Eligibility Result: {}", eligible);
        return EligibilityResponseDTO.builder()
                .eligible(eligible)
                .riskCategory(risk.name())
                .foir(foir)
                .ltvRatio(ltv)
                .maxEligibleAmount(maxEligibleAmount)
                .message(eligible ? APPROVED_MSG : REJECTED_MSG)
                .build();
    }

    @Override
    public List<LoanEligibility> getAllEligibility() {
        return repository.findAll();
    }

    @Override
    public LoanEligibility getEligibilityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("Record not found"));
    }

    @Override
    public void deleteEligibility(Long id) {
        repository.deleteById(id);
    }
}
