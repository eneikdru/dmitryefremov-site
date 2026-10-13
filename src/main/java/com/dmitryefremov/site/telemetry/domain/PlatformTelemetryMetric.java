package com.dmitryefremov.site.telemetry.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "platform_telemetry_metrics")
public class PlatformTelemetryMetric {
    @Id
    @Column(name = "metric_type", nullable = false)
    private String metricType;

    @Column(name = "counter", nullable = false)
    private Double counter;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public PlatformTelemetryMetric() {}

    public PlatformTelemetryMetric(String metricType, Double counter, OffsetDateTime updatedAt) {
        this.metricType = metricType;
        this.counter = counter;
        this.updatedAt = updatedAt;
    }

    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }
    public Double getCounter() { return counter; }
    public void setCounter(Double counter) { this.counter = counter; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
