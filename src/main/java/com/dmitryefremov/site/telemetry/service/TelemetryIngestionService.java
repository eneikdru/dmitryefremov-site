package com.dmitryefremov.site.telemetry.service;

import com.dmitryefremov.site.telemetry.domain.ScrollytellingProgression;
import com.dmitryefremov.site.telemetry.domain.TelemetryEvent;
import com.dmitryefremov.site.telemetry.dto.*;
import com.dmitryefremov.site.telemetry.repository.ScrollytellingProgressionRepository;
import com.dmitryefremov.site.telemetry.repository.TelemetryEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class TelemetryIngestionService {

    private final TelemetryEventRepository telemetryEventRepository;
    private final ScrollytellingProgressionRepository scrollytellingProgressionRepository;
    private Clock clock = Clock.systemUTC();

    public TelemetryIngestionService(TelemetryEventRepository telemetryEventRepository,
                                     ScrollytellingProgressionRepository scrollytellingProgressionRepository) {
        this.telemetryEventRepository = telemetryEventRepository;
        this.scrollytellingProgressionRepository = scrollytellingProgressionRepository;
    }

    @Autowired(required = false)
    public void setClock(Clock clock) {
        if (clock != null) {
            this.clock = clock;
        }
    }

    @Transactional
    public TelemetryIngestionResponse ingestEvent(TelemetryIngestionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Telemetry payload must not be null");
        }

        String eventType = (request.getEventType() == null || request.getEventType().isBlank())
                ? "A2A_CONSUMPTION"
                : request.getEventType();

        double metricValue = request.getMetricValue() != null ? request.getMetricValue() : 1.0;
        OffsetDateTime now = OffsetDateTime.now(clock);

        TelemetryEvent event = new TelemetryEvent(
                eventType,
                request.getSource(),
                request.getResourceId(),
                metricValue,
                request.getPayload(),
                now
        );

        TelemetryEvent savedEvent = telemetryEventRepository.save(event);

        if ("SCROLLYTELLING_DEPTH".equalsIgnoreCase(eventType) || eventType.toUpperCase().contains("SCROLLYTELLING")) {
            String resId = (request.getResourceId() != null && !request.getResourceId().isBlank())
                    ? request.getResourceId()
                    : "default";
            processScrollytellingProgression(resId, metricValue, now);
        }

        return new TelemetryIngestionResponse(
                savedEvent.getId(),
                savedEvent.getEventType(),
                savedEvent.getSource(),
                savedEvent.getResourceId(),
                savedEvent.getMetricValue(),
                "PROCESSED",
                savedEvent.getCreatedAt()
        );
    }

    @Transactional
    public ScrollytellingProgressionDto processScrollytellingDepth(ScrollytellingIngestionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Scrollytelling payload must not be null");
        }

        String resId = (request.getResourceId() != null && !request.getResourceId().isBlank())
                ? request.getResourceId()
                : "default";

        OffsetDateTime now = OffsetDateTime.now(clock);

        // 1. Persist timeseries telemetry event
        TelemetryEvent event = new TelemetryEvent(
                "SCROLLYTELLING_DEPTH",
                request.getSource(),
                resId,
                request.getDepth(),
                null,
                now
        );
        telemetryEventRepository.save(event);

        // 2. Increment progression metric atomically
        ScrollytellingProgression progression = processScrollytellingProgression(resId, request.getDepth(), now);

        return mapToProgressionDto(progression);
    }

    private ScrollytellingProgression processScrollytellingProgression(String resourceId, double depth, OffsetDateTime now) {
        int updatedRows = scrollytellingProgressionRepository.incrementProgressionAtomic(resourceId, depth, now);

        if (updatedRows == 0) {
            try {
                ScrollytellingProgression newProgression = new ScrollytellingProgression(
                        resourceId,
                        depth,
                        depth,
                        1,
                        now
                );
                return scrollytellingProgressionRepository.save(newProgression);
            } catch (DataIntegrityViolationException ex) {
                scrollytellingProgressionRepository.incrementProgressionAtomic(resourceId, depth, now);
            }
        }

        return scrollytellingProgressionRepository.findByResourceId(resourceId)
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve scrollytelling progression for resourceId: " + resourceId));
    }

    @Transactional(readOnly = true)
    public List<TelemetryEvent> getAllEvents() {
        return telemetryEventRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<ScrollytellingProgressionDto> getAllScrollytellingProgressions() {
        return scrollytellingProgressionRepository.findAll().stream()
                .map(this::mapToProgressionDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ScrollytellingProgressionDto getScrollytellingProgression(String resourceId) {
        return scrollytellingProgressionRepository.findByResourceId(resourceId)
                .map(this::mapToProgressionDto)
                .orElseThrow(() -> new IllegalArgumentException("Progression metric not found for resourceId: " + resourceId));
    }

    private ScrollytellingProgressionDto mapToProgressionDto(ScrollytellingProgression progression) {
        return new ScrollytellingProgressionDto(
                progression.getId(),
                progression.getResourceId(),
                progression.getMaxDepth(),
                progression.getTotalDepth(),
                progression.getEventCount(),
                progression.getUpdatedAt()
        );
    }
}
