package com.bank.loan.eligibility_service.service;

import com.bank.loan.eligibility_service.dto.EligibilityRequestDTO;
import com.bank.loan.eligibility_service.dto.EligibilityResponseDTO;
import com.bank.loan.eligibility_service.entity.LoanEligibility;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EligibilityService {
    EligibilityResponseDTO checkEligibility(EligibilityRequestDTO request);


    List<LoanEligibility> getAllEligibility();

    LoanEligibility getEligibilityById(Long id);

    void deleteEligibility(Long id);


}
