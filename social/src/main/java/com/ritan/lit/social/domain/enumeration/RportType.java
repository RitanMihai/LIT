package com.ritan.lit.social.domain.enumeration;

/**
 * The RportType enumeration.
 */
public enum RportType {
    SPAM("spam"),
    NUDITY("nudity"),
    DOXING("doxing"),
    VIOLENCE("violence"),
    TERRORISM("terrorism"),
    HATE_SEPACH("hate speach"),
    CHILD_ABUSE("child abuse"),
    ANIMAL_ABUSE("animal abuse"),
    SEXUAL_CONTENT("sexual content"),
    PULBIC_SHAMING("public shaming"),
    FRAUDULENT_SCHEME("fraudulent scheme"),
    FALSE_INFORMATION("false information"),
    PROMOTE_ILLEGAL_DRUGS("promote illegal drugs"),
    OTHER("other");

    private final String value;

    RportType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
