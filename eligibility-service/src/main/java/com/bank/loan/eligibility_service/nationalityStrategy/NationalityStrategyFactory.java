package com.bank.loan.eligibility_service.nationalityStrategy;

import com.bank.loan.eligibility_service.enums.Nationality;

import static com.bank.loan.eligibility_service.enums.Nationality.*;

public class NationalityStrategyFactory {

    public NationalityEligibilityStrategy getStrategy(Nationality nationality) {

        switch (nationality) {

            case INDIAN:
                return new IndianEligibilityStrategy();

            case NRI:
                return new NriEligibilityStrategy();

            case FOREIGN:
                return new ForeignEligibilityStrategy();

            default:
                return new IndianEligibilityStrategy();
        }

}}
