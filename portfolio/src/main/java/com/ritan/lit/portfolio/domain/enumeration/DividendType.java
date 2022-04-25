package com.ritan.lit.portfolio.domain.enumeration;

/**
 * The DividendType enumeration.
 */
public enum DividendType {
    PROPERTY_INCOME("property income"),
    ORDINARY("ordinary");

    private final String value;

    DividendType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
