package com.dmitryefremov.site.schedule.domain;

public enum PublicationPlatform {
    TELEGRAM,
    YOUTUBE,
    ARTICLE,
    PODCAST;

    public static PublicationPlatform parse(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Publication platform cannot be null or blank");
        }
        try {
            return PublicationPlatform.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown publication platform: " + value);
        }
    }
}
