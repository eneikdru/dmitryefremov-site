package com.dmitryefremov.site.telemetry.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "telemetry_events")
public class TelemetryEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;
    private Long metricValue;
    private OffsetDateTime createdAt;

    public TelemetryEvent() {
    }

    public TelemetryEvent(String eventType, Long metricValue) {
        this.eventType = eventType;
        this.metricValue = metricValue;
        this.createdAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
