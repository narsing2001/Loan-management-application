package com.example.LoanTypeStrategyInterest.dto.dto1;

import com.example.LoanTypeStrategyInterest.enums.enum1.RejectionReason;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Supplier;

@AllArgsConstructor
public class RejectionRule {
    private Supplier<Boolean> condition;
    @Getter
    private RejectionReason reason;

     public boolean test() {
        return condition.get();
    }

}
