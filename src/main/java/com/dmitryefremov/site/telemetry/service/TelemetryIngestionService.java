package com.dmitryefremov.site.telemetry.service;

import com.dmitryefremov.site.telemetry.domain.TelemetryEvent;
import com.dmitryefremov.site.telemetry.dto.TelemetryEventDto;
import com.dmitryefremov.site.telemetry.repository.TelemetryEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TelemetryIngestionService {

    private final TelemetryEventRepository repository;

    public TelemetryIngestionService(TelemetryEventRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void ingestEvent(TelemetryEventDto eventDto) {
        if (eventDto == null || eventDto.getEventType() == null) {
            throw new IllegalArgumentException("Invalid telemetry event: eventType is required.");
        }

        Long value = eventDto.getMetricValue();
        if (value == null) {
            value = 1L;
        }

        TelemetryEvent event = new TelemetryEvent(eventDto.getEventType(), value);
        repository.save(event);
    }
}
