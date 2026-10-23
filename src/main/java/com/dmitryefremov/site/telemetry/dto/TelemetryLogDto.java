package com.dmitryefremov.site.telemetry.dto;

import java.time.OffsetDateTime;
import java.util.Objects;

public class TelemetryLogDto {

    private Long id;
    private String eventType;
    private String payload;
    private String source;
    private OffsetDateTime createdAt;

    public TelemetryLogDto() {
    }

    public TelemetryLogDto(Long id, String eventType, String payload, String source, OffsetDateTime createdAt) {
        this.id = id;
        this.eventType = eventType;
        this.payload = payload;
        this.source = source;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TelemetryLogDto that = (TelemetryLogDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(eventType, that.eventType) &&
                Objects.equals(payload, that.payload) &&
                Objects.equals(source, that.source) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventType, payload, source, createdAt);
    }
}
