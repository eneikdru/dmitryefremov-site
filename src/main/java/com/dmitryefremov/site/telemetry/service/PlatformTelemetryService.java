package com.dmitryefremov.site.telemetry.service;

import com.dmitryefremov.site.telemetry.domain.PlatformTelemetryEvent;
import com.dmitryefremov.site.telemetry.domain.PlatformTelemetryMetric;
import com.dmitryefremov.site.telemetry.dto.TelemetryIngestionRequestDto;
import com.dmitryefremov.site.telemetry.repository.PlatformTelemetryEventRepository;
import com.dmitryefremov.site.telemetry.repository.PlatformTelemetryMetricRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;

@Service
public class PlatformTelemetryService {

    private static final Set<String> PROGRESSION_METRICS = Set.of("A2A_CONSUMPTION", "SCROLLYTELLING_DEPTH");

    private final PlatformTelemetryEventRepository eventRepository;
    private final PlatformTelemetryMetricRepository metricRepository;

    public PlatformTelemetryService(PlatformTelemetryEventRepository eventRepository,
                                    PlatformTelemetryMetricRepository metricRepository) {
        this.eventRepository = eventRepository;
        this.metricRepository = metricRepository;
    }

    @Transactional
    public void ingestEvent(TelemetryIngestionRequestDto request) {
        // 1. Save to timeseries datastore
        PlatformTelemetryEvent event = new PlatformTelemetryEvent(
                request.getEventType(),
                request.getValue(),
                request.getMetadata()
        );
        eventRepository.save(event);

        // 2. Increment progression metric if applicable
        if (request.getEventType() != null && PROGRESSION_METRICS.contains(request.getEventType())) {
            OffsetDateTime now = OffsetDateTime.now();
            int rowsUpdated = metricRepository.incrementMetric(request.getEventType(), request.getValue(), now);
            if (rowsUpdated == 0) {
                // If it wasn't pre-seeded, insert it
                PlatformTelemetryMetric newMetric = new PlatformTelemetryMetric(request.getEventType(), request.getValue(), now);
                metricRepository.save(newMetric);
            }
        }
    }
}
