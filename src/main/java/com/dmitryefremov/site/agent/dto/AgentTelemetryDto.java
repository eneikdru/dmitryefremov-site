package com.dmitryefremov.site.agent.dto;

import java.time.OffsetDateTime;

public class AgentTelemetryDto {
    private long totalOrders;
    private long totalMessages;
    private long totalInteractions;
    private OffsetDateTime reportedAt;

    public AgentTelemetryDto() {
    }

    public AgentTelemetryDto(long totalOrders, long totalMessages, long totalInteractions, OffsetDateTime reportedAt) {
        this.totalOrders = totalOrders;
        this.totalMessages = totalMessages;
        this.totalInteractions = totalInteractions;
        this.reportedAt = reportedAt;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(long totalMessages) {
        this.totalMessages = totalMessages;
    }

    public long getTotalInteractions() {
        return totalInteractions;
    }

    public void setTotalInteractions(long totalInteractions) {
        this.totalInteractions = totalInteractions;
    }

    public OffsetDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(OffsetDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }
}
