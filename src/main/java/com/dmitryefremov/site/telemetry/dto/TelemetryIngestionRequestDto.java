package com.dmitryefremov.site.telemetry.dto;

public class TelemetryIngestionRequestDto {
    private String eventType;
    private Double value;
    private String metadata;

    public TelemetryIngestionRequestDto() {
    }

    public TelemetryIngestionRequestDto(String eventType, Double value, String metadata) {
        this.eventType = eventType;
        this.value = value;
        this.metadata = metadata;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
