package com.dmitryefremov.site.telemetry.dto;

public class TelemetryIngestionRequest {

    private String eventType;
    private String source;
    private String resourceId;
    private Double metricValue;
    private String payload;

    public TelemetryIngestionRequest() {
    }

    public TelemetryIngestionRequest(String eventType, String source, String resourceId, Double metricValue, String payload) {
        this.eventType = eventType;
        this.source = source;
        this.resourceId = resourceId;
        this.metricValue = metricValue;
        this.payload = payload;
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

    public Double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
