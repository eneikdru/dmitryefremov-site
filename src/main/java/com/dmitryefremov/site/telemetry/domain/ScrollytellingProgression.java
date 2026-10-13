package com.dmitryefremov.site.telemetry.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "scrollytelling_progression")
public class ScrollytellingProgression {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_id", nullable = false, unique = true)
    private String resourceId;

    @Column(name = "max_depth", nullable = false)
    private double maxDepth;

    @Column(name = "total_depth", nullable = false)
    private double totalDepth;

    @Column(name = "event_count", nullable = false)
    private long eventCount;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public ScrollytellingProgression() {
    }

    public ScrollytellingProgression(String resourceId, double maxDepth, double totalDepth, long eventCount, OffsetDateTime updatedAt) {
        this.resourceId = resourceId;
        this.maxDepth = maxDepth;
        this.totalDepth = totalDepth;
        this.eventCount = eventCount;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public double getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(double maxDepth) {
        this.maxDepth = maxDepth;
    }

    public double getTotalDepth() {
        return totalDepth;
    }

    public void setTotalDepth(double totalDepth) {
        this.totalDepth = totalDepth;
    }

    public long getEventCount() {
        return eventCount;
    }

    public void setEventCount(long eventCount) {
        this.eventCount = eventCount;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
