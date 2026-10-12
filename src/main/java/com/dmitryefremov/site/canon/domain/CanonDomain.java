package com.dmitryefremov.site.canon.domain;

public enum CanonDomain {
    AUTONOMOUS_ENGINEERING("Autonomous Engineering"),
    AGENTIC_ECONOMY("Agentic Economy (A2A)"),
    PRAGMATIC_BUSINESS_STRATEGY("Pragmatic Business Strategy"),
    PHILOSOPHY_OF_MIND("Philosophy of Mind");

    private final String displayName;

    CanonDomain(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static CanonDomain fromString(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        for (CanonDomain domain : CanonDomain.values()) {
            if (domain.name().equalsIgnoreCase(text) || domain.displayName.equalsIgnoreCase(text)) {
                return domain;
            }
        }
        throw new IllegalArgumentException("Unknown Canon domain: " + text);
    }
}
