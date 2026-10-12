package com.dmitryefremov.site.agent.controller;

import com.dmitryefremov.site.agent.dto.AgentMessageRequestDto;
import com.dmitryefremov.site.agent.dto.AgentOrderRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AgentInteractionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    void testSubmitOrderIncrementsMetricAndReportsInTelemetry() throws Exception {
        double initialOrderCount = meterRegistry.counter("ai.agent.interactions", "type", "order").count();

        AgentOrderRequestDto orderReq = new AgentOrderRequestDto(
                "MVP",
                "Acme Corp",
                "contact@acme.com",
                "Need MVP in 2 weeks",
                "ChatGPT"
        );

        mockMvc.perform(post("/api/v1/agent/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.serviceType").value("MVP"))
                .andExpect(jsonPath("$.clientName").value("Acme Corp"))
                .andExpect(jsonPath("$.contactEmail").value("contact@acme.com"))
                .andExpect(jsonPath("$.requirements").value("Need MVP in 2 weeks"))
                .andExpect(jsonPath("$.agentName").value("ChatGPT"));

        double updatedOrderCount = meterRegistry.counter("ai.agent.interactions", "type", "order").count();
        assertThat(updatedOrderCount).isEqualTo(initialOrderCount + 1.0);

        mockMvc.perform(get("/api/v1/agent/telemetry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").value(1))
                .andExpect(jsonPath("$.totalMessages").value(0))
                .andExpect(jsonPath("$.totalInteractions").value(1));
    }

    @Test
    void testSendMessageIncrementsMetricAndReportsInTelemetry() throws Exception {
        double initialMessageCount = meterRegistry.counter("ai.agent.interactions", "type", "message").count();

        AgentMessageRequestDto messageReq = new AgentMessageRequestDto(
                "Alice",
                "alice@example.com",
                "Hello Dmitry, interested in audit.",
                "Claude"
        );

        mockMvc.perform(post("/api/v1/agent/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.senderName").value("Alice"))
                .andExpect(jsonPath("$.contactEmail").value("alice@example.com"))
                .andExpect(jsonPath("$.messageText").value("Hello Dmitry, interested in audit."))
                .andExpect(jsonPath("$.agentName").value("Claude"));

        double updatedMessageCount = meterRegistry.counter("ai.agent.interactions", "type", "message").count();
        assertThat(updatedMessageCount).isEqualTo(initialMessageCount + 1.0);

        mockMvc.perform(get("/api/v1/agent/telemetry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").value(0))
                .andExpect(jsonPath("$.totalMessages").value(1))
                .andExpect(jsonPath("$.totalInteractions").value(1));
    }

    @Test
    void testGetLlmsTxtReturnsPlainTextInstructions() throws Exception {
        mockMvc.perform(get("/llms.txt"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Dmitry Efremov - AI Agent Direct API")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("POST /api/v1/agent/orders")));
    }

    @Test
    void testSubmitOrderValidationFailure() throws Exception {
        AgentOrderRequestDto invalidReq = new AgentOrderRequestDto("", "Client", "email@test.com", "Req", "Agent");

        mockMvc.perform(post("/api/v1/agent/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("serviceType must not be blank"));
    }
}
