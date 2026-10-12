package com.dmitryefremov.site.telemetry.controller;

import com.dmitryefremov.site.telemetry.dto.AgentMetricsDto;
import com.dmitryefremov.site.telemetry.service.AgentTelemetryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/telemetry")
public class TelemetryController {

    private final AgentTelemetryService telemetryService;

    public TelemetryController(AgentTelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<AgentMetricsDto> getMetrics() {
        return ResponseEntity.ok(telemetryService.getMetrics());
    }
}
