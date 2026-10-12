package com.dmitryefremov.site.telemetry.controller;

import com.dmitryefremov.site.telemetry.service.AgentTelemetryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TelemetryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgentTelemetryService telemetryService;

    @BeforeEach
    void setUp() {
        telemetryService.reset();
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
}
