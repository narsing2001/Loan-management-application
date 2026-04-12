package com.example.LoanTypeStrategyInterest.service.serv1;


import com.example.LoanTypeStrategyInterest.dto.dto1.UserApplicationRequestDTO;
import com.example.LoanTypeStrategyInterest.dto.dto1.UserApplicationResponseDTO;
import com.example.LoanTypeStrategyInterest.dto.dto1.UserApplicationStatusResponseDTO;
import com.example.LoanTypeStrategyInterest.entity.entity1.UserApplicationDetail;
import com.example.LoanTypeStrategyInterest.enums.enum1.ApplicationStatus;
import com.example.LoanTypeStrategyInterest.enums.enum1.Gender;
import com.example.LoanTypeStrategyInterest.enums.enum1.LoanType;
import com.example.LoanTypeStrategyInterest.exception.EmailAlradyExistsException;
import com.example.LoanTypeStrategyInterest.exception.RequestAmountZeroException;
import com.example.LoanTypeStrategyInterest.factory.StrategyFactory;
import com.example.LoanTypeStrategyInterest.repository.repo1.UserApplicationRepository;
import com.example.LoanTypeStrategyInterest.strategy.InterestStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserApplicationDetailService {

    private static final BigDecimal FEMALE_DISCOUNT = new BigDecimal("0.50");
    private final UserApplicationRepository userApplicationRepository;

    public UserApplicationResponseDTO applyLoan(UserApplicationRequestDTO requestDTO) {
        Optional<UserApplicationDetail> existing = userApplicationRepository.findByApplicantEmail(requestDTO.getApplicantEmail());
        if (existing.isPresent()) {
            throw new EmailAlradyExistsException("education loan with current application email is already exists");
        }
        validateRequest(requestDTO);
        InterestStrategy interestStrategy= StrategyFactory.getStrategy(requestDTO.getLoanType());
        interestStrategy.validatePreConditions(requestDTO);
        BigDecimal interestRate=interestStrategy.findInterestRate(requestDTO);
        boolean female_discount=(requestDTO.getGender()== Gender.FEMALE);
        if(female_discount){
              interestRate=interestRate.subtract(FEMALE_DISCOUNT).setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal maxAllowed=interestStrategy.getMaxLoanAmount(requestDTO);
        BigDecimal approvedAmt=requestDTO.getRequestedAmount().min(maxAllowed).setScale(2,RoundingMode.HALF_UP);
        int tenureYears=interestStrategy.getMaxTenureYears(requestDTO.getTenureYears());
        UserApplicationDetail applicationDetail=buildEntity(requestDTO,interestRate,approvedAmt,tenureYears);
        interestStrategy.totalCoverage(requestDTO,applicationDetail);
        applicationDetail.setMonthlyEmi(calculateEmi(approvedAmt,interestRate,tenureYears));
        applicationDetail.setApplicationStatus(ApplicationStatus.APPROVED);
        applicationDetail.setCreatedAt(LocalDateTime.now());
        applicationDetail.setUpdatedAt(LocalDateTime.now());
        UserApplicationDetail savedEntity = userApplicationRepository.save(applicationDetail);
        return buildResponse(savedEntity,female_discount);
    }

    @Transactional(readOnly = true)
    public List<UserApplicationStatusResponseDTO> getByStatus(ApplicationStatus status) {
        return userApplicationRepository.findByApplicationStatus(status).stream()
                                                 .map(this::buildStatusResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<UserApplicationStatusResponseDTO>getByLoanType(LoanType loanType){
        return  userApplicationRepository.findByLoanType(loanType).stream()
                .map(this::buildStatusResponse).toList();
    }


    private UserApplicationStatusResponseDTO buildStatusResponse(UserApplicationDetail userApplicationDetail) {
        return UserApplicationStatusResponseDTO.builder()
                .applicationId(String.valueOf(userApplicationDetail.getId()))
                .applicantName(userApplicationDetail.getApplicantName())
                .status(userApplicationDetail.getApplicationStatus())
                .loanType(String.valueOf(userApplicationDetail.getLoanType()))
                .build();
    }


    private BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRatePct, int years) {
        BigDecimal r = annualRatePct.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        int n = years * 12;
        BigDecimal power = BigDecimal.ONE.add(r).pow(n, new MathContext(10));
        return principal.multiply(r).multiply(power).divide(power.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
    }
    private void validateRequest(UserApplicationRequestDTO requestDTO) {
        if (requestDTO.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RequestAmountZeroException("Requested amount must be greater than zero");
        }
    }

    private UserApplicationResponseDTO buildResponse(UserApplicationDetail entity, boolean femaleDiscount) {
        UserApplicationResponseDTO res = new UserApplicationResponseDTO();
        res.setId(entity.getId());
        res.setApplicantName(entity.getApplicantName());
        res.setApplicantEmail(entity.getApplicantEmail());
        res.setGender(entity.getGender());
        res.setLoanType(entity.getLoanType());
        res.setCourseType(entity.getCourseType());
        res.setRequestedAmount(entity.getRequestedAmount());
        res.setApprovedAmount(entity.getApprovedAmount());
        res.setInterestRate(entity.getInterestRate());
        res.setTenureYears(entity.getTenureYears());
        res.setMonthlyEmi(entity.getMonthlyEmi());
        res.setTuitionFeeCoverage(entity.getTuitionFeeCoverage());
        res.setAccommodationCoverage(entity.getAccommodationCoverage());
        res.setTravelExpenseCoverage(entity.getTravelExpenseCoverage());
        res.setLaptopExpenseCoverage(entity.getLaptopExpenseCoverage());
        res.setExamFeeCoverage(entity.getExamFeeCoverage());
        res.setOtherExpenseCoverage(entity.getOtherExpenseCoverage());
        res.setAdmissionLetterVerified(entity.isAdmissionLetterVerified());
        res.setInstitutionApproved(entity.isInstitutionApproved());
        res.setApplicationStatus(entity.getApplicationStatus());
        res.setCreatedAt(entity.getCreatedAt());
        res.setUpdatedAt(entity.getUpdatedAt());
        String message = femaleDiscount ? "Approved with special female candidate discount applied." : "Loan application approved successfully.";
        String applicantName= res.getApplicantName();
        UUID id= res.getId();
        log.info("Dear your loan application with id:{}, and  Name:{}, are {} ",id,applicantName,message);
        return res;
    }

    private UserApplicationDetail buildEntity(UserApplicationRequestDTO userApplicationRequestDTO,
                                              BigDecimal interestRate,
                                              BigDecimal approvedAmt, int tenureYears) {
        UserApplicationDetail applicationDetail = new UserApplicationDetail();
        applicationDetail.setApplicantName(userApplicationRequestDTO.getApplicantName());
        applicationDetail.setApplicantEmail(userApplicationRequestDTO.getApplicantEmail());
        applicationDetail.setGender(userApplicationRequestDTO.getGender());
        applicationDetail.setLoanType(userApplicationRequestDTO.getLoanType());
        applicationDetail.setCourseType(userApplicationRequestDTO.getCourseType());
        applicationDetail.setRequestedAmount(userApplicationRequestDTO.getRequestedAmount());
        applicationDetail.setInterestRate(interestRate);
        applicationDetail.setApprovedAmount(approvedAmt);
        applicationDetail.setTenureYears(tenureYears);


        applicationDetail.setTuitionFeeCoverage(userApplicationRequestDTO.getTuitionFeeCoverage());
        applicationDetail.setAccommodationCoverage(userApplicationRequestDTO.getAccommodationCoverage());
        applicationDetail.setTravelExpenseCoverage(userApplicationRequestDTO.getTravelExpenseCoverage());
        applicationDetail.setLaptopExpenseCoverage(userApplicationRequestDTO.getLaptopExpenseCoverage());
        applicationDetail.setExamFeeCoverage(userApplicationRequestDTO.getExamFeeCoverage());
        applicationDetail.setOtherExpenseCoverage(userApplicationRequestDTO.getOtherExpenseCoverage());


        applicationDetail.setAdmissionLetterVerified(userApplicationRequestDTO.isAdmissionLetterVerified());
        applicationDetail.setInstitutionApproved(userApplicationRequestDTO.isInstitutionApproved());
        return applicationDetail;
    }






}












