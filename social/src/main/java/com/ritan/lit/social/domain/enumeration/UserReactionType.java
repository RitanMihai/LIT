package com.ritan.lit.social.domain.enumeration;

/**
 * The UserReactionType enumeration.
 */
public enum UserReactionType {
    LIT("lit"),
    LOVE("love"),
    AMUSING("amusing"),
    AMAZING("amazing"),
    ANGER("anger"),
    FEAR("fear"),
    SAD("sad"),
    BORING("boring");

    private final String value;

    UserReactionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
