package com.dmitryefremov.site.telemetry.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "platform_telemetry_events")
public class PlatformTelemetryEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "metric_value", nullable = false)
    private Double metricValue;

    @Column(name = "event_metadata")
    private String eventMetadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public PlatformTelemetryEvent() {}

    public PlatformTelemetryEvent(String eventType, Double metricValue, String eventMetadata) {
        this.eventType = eventType;
        this.metricValue = metricValue;
        this.eventMetadata = eventMetadata;
        this.createdAt = OffsetDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    public Long getId() { return id; }
    public String getEventType() { return eventType; }
    public Double getMetricValue() { return metricValue; }
    public String getEventMetadata() { return eventMetadata; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
