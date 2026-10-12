package com.dmitryefremov.site.canon.domain;

public enum CanonRelationshipType {
    REFERENCES,
    SUPERSEDES,
    RELATED,
    DEPENDS_ON,
    EXTENDS;

    public static CanonRelationshipType fromString(String text) {
        if (text == null || text.isBlank()) {
            return REFERENCES;
        }
        for (CanonRelationshipType rel : CanonRelationshipType.values()) {
            if (rel.name().equalsIgnoreCase(text)) {
                return rel;
            }
        }
        throw new IllegalArgumentException("Unknown Canon relationship type: " + text);
    }
}
