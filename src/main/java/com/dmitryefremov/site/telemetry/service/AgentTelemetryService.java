package com.dmitryefremov.site.telemetry.service;

import com.dmitryefremov.site.telemetry.dto.AgentMetricsDto;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.LongAdder;

@Service
public class AgentTelemetryService {

    private final LongAdder totalAgentUpdates = new LongAdder();
    private final LongAdder totalApiRequests = new LongAdder();
    private final LongAdder failedApiRequests = new LongAdder();

    public void incrementAgentUpdates() {
        totalAgentUpdates.increment();
    }

    public void incrementApiRequests() {
        totalApiRequests.increment();
    }

    public void incrementFailedApiRequests() {
        failedApiRequests.increment();
    }

    public void reset() {
        totalAgentUpdates.reset();
        totalApiRequests.reset();
        failedApiRequests.reset();
    }

    public AgentMetricsDto getMetrics() {
        long updates = totalAgentUpdates.sum();
        long requests = totalApiRequests.sum();
        long failures = failedApiRequests.sum();

        double failureRate = requests > 0 ? (double) failures / requests : 0.0;

        return new AgentMetricsDto(updates, requests, failures, failureRate);
    }
}
