package com.dmitryefremov.site.telemetry.service;

import com.dmitryefremov.site.telemetry.dto.AgentMetricsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class AgentTelemetryServiceTest {

    private AgentTelemetryService telemetryService;

    @BeforeEach
    void setUp() {
        telemetryService = new AgentTelemetryService();
    }

    @Test
    void initialMetricsShouldBeZero() {
        AgentMetricsDto metrics = telemetryService.getMetrics();
        assertThat(metrics.getTotalAgentUpdates()).isZero();
        assertThat(metrics.getTotalApiRequests()).isZero();
        assertThat(metrics.getFailedApiRequests()).isZero();
        assertThat(metrics.getFailureRate()).isEqualTo(0.0);
    }

    @Test
    void incrementCountersAndCalculateFailureRateCorrectly() {
        telemetryService.incrementApiRequests();
        telemetryService.incrementApiRequests();
        telemetryService.incrementApiRequests();
        telemetryService.incrementApiRequests();

        telemetryService.incrementFailedApiRequests();
        telemetryService.incrementAgentUpdates();

        AgentMetricsDto metrics = telemetryService.getMetrics();
        assertThat(metrics.getTotalApiRequests()).isEqualTo(4);
        assertThat(metrics.getFailedApiRequests()).isEqualTo(1);
        assertThat(metrics.getTotalAgentUpdates()).isEqualTo(1);
        assertThat(metrics.getFailureRate()).isCloseTo(0.25, within(0.0001));
    }

    @Test
    void resetShouldClearAllCounters() {
        telemetryService.incrementApiRequests();
        telemetryService.incrementFailedApiRequests();
        telemetryService.incrementAgentUpdates();

        telemetryService.reset();

        AgentMetricsDto metrics = telemetryService.getMetrics();
        assertThat(metrics.getTotalAgentUpdates()).isZero();
        assertThat(metrics.getTotalApiRequests()).isZero();
        assertThat(metrics.getFailedApiRequests()).isZero();
        assertThat(metrics.getFailureRate()).isEqualTo(0.0);
    }
}
