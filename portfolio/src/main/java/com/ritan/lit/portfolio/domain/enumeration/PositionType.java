package com.ritan.lit.portfolio.domain.enumeration;

/**
 * The PositionType enumeration.
 */
public enum PositionType {
    OPEN("open"),
    CLOSED("closed");

    private final String value;

    PositionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
