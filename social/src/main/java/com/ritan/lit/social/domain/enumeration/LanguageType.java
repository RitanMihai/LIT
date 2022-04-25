package com.ritan.lit.social.domain.enumeration;

/**
 * The LanguageType enumeration.
 */
public enum LanguageType {
    ENG("english"),
    RO("romanian");

    private final String value;

    LanguageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
