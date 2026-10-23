package com.dmitryefremov.site.telemetry.dto;

import java.util.Objects;

public class TelemetryIngestRequestDto {

    private String eventType;
    private String payload;
    private String source;

    public TelemetryIngestRequestDto() {
    }

    public TelemetryIngestRequestDto(String eventType, String payload, String source) {
        this.eventType = eventType;
        this.payload = payload;
        this.source = source;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TelemetryIngestRequestDto that = (TelemetryIngestRequestDto) o;
        return Objects.equals(eventType, that.eventType) &&
                Objects.equals(payload, that.payload) &&
                Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, payload, source);
    }
}
