package com.dmitryefremov.site.telemetry.dto;

import java.time.OffsetDateTime;

public class ScrollytellingProgressionDto {

    private Long id;
    private String resourceId;
    private double maxDepth;
    private double totalDepth;
    private long eventCount;
    private OffsetDateTime updatedAt;

    public ScrollytellingProgressionDto() {
    }

    public ScrollytellingProgressionDto(Long id, String resourceId, double maxDepth, double totalDepth, long eventCount, OffsetDateTime updatedAt) {
        this.id = id;
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
