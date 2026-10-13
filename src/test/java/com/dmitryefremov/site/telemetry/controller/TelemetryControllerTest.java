package com.dmitryefremov.site.telemetry.controller;

import com.dmitryefremov.site.telemetry.repository.ScrollytellingProgressionRepository;
import com.dmitryefremov.site.telemetry.repository.TelemetryEventRepository;
import com.dmitryefremov.site.telemetry.service.AgentTelemetryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
    private AgentTelemetryService agentTelemetryService;

    @Autowired
    private TelemetryEventRepository telemetryEventRepository;

    @Autowired
    private ScrollytellingProgressionRepository scrollytellingProgressionRepository;

    @BeforeEach
    void setUp() {
        agentTelemetryService.reset();
        telemetryEventRepository.deleteAll();
        scrollytellingProgressionRepository.deleteAll();
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
    @DisplayName("Acceptance Criterion 1: Ingest A2A consumption event and persist metric to timeseries datastore")
    void shouldIngestA2AConsumptionEventAndPersistToTimeseriesDatastore() throws Exception {
        String payload = """
                {
                    "eventType": "A2A_CONSUMPTION",
                    "source": "a2a-agent-client",
                    "resourceId": "article-42",
                    "metricValue": 1.0,
                    "payload": "{\\"agentId\\":\\"agent-test-1\\"}"
                }
                """;

        mockMvc.perform(post("/api/v1/telemetry/ingest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventType", is("A2A_CONSUMPTION")))
                .andExpect(jsonPath("$.source", is("a2a-agent-client")))
                .andExpect(jsonPath("$.resourceId", is("article-42")))
                .andExpect(jsonPath("$.metricValue", is(1.0)))
                .andExpect(jsonPath("$.status", is("PROCESSED")));

        // Verify metric event was persisted to timeseries datastore
        mockMvc.perform(get("/api/v1/telemetry/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].eventType", is("A2A_CONSUMPTION")))
                .andExpect(jsonPath("$[0].resourceId", is("article-42")))
                .andExpect(jsonPath("$[0].metricValue", is(1.0)));
    }

    @Test
    @DisplayName("Acceptance Criterion 2: Process scrollytelling depth payload and increment progression metric")
    void shouldProcessScrollytellingDepthAndIncrementProgressionMetric() throws Exception {
        String firstPayload = """
                {
                    "resourceId": "essay-scrollytelling-alpha",
                    "depth": 25.0,
                    "source": "scrollytelling-ui"
                }
                """;

        mockMvc.perform(post("/api/v1/telemetry/scrollytelling")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resourceId", is("essay-scrollytelling-alpha")))
                .andExpect(jsonPath("$.maxDepth", is(25.0)))
                .andExpect(jsonPath("$.totalDepth", is(25.0)))
                .andExpect(jsonPath("$.eventCount", is(1)));

        String secondPayload = """
                {
                    "resourceId": "essay-scrollytelling-alpha",
                    "depth": 80.0,
                    "source": "scrollytelling-ui"
                }
                """;

        mockMvc.perform(post("/api/v1/telemetry/scrollytelling")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resourceId", is("essay-scrollytelling-alpha")))
                .andExpect(jsonPath("$.maxDepth", is(80.0)))
                .andExpect(jsonPath("$.totalDepth", is(105.0)))
                .andExpect(jsonPath("$.eventCount", is(2)));

        // Retrieve scrollytelling progression metric
        mockMvc.perform(get("/api/v1/telemetry/scrollytelling/essay-scrollytelling-alpha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceId", is("essay-scrollytelling-alpha")))
                .andExpect(jsonPath("$.maxDepth", is(80.0)))
                .andExpect(jsonPath("$.totalDepth", is(105.0)))
                .andExpect(jsonPath("$.eventCount", is(2)));
    }

    @Test
    @DisplayName("Process scrollytelling depth via general ingestion endpoint")
    void shouldProcessScrollytellingDepthViaIngestEndpoint() throws Exception {
        String payload = """
                {
                    "eventType": "SCROLLYTELLING_DEPTH",
                    "source": "general-ingest",
                    "resourceId": "essay-scrollytelling-beta",
                    "metricValue": 50.0
                }
                """;

        mockMvc.perform(post("/api/v1/telemetry/ingest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventType", is("SCROLLYTELLING_DEPTH")))
                .andExpect(jsonPath("$.resourceId", is("essay-scrollytelling-beta")))
                .andExpect(jsonPath("$.metricValue", is(50.0)));

        mockMvc.perform(get("/api/v1/telemetry/scrollytelling/essay-scrollytelling-beta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxDepth", is(50.0)))
                .andExpect(jsonPath("$.totalDepth", is(50.0)))
                .andExpect(jsonPath("$.eventCount", is(1)));
    }
}
