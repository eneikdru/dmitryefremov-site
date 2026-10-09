package com.dmitryefremov.site.schedule.domain;

public enum PublicationStatus {
    SCHEDULED,
    IN_PRODUCTION,
    PUBLISHED;

    public static PublicationStatus parse(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Publication status cannot be null or blank");
        }
        try {
            return PublicationStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown publication status: " + value);
        }
    }
}
