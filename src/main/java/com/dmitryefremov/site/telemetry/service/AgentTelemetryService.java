package com.dmitryefremov.site.telemetry.service;

import com.dmitryefremov.site.admin.domain.TelemetryLog;
import com.dmitryefremov.site.admin.repository.TelemetryLogRepository;
import com.dmitryefremov.site.telemetry.dto.AgentMetricsDto;
import com.dmitryefremov.site.telemetry.dto.TelemetryIngestRequestDto;
import com.dmitryefremov.site.telemetry.dto.TelemetryLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.LongAdder;

@Service
public class AgentTelemetryService {

    private final TelemetryLogRepository telemetryLogRepository;
    private final Clock clock;

    private final LongAdder totalAgentUpdates = new LongAdder();
    private final LongAdder totalApiRequests = new LongAdder();
    private final LongAdder failedApiRequests = new LongAdder();

    @Autowired
    public AgentTelemetryService(TelemetryLogRepository telemetryLogRepository,
                                 @Autowired(required = false) Clock clock) {
        this.telemetryLogRepository = telemetryLogRepository;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    public AgentTelemetryService() {
        this(null, Clock.systemUTC());
    }

    public AgentTelemetryService(TelemetryLogRepository telemetryLogRepository) {
        this(telemetryLogRepository, Clock.systemUTC());
    }

    @Transactional
    public TelemetryLogDto ingestTelemetry(TelemetryIngestRequestDto request) {
        if (request == null || request.getEventType() == null || request.getEventType().isBlank()) {
            throw new IllegalArgumentException("Event type cannot be empty");
        }

        TelemetryLog log = new TelemetryLog(
                request.getEventType(),
                request.getPayload(),
                request.getSource()
        );
        log.setCreatedAt(OffsetDateTime.now(clock));

        if (telemetryLogRepository != null) {
            log = telemetryLogRepository.save(log);
        }

        return new TelemetryLogDto(
                log.getId(),
                log.getEventType(),
                log.getPayload(),
                log.getSource(),
                log.getCreatedAt()
        );
    }

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
