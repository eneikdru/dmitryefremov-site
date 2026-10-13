package com.dmitryefremov.site.telemetry.controller;

import com.dmitryefremov.site.telemetry.domain.PlatformTelemetryEvent;
import com.dmitryefremov.site.telemetry.domain.PlatformTelemetryMetric;
import com.dmitryefremov.site.telemetry.repository.PlatformTelemetryEventRepository;
import com.dmitryefremov.site.telemetry.repository.PlatformTelemetryMetricRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PlatformTelemetryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlatformTelemetryEventRepository eventRepository;

    @Autowired
    private PlatformTelemetryMetricRepository metricRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        metricRepository.deleteAll();
    }

    @Test
    void shouldIngestA2AConsumptionEventAndIncrementMetric() throws Exception {
        String jsonPayload = """
                {
                    "eventType": "A2A_CONSUMPTION",
                    "value": 1.5,
                    "metadata": "{\\"source\\": \\"agent\\"}"
                }
                """;

        mockMvc.perform(post("/api/v1/telemetry/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isAccepted());

        List<PlatformTelemetryEvent> events = eventRepository.findAll();
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getEventType()).isEqualTo("A2A_CONSUMPTION");
        // Use type-safe numeric assertion with tolerance for floats over JSON boundary
        assertThat(events.get(0).getMetricValue()).isCloseTo(1.5, within(0.0001));
        assertThat(events.get(0).getEventMetadata()).isEqualTo("{\"source\": \"agent\"}");

        Optional<PlatformTelemetryMetric> metric = metricRepository.findById("A2A_CONSUMPTION");
        assertThat(metric).isPresent();
        assertThat(metric.get().getCounter()).isCloseTo(1.5, within(0.0001));
    }

    @Test
    void shouldIngestScrollytellingDepthEventAndIncrementMetric() throws Exception {
        String jsonPayload = """
                {
                    "eventType": "SCROLLYTELLING_DEPTH",
                    "value": 25.0
                }
                """;

        mockMvc.perform(post("/api/v1/telemetry/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isAccepted());

        List<PlatformTelemetryEvent> events = eventRepository.findAll();
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getEventType()).isEqualTo("SCROLLYTELLING_DEPTH");
        assertThat(events.get(0).getMetricValue()).isCloseTo(25.0, within(0.0001));

        Optional<PlatformTelemetryMetric> metric = metricRepository.findById("SCROLLYTELLING_DEPTH");
        assertThat(metric).isPresent();
        assertThat(metric.get().getCounter()).isCloseTo(25.0, within(0.0001));
    }

    @Test
    void shouldFailValidationOnMissingValue() throws Exception {
        String jsonPayload = """
                {
                    "eventType": "A2A_CONSUMPTION"
                }
                """;

        mockMvc.perform(post("/api/v1/telemetry/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }
}
