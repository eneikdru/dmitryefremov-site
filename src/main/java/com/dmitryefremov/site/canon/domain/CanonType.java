package com.dmitryefremov.site.canon.domain;

public enum CanonType {
    MANIFESTO("Manifesto"),
    GLOSSARY_TERM("Glossary Term"),
    ONTOLOGY("Ontology");

    private final String displayName;

    CanonType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static CanonType fromString(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        for (CanonType type : CanonType.values()) {
            if (type.name().equalsIgnoreCase(text) || type.displayName.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown Canon type: " + text);
    }
}
