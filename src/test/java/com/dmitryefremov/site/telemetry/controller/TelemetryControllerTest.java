package com.dmitryefremov.site.telemetry.controller;

import com.dmitryefremov.site.admin.domain.TelemetryLog;
import com.dmitryefremov.site.admin.repository.TelemetryLogRepository;
import com.dmitryefremov.site.telemetry.dto.TelemetryIngestRequestDto;
import com.dmitryefremov.site.telemetry.service.AgentTelemetryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TelemetryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgentTelemetryService telemetryService;

    @Autowired
    private TelemetryLogRepository telemetryLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        telemetryService.reset();
        telemetryLogRepository.deleteAll();
    }

    @Test
    void shouldTrackApiRequestsAndExposeMetrics() throws Exception {
        // Query an existing endpoint to record a successful request
        mockMvc.perform(get("/api/v1/articles"))
                .andExpect(status().isOk());

        // Query a non-existent endpoint under /api/v1 to record a failed request
        mockMvc.perform(get("/api/v1/articles/non-existent-article-slug-xyz"))
                .andExpect(status().isNotFound());

        // Verify /api/v1/telemetry/metrics reflects 2 requests, 1 failure, failureRate = 0.5
        mockMvc.perform(get("/api/v1/telemetry/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalApiRequests", is(2)))
                .andExpect(jsonPath("$.failedApiRequests", is(1)))
                .andExpect(jsonPath("$.totalAgentUpdates", is(0)));
    }

    @Test
    void shouldIngestA2AConsumptionTelemetryEvent() throws Exception {
        TelemetryIngestRequestDto request = new TelemetryIngestRequestDto(
                "A2A_CONSUMPTION_EVENT",
                "{\"agentId\":\"agent-1\",\"endpoint\":\"/api/v1/a2a/materials\"}",
                "a2a-agent"
        );

        mockMvc.perform(post("/api/v1/telemetry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.eventType", is("A2A_CONSUMPTION_EVENT")))
                .andExpect(jsonPath("$.source", is("a2a-agent")))
                .andExpect(jsonPath("$.createdAt", notNullValue()));

        List<TelemetryLog> logs = telemetryLogRepository.findByEventType("A2A_CONSUMPTION_EVENT");
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getSource()).isEqualTo("a2a-agent");
    }

    @Test
    void shouldIngestScrollytellingDepthProgressionEvent() throws Exception {
        TelemetryIngestRequestDto request = new TelemetryIngestRequestDto(
                "SCROLLEVENT",
                "{\"section\":\"hero\",\"depthPercentage\":75.5}",
                "web-client"
        );

        mockMvc.perform(post("/api/v1/telemetry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.eventType", is("SCROLLEVENT")))
                .andExpect(jsonPath("$.source", is("web-client")));

        List<TelemetryLog> logs = telemetryLogRepository.findByEventType("SCROLLEVENT");
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getPayload()).contains("75.5");
    }

    @Test
    void shouldReturn400WhenEventTypeIsMissing() throws Exception {
        TelemetryIngestRequestDto request = new TelemetryIngestRequestDto(
                "",
                "{\"section\":\"hero\"}",
                "web-client"
        );

        mockMvc.perform(post("/api/v1/telemetry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Event type cannot be empty")));
    }
}
