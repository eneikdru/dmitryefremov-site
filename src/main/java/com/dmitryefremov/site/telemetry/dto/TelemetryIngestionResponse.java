package com.dmitryefremov.site.telemetry.dto;

import java.time.OffsetDateTime;

public class TelemetryIngestionResponse {

    private Long id;
    private String eventType;
    private String source;
    private String resourceId;
    private double metricValue;
    private String status;
    private OffsetDateTime createdAt;

    public TelemetryIngestionResponse() {
    }

    public TelemetryIngestionResponse(Long id, String eventType, String source, String resourceId, double metricValue, String status, OffsetDateTime createdAt) {
        this.id = id;
        this.eventType = eventType;
        this.source = source;
        this.resourceId = resourceId;
        this.metricValue = metricValue;
        this.status = status;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(double metricValue) {
        this.metricValue = metricValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
