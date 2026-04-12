package com.example.LoanTypeStrategyInterest.enums.enum2;

import lombok.Getter;

@Getter
public enum BankType {
    SBI("State Bank of India", "SBI", true),
    HDFC("HDFC Bank", "HDFC", false),
    ICICI("ICICI Bank", "ICICI", false),
    AXIS("Axis Bank", "AXIS", false),
    PNB("Punjab National Bank", "PNB", true),
    BOB("Bank of Baroda", "BOB", true),
    CANARA("Canara Bank", "CANARA", true),
    UNION("Union Bank of India", "UNION", true),
    KOTAK("Kotak Mahindra Bank", "KOTAK", false),
    IDBI("IDBI Bank", "IDBI", true);

    private final String displayName;
    private final String code;
    private final boolean governmentBank;

    BankType(String displayName, String code, boolean governmentBank) {
        this.displayName = displayName;
        this.code = code;
        this.governmentBank = governmentBank;
    }


}