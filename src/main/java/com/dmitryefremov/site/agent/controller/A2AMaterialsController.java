package com.dmitryefremov.site.agent.controller;

import com.dmitryefremov.site.agent.domain.AgentApiToken;
import com.dmitryefremov.site.agent.service.AgentContentService;
import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class A2AMaterialsController {

    private final AgentContentService agentContentService;
    private final Clock clock;

    public A2AMaterialsController(AgentContentService agentContentService, Clock clock) {
        this.agentContentService = agentContentService;
        this.clock = clock;
    }

    @GetMapping(value = {"/api/v1/a2a/materials", "/api/v1/materials"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAuthenticMaterials(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-API-Token", required = false) String apiTokenHeader) {

        String token = apiTokenHeader != null ? apiTokenHeader : authHeader;
        Optional<AgentApiToken> agentOpt = agentContentService.authenticateToken(token);
        if (agentOpt.isEmpty()) {
            ErrorResponseDto error = new ErrorResponseDto(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Unauthorized: Invalid or missing API key",
                    OffsetDateTime.now(clock)
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "AUTHENTIC");
        response.put("extractedAt", OffsetDateTime.now(clock));
        response.putAll(agentContentService.getAllContentAsJson());

        return ResponseEntity.ok(response);
    }
}
