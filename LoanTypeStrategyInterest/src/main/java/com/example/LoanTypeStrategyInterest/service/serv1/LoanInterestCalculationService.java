package com.example.LoanTypeStrategyInterest.service.serv1;






import com.example.LoanTypeStrategyInterest.dto.dto1.InterestRateCalculationRequestDTO;
import com.example.LoanTypeStrategyInterest.dto.dto1.InterestRateCalculationResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class LoanInterestCalculationService {

    private final InterestRateCalculationService interestRateCalculationService;

    public LoanInterestCalculationService(InterestRateCalculationService interestRateCalculationService) {
        this.interestRateCalculationService = interestRateCalculationService;
    }

    /**
     * Compute loan interest rate directly from request DTO
     */
    public InterestRateCalculationResponseDTO computeLoanInterestRate(InterestRateCalculationRequestDTO requestDTO) {
        InterestRateCalculationResponseDTO response = interestRateCalculationService.calculateInterestRate(requestDTO);
        response.setCalculationMethod("Computed using LoanInterestCalculationService | Method: Newton-Raphson");
        return response;
    }

    /**
     * Compute loan interest rate using Binary Search
     */
    public InterestRateCalculationResponseDTO computeLoanInterestRateBinary(InterestRateCalculationRequestDTO requestDTO) {
        InterestRateCalculationResponseDTO response = interestRateCalculationService.calculateInterestRateBinarySearch(requestDTO);
        response.setCalculationMethod("Computed using LoanInterestCalculationService | Method: Binary Search");
        return response;
    }
}

































/*
import com.example.LoanTypeStrategyInterest.dto.dto1.InterestRateCalculationRequestDTO;
import com.example.LoanTypeStrategyInterest.dto.dto1.InterestRateCalculationResponseDTO;
import com.example.LoanTypeStrategyInterest.dto.dto1.UserApplicationRequestDTO;
import com.example.LoanTypeStrategyInterest.dto.dto1.UserApplicationResponseDTO;
import com.example.LoanTypeStrategyInterest.factory.StrategyFactory;
import com.example.LoanTypeStrategyInterest.strategy.InterestStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class LoanInterestCalculationService {
    private final StrategyFactory strategyFactory;
    private final InterestRateCalculationService interestRateCalculationService;
    private final UserApplicationDetailService userApplicationDetailService;

    public LoanInterestCalculationService(StrategyFactory strategyFactory, InterestRateCalculationService interestRateCalculationService, UserApplicationDetailService userApplicationDetailService) {
        this.strategyFactory = strategyFactory;
        this.interestRateCalculationService = interestRateCalculationService;
        this.userApplicationDetailService = userApplicationDetailService;
    }

    public InterestRateCalculationResponseDTO computeLoanInterestRate(UserApplicationRequestDTO userApplicationRequestDTO) {
        InterestStrategy strategy = strategyFactory.getStrategy(userApplicationRequestDTO.getLoanType());
        BigDecimal rate = strategy.findInterestRate(userApplicationRequestDTO);
        UserApplicationResponseDTO userApplicationResponseDTO=userApplicationDetailService.applyLoan(userApplicationRequestDTO);

        InterestRateCalculationRequestDTO emiRequest = InterestRateCalculationRequestDTO.builder()
                .approvedAmount(strategy.getMaxLoanAmount(userApplicationRequestDTO))
                .monthlyEmi(userApplicationResponseDTO.getMonthlyEmi())
                .tenureYears(strategy.getMaxTenureYears(userApplicationRequestDTO.getTenureYears()))
                .build();

        InterestRateCalculationResponseDTO emiResponse = interestRateCalculationService.calculateInterestRate(emiRequest);
        emiResponse.setCalculationMethod("LoanType: " + strategy.getSupportedLoanType() + " | Rate applied: " + rate + "%");
        return emiResponse;
    }
}
 */