package com.bank.loan.eligibility_service.loanEligibilityStrategy;

import com.bank.loan.eligibility_service.entity.CoApplicantDetails;
import com.bank.loan.eligibility_service.entity.EducationDetails;
import com.bank.loan.eligibility_service.entity.FinancialDetails;
import com.bank.loan.eligibility_service.entity.StudentDetails;
import com.bank.loan.eligibility_service.enums.CourseType;
import com.bank.loan.eligibility_service.enums.Nationality;
import com.bank.loan.eligibility_service.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DomesticLoanStrategy implements  LoanEligibilityStrategy{

    private static final double HIGH_LOAN = 25_00_000;
    private static final double MEDIUM_LOAN = 7_00_000;

    @Override
    public void validate(StudentDetails student, EducationDetails education, FinancialDetails financial, CoApplicantDetails coApplicant) {
        double loan = Optional.ofNullable(financial.getRequestedLoanAmount())
                .orElseThrow(() -> new BusinessException("Loan amount required"));

        double YearlyIncome = Optional.ofNullable(coApplicant.getCoApplicantIncome())
                .orElse(0.0);

        double monthlyIncome = YearlyIncome / 12;
        if (loan > YearlyIncome){
            throw  new BusinessException("Co-applicant income must be greater than loan");
        }
        int coScore = Optional.ofNullable(coApplicant.getCoApplicantCreditScore())
                .orElse(0);

        Optional.of(student.getNationality())
                .filter(n -> n == Nationality.INDIAN)
                .orElseThrow(() -> new BusinessException("Domestic loan only for Indian"));

        Optional.ofNullable(education.getAdmissionConfirmed())
                .filter(Boolean::booleanValue)
                .orElseThrow(() -> new BusinessException("Admission required"));

        Optional.ofNullable(coApplicant.getCoApplicationPresent())
                .filter(Boolean::booleanValue)
                .orElseThrow(() -> new BusinessException("Co-applicant required"));


        Optional.of(loan)
                .filter(l -> l >= HIGH_LOAN && monthlyIncome < 40000)
                .ifPresent(l -> { throw new BusinessException("Min 40k income required"); });

        Optional.of(loan)
                .filter(l -> l > MEDIUM_LOAN && l < HIGH_LOAN && monthlyIncome < 25000)
                .ifPresent(l -> { throw new BusinessException("Min 25k income required"); });

        Optional.of(coScore)
                .filter(score -> score >= 600 && score <= 900)
                .orElseThrow(() -> new BusinessException("Credit score must be 600-900"));

        Optional.of(loan)
                .filter(l -> l >= HIGH_LOAN)
                .ifPresent(l -> {
                    Optional.ofNullable(financial.getCreditScore())
                            .filter(score -> score >= 700)
                            .orElseThrow(() -> new BusinessException("750+ required"));

                    Optional.ofNullable(education.getAcademicPercentage())
                            .filter(p -> p >= 50)
                            .orElseThrow(() -> new BusinessException("Min above 50%"));

                    Optional.ofNullable(education.getCourseType())
                            .filter(ct -> ct == CourseType.STEM)
                            .orElseThrow(() -> new BusinessException("Only STEM allowed"));
                });

    }
}
