package com.bank.loan.eligibility_service.loanEligibilityStrategy;

import com.bank.loan.eligibility_service.entity.CoApplicantDetails;
import com.bank.loan.eligibility_service.entity.EducationDetails;
import com.bank.loan.eligibility_service.entity.FinancialDetails;
import com.bank.loan.eligibility_service.entity.StudentDetails;
import com.bank.loan.eligibility_service.enums.Country;
import com.bank.loan.eligibility_service.enums.CourseType;
import com.bank.loan.eligibility_service.enums.Nationality;
import com.bank.loan.eligibility_service.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class InternationalLoanStrategy implements LoanEligibilityStrategy{

    private static final double HIGH_LOAN = 25_00_000;
    private static final double MEDIUM_LOAN = 7_00_000;
    private static final Set<Country> ALLOWED_COUNTRIES = Set.of(
            Country.USA, Country.CANADA, Country.UK, Country.AUSTRALIA, Country.GERMANY
    );

    @Override
    public void validate(StudentDetails student,
                         EducationDetails education,
                         FinancialDetails financial,
                         CoApplicantDetails coApplicant) {

        double loan = Optional.ofNullable(financial.getRequestedLoanAmount())
                .orElseThrow(() -> new BusinessException("Loan amount required"));


        double yearlyincome = Optional.ofNullable(coApplicant.getCoApplicantIncome())
                .orElse(0.0);

        double monthlyIncome = yearlyincome/12;

        if (loan > yearlyincome) {
            throw new BusinessException("international Loan must be less than income");
        }

        int coScore = Optional.ofNullable(coApplicant.getCoApplicantCreditScore())
                .orElse(0);

        Optional.ofNullable(education.getAdmissionConfirmed())
                .filter(Boolean::booleanValue)
                .orElseThrow(() -> new BusinessException("Admission required"));

        Optional.ofNullable(education.getCountry())
                .filter(ALLOWED_COUNTRIES::contains)
                .orElseThrow(() -> new BusinessException("Country not supported"));

        Optional.ofNullable(coApplicant.getCoApplicationPresent())
                .filter(Boolean::booleanValue)
                .orElseThrow(() -> new BusinessException("Co-applicant mandatory"));

        Optional.ofNullable(student.getNationality())
                .filter(n -> n == Nationality.FOREIGN)
                .orElseThrow(() -> new BusinessException("International loan only for foreign students"));

        Optional.of(loan)
                .filter(l -> l >= HIGH_LOAN && monthlyIncome < 60000)
                .ifPresent(l -> { throw new BusinessException("Min 60k income"); });

        Optional.of(loan)
                .filter(l -> l > MEDIUM_LOAN && l < HIGH_LOAN && monthlyIncome < 45000)
                .ifPresent(l -> { throw new BusinessException("Min 45k income"); });

        Optional.of(coScore)
                .filter(score -> score >= 700 && score <= 900)
                .orElseThrow(() -> new BusinessException("Credit score 700-900 required"));

        Optional.of(loan)
                .filter(l -> l >= HIGH_LOAN)
                .ifPresent(l -> {
                    Optional.ofNullable(financial.getCreditScore())
                            .filter(score -> score >= 750)
                            .orElseThrow(() -> new BusinessException("750+ required"));

                    Optional.ofNullable(education.getAcademicPercentage())
                            .filter(p -> p >= 60)
                            .orElseThrow(() -> new BusinessException("Min 60%"));

                    Optional.ofNullable(education.getCourseType())
                            .filter(ct -> ct == CourseType.STEM)
                            .orElseThrow(() -> new BusinessException("Only STEM allowed"));
                });
    }
}