package com.dmitryefremov.site.telemetry.controller;

import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import com.dmitryefremov.site.telemetry.domain.TelemetryEvent;
import com.dmitryefremov.site.telemetry.dto.*;
import com.dmitryefremov.site.telemetry.service.AgentTelemetryService;
import com.dmitryefremov.site.telemetry.service.TelemetryIngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/telemetry")
public class TelemetryController {

    private final AgentTelemetryService agentTelemetryService;
    private final TelemetryIngestionService telemetryIngestionService;
    private Clock clock = Clock.systemUTC();

    public TelemetryController(AgentTelemetryService agentTelemetryService,
                               TelemetryIngestionService telemetryIngestionService) {
        this.agentTelemetryService = agentTelemetryService;
        this.telemetryIngestionService = telemetryIngestionService;
    }

    @Autowired(required = false)
    public void setClock(Clock clock) {
        if (clock != null) {
            this.clock = clock;
        }
    }

    @GetMapping("/metrics")
    public ResponseEntity<AgentMetricsDto> getMetrics() {
        return ResponseEntity.ok(agentTelemetryService.getMetrics());
    }

    @PostMapping("/ingest")
    public ResponseEntity<TelemetryIngestionResponse> ingestTelemetry(@RequestBody TelemetryIngestionRequest request) {
        TelemetryIngestionResponse response = telemetryIngestionService.ingestEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/scrollytelling")
    public ResponseEntity<ScrollytellingProgressionDto> ingestScrollytelling(@RequestBody ScrollytellingIngestionRequest request) {
        ScrollytellingProgressionDto response = telemetryIngestionService.processScrollytellingDepth(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/events")
    public ResponseEntity<List<TelemetryEvent>> getEvents() {
        return ResponseEntity.ok(telemetryIngestionService.getAllEvents());
    }

    @GetMapping("/scrollytelling")
    public ResponseEntity<List<ScrollytellingProgressionDto>> getScrollytellingProgressions() {
        return ResponseEntity.ok(telemetryIngestionService.getAllScrollytellingProgressions());
    }

    @GetMapping("/scrollytelling/{resourceId}")
    public ResponseEntity<ScrollytellingProgressionDto> getScrollytellingProgression(@PathVariable String resourceId) {
        return ResponseEntity.ok(telemetryIngestionService.getScrollytellingProgression(resourceId));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                OffsetDateTime.now(clock)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
