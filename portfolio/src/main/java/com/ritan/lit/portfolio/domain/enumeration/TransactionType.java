package com.ritan.lit.portfolio.domain.enumeration;

/**
 * The TransactionType enumeration.
 */
public enum TransactionType {
    DEPOSIT("deposit"),
    WITHDRAW("withdraw");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
