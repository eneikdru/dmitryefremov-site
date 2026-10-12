package com.dmitryefremov.site.agent.controller;

import com.dmitryefremov.site.agent.dto.*;
import com.dmitryefremov.site.agent.service.AgentInteractionService;
import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
public class AgentInteractionController {

    private final AgentInteractionService agentInteractionService;

    public AgentInteractionController(AgentInteractionService agentInteractionService) {
        this.agentInteractionService = agentInteractionService;
    }

    @PostMapping("/api/v1/agent/orders")
    public ResponseEntity<AgentOrderResponseDto> submitOrder(@RequestBody AgentOrderRequestDto request) {
        AgentOrderResponseDto response = agentInteractionService.submitOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/api/v1/agent/messages")
    public ResponseEntity<AgentMessageResponseDto> sendMessage(@RequestBody AgentMessageRequestDto request) {
        AgentMessageResponseDto response = agentInteractionService.sendMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/agent/telemetry")
    public ResponseEntity<AgentTelemetryDto> getTelemetry() {
        AgentTelemetryDto telemetry = agentInteractionService.getTelemetry();
        return ResponseEntity.ok(telemetry);
    }

    @GetMapping(value = "/llms.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getLlmsTxt() {
        String content = """
                # Dmitry Efremov - AI Agent Direct API

                Welcome to the direct API interface for external AI agents (ChatGPT, Claude, personal assistants).

                ## Capabilities
                - Order Services (MVP development, architecture audit, process automation): POST /api/v1/agent/orders
                - Send Direct Message to Dmitry Efremov: POST /api/v1/agent/messages
                - Telemetry & Usage Metrics: GET /api/v1/agent/telemetry
                - Articles Stream API: GET /api/v1/articles
                - Podcasts Stream API: GET /api/v1/podcasts

                ## Schema Definitions

                ### Submit Order
                POST /api/v1/agent/orders
                Content-Type: application/json
                {
                  "serviceType": "MVP" | "Audit" | "Automation",
                  "clientName": "String",
                  "contactEmail": "String",
                  "requirements": "String",
                  "agentName": "ChatGPT / Claude / CustomAgent"
                }

                ### Send Message
                POST /api/v1/agent/messages
                Content-Type: application/json
                {
                  "senderName": "String",
                  "contactEmail": "String",
                  "messageText": "String",
                  "agentName": "ChatGPT / Claude / CustomAgent"
                }
                """;
        return ResponseEntity.ok(content);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
