package com.dmitryefremov.site.telemetry.controller;

import com.dmitryefremov.site.telemetry.service.AgentTelemetryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;

import com.dmitryefremov.site.telemetry.domain.TelemetryEvent;
import com.dmitryefremov.site.telemetry.repository.TelemetryEventRepository;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dmitryefremov.site.Application;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class TelemetryControllerTest {

    @Autowired
    private TelemetryEventRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgentTelemetryService telemetryService;

    @BeforeEach
    void setUp() {
        telemetryService.reset();
        repository.deleteAll();
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
    void shouldIngestTelemetryEvents() throws Exception {
        String a2aPayload = """
                {
                    "eventType": "A2A_CONSUMPTION",
                    "metricValue": 1
                }
                """;

        mockMvc.perform(post("/api/v1/telemetry/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(a2aPayload))
                .andExpect(status().isOk());

        String scrollytellingPayload = """
                {
                    "eventType": "SCROLLYTELLING_DEPTH",
                    "metricValue": 5
                }
                """;

        mockMvc.perform(post("/api/v1/telemetry/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(scrollytellingPayload))
                .andExpect(status().isOk());

        List<TelemetryEvent> events = repository.findAll();
        assertThat(events).hasSize(2);

        assertThat(events).anyMatch(e -> "A2A_CONSUMPTION".equals(e.getEventType()) && e.getMetricValue() == 1L);
        assertThat(events).anyMatch(e -> "SCROLLYTELLING_DEPTH".equals(e.getEventType()) && e.getMetricValue() == 5L);
    }
}
