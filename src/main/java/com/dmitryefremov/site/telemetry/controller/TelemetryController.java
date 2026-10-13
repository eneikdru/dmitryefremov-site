package com.dmitryefremov.site.telemetry.controller;

import com.dmitryefremov.site.telemetry.dto.AgentMetricsDto;
import com.dmitryefremov.site.telemetry.service.AgentTelemetryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/telemetry")
public class TelemetryController {

    private final AgentTelemetryService telemetryService;

    public TelemetryController(AgentTelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @PostMapping("/events")
    public ResponseEntity<Void> receiveEvent(@RequestBody Map<String, Object> eventData) {
        // Here we could pass the event to a service.
        // For now, we return 200 OK as a minimal stub for integration.
        return ResponseEntity.ok().build();
    }


    @GetMapping("/metrics")
    public ResponseEntity<AgentMetricsDto> getMetrics() {
        return ResponseEntity.ok(telemetryService.getMetrics());
    }
}
