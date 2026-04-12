package com.example.loantypecategories.factory.fact1;

import com.example.loantypecategories.constant.LoanType;
import com.example.loantypecategories.strategy.strategy1.DomesticLoanStrategy1;
import com.example.loantypecategories.strategy.strategy1.InternationalLoanStrategy1;
import com.example.loantypecategories.strategy.strategy1.LoanStrategy1;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for resolving loan strategies based on loan type
 * Uses Spring-managed strategy beans
 */
@Component
public class LoanStrategyFactory1 {

    private final Map<String, LoanStrategy1> strategies = new HashMap<>();

    public LoanStrategyFactory1(
            DomesticLoanStrategy1 domesticStrategy,
            InternationalLoanStrategy1 internationalStrategy) {

        strategies.put("DOMESTIC", domesticStrategy);
        strategies.put("INTERNATIONAL", internationalStrategy);
    }

    /**
     * Get strategy instance for the given loan type
     */
    public LoanStrategy1 getStrategy(LoanType loanType) {
        if (loanType == null) {
            throw new IllegalArgumentException("Loan type cannot be null");
        }

        LoanStrategy1 strategy = strategies.get(loanType.name());

        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No strategy found for loan type: " + loanType
            );
        }

        return strategy;
    }

    /**
     * Check if strategy exists for given loan type
     */
    public boolean hasStrategy(LoanType loanType) {
        return loanType != null && strategies.containsKey(loanType.name());
    }
}