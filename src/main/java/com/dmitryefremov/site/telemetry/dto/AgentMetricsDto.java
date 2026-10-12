package com.dmitryefremov.site.telemetry.dto;

public class AgentMetricsDto {
    private long totalAgentUpdates;
    private long totalApiRequests;
    private long failedApiRequests;
    private double failureRate;

    public AgentMetricsDto() {
    }

    public AgentMetricsDto(long totalAgentUpdates, long totalApiRequests, long failedApiRequests, double failureRate) {
        this.totalAgentUpdates = totalAgentUpdates;
        this.totalApiRequests = totalApiRequests;
        this.failedApiRequests = failedApiRequests;
        this.failureRate = failureRate;
    }

    public long getTotalAgentUpdates() {
        return totalAgentUpdates;
    }

    public void setTotalAgentUpdates(long totalAgentUpdates) {
        this.totalAgentUpdates = totalAgentUpdates;
    }

    public long getTotalApiRequests() {
        return totalApiRequests;
    }

    public void setTotalApiRequests(long totalApiRequests) {
        this.totalApiRequests = totalApiRequests;
    }

    public long getFailedApiRequests() {
        return failedApiRequests;
    }

    public void setFailedApiRequests(long failedApiRequests) {
        this.failedApiRequests = failedApiRequests;
    }

    public double getFailureRate() {
        return failureRate;
    }

    public void setFailureRate(double failureRate) {
        this.failureRate = failureRate;
    }
}
