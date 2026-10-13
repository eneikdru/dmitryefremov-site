package com.dmitryefremov.site.telemetry.controller;

import com.dmitryefremov.site.telemetry.dto.AgentMetricsDto;
import com.dmitryefremov.site.telemetry.dto.TelemetryIngestionRequestDto;
import com.dmitryefremov.site.telemetry.service.AgentTelemetryService;
import com.dmitryefremov.site.telemetry.service.PlatformTelemetryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/telemetry")
public class TelemetryController {

    private final AgentTelemetryService agentTelemetryService;
    private final PlatformTelemetryService platformTelemetryService;

    public TelemetryController(AgentTelemetryService agentTelemetryService,
                               PlatformTelemetryService platformTelemetryService) {
        this.agentTelemetryService = agentTelemetryService;
        this.platformTelemetryService = platformTelemetryService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<AgentMetricsDto> getMetrics() {
        return ResponseEntity.ok(agentTelemetryService.getMetrics());
    }

    @PostMapping("/events")
    public ResponseEntity<Void> ingestEvent(@RequestBody TelemetryIngestionRequestDto request) {
        if (request.getEventType() == null || request.getValue() == null) {
            return ResponseEntity.badRequest().build();
        }
        platformTelemetryService.ingestEvent(request);
        return ResponseEntity.accepted().build();
    }
}
