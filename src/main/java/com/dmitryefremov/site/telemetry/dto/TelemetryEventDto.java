package com.dmitryefremov.site.telemetry.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TelemetryEventDto {
    private String eventType;
    private Long metricValue;

    public TelemetryEventDto() {
    }

    public TelemetryEventDto(String eventType, Long metricValue) {
        this.eventType = eventType;
        this.metricValue = metricValue;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(Long metricValue) {
        this.metricValue = metricValue;
    }
}
