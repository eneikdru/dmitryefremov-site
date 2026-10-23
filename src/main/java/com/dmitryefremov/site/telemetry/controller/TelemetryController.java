package com.dmitryefremov.site.telemetry.controller;

import com.dmitryefremov.site.telemetry.dto.AgentMetricsDto;
import com.dmitryefremov.site.telemetry.dto.TelemetryIngestRequestDto;
import com.dmitryefremov.site.telemetry.dto.TelemetryLogDto;
import com.dmitryefremov.site.telemetry.service.AgentTelemetryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/telemetry")
public class TelemetryController {

    private final AgentTelemetryService telemetryService;

    public TelemetryController(AgentTelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @PostMapping
    public ResponseEntity<?> ingestTelemetry(@RequestBody TelemetryIngestRequestDto request) {
        if (request == null || request.getEventType() == null || request.getEventType().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Event type cannot be empty"));
        }

        TelemetryLogDto result = telemetryService.ingestTelemetry(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/metrics")
    public ResponseEntity<AgentMetricsDto> getMetrics() {
        return ResponseEntity.ok(telemetryService.getMetrics());
    }

    private static class ErrorResponse {
        private final int status;
        private final String message;

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}
