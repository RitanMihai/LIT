package com.ritan.lit.portfolio.domain.enumeration;

/**
 * The OrderType enumeration.
 */
public enum OrderType {
    BUY("buy"),
    SELL("sell");

    private final String value;

    OrderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
