package com.dmitryefremov.site.telemetry.controller;

import com.dmitryefremov.site.telemetry.dto.AgentMetricsDto;
import com.dmitryefremov.site.telemetry.dto.TelemetryEventDto;
import com.dmitryefremov.site.telemetry.service.AgentTelemetryService;
import com.dmitryefremov.site.telemetry.service.TelemetryIngestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/telemetry")
public class TelemetryController {

    private final AgentTelemetryService telemetryService;
    private final TelemetryIngestionService ingestionService;

    public TelemetryController(AgentTelemetryService telemetryService, TelemetryIngestionService ingestionService) {
        this.telemetryService = telemetryService;
        this.ingestionService = ingestionService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<AgentMetricsDto> getMetrics() {
        return ResponseEntity.ok(telemetryService.getMetrics());
    }

    @PostMapping("/events")
    public ResponseEntity<Void> ingestEvent(@RequestBody TelemetryEventDto eventDto) {
        try {
            ingestionService.ingestEvent(eventDto);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
