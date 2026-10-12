package com.dmitryefremov.site.agent.controller;

import com.dmitryefremov.site.agent.dto.*;
import com.dmitryefremov.site.agent.service.AgentInteractionService;
import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.OffsetDateTime;

@RestController
@RequestMapping
public class AgentInteractionController {

    private final AgentInteractionService agentInteractionService;
    private Clock clock = Clock.systemUTC();

    public AgentInteractionController(AgentInteractionService agentInteractionService) {
        this.agentInteractionService = agentInteractionService;
    }

    @Autowired(required = false)
    public void setClock(Clock clock) {
        if (clock != null) {
            this.clock = clock;
        }
    }

    @PostMapping("/api/v1/agent/orders")
    public ResponseEntity<AgentOrderResponse> submitOrder(@RequestBody AgentOrderRequest request) {
        AgentOrderResponse response = agentInteractionService.submitOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/api/v1/agent/messages")
    public ResponseEntity<AgentMessageResponse> submitMessage(@RequestBody AgentMessageRequest request) {
        AgentMessageResponse response = agentInteractionService.submitMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/agent/telemetry")
    public ResponseEntity<AgentTelemetryDto> getTelemetry() {
        AgentTelemetryDto telemetry = agentInteractionService.getTelemetry();
        return ResponseEntity.ok(telemetry);
    }

    @GetMapping("/api/v1/telemetry/agent")
    public ResponseEntity<AgentTelemetryDto> getTelemetryAlias() {
        AgentTelemetryDto telemetry = agentInteractionService.getTelemetry();
        return ResponseEntity.ok(telemetry);
    }

    @GetMapping(value = {"/api/v1/agent/llms.txt", "/llms.txt"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getLlmInstructions() {
        String manifest = """
                # Dmitry Efremov Site - AI Agent API Specification

                This API allows autonomous AI agents (ChatGPT, Claude, personal assistants) to order services and submit direct messages.

                ## Endpoints:
                - POST /api/v1/agent/orders
                  Body: {"clientName": "string", "clientEmail": "string", "serviceType": "MVP|AUDIT|AUTOMATION", "requirements": "string"}
                  Description: Submit a service order request.

                - POST /api/v1/agent/messages
                  Body: {"senderName": "string", "senderEmail": "string", "message": "string"}
                  Description: Send a direct message.

                - GET /api/v1/agent/telemetry
                  Description: Retrieve interaction volume and lead tracking metrics.
                """;
        return ResponseEntity.ok(manifest);
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
