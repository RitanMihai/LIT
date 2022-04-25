package com.ritan.lit.watchlist.domain.enumeration;

/**
 * The StockType enumeration.
 */
public enum StockType {
    COMMON("common"),
    PREFERRED("preferred");

    private final String value;

    StockType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
