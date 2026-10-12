package com.dmitryefremov.site.agent.controller;

import com.dmitryefremov.site.agent.dto.AgentMessageRequest;
import com.dmitryefremov.site.agent.dto.AgentOrderRequest;
import com.dmitryefremov.site.agent.repository.AgentInteractionTelemetryRepository;
import com.dmitryefremov.site.agent.repository.AgentMessageRepository;
import com.dmitryefremov.site.agent.repository.AgentOrderRepository;
import com.dmitryefremov.site.agent.service.AgentInteractionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
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
    private AgentOrderRepository orderRepository;

    @Autowired
    private AgentMessageRepository messageRepository;

    @Autowired
    private AgentInteractionTelemetryRepository telemetryRepository;

    @Autowired
    private AgentInteractionController agentInteractionController;

    @Autowired
    private AgentInteractionService agentInteractionService;

    private final Instant fixedInstant = Instant.parse("2026-10-12T12:00:00Z");
    private final Clock fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        messageRepository.deleteAll();
        agentInteractionController.setClock(fixedClock);
        agentInteractionService.setClock(fixedClock);
    }

    @Test
    void testSubmitOrderIncrementsCounterAndReportsInTelemetry() throws Exception {
        AgentOrderRequest orderRequest = new AgentOrderRequest(
                "Claude Personal Assistant",
                "claude@anthropic.com",
                "MVP",
                "Automated MVP request"
        );

        mockMvc.perform(post("/api/v1/agent/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientName", is("Claude Personal Assistant")))
                .andExpect(jsonPath("$.clientEmail", is("claude@anthropic.com")))
                .andExpect(jsonPath("$.serviceType", is("MVP")))
                .andExpect(jsonPath("$.status", is("RECEIVED")));

        mockMvc.perform(get("/api/v1/agent/telemetry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders", is(1)))
                .andExpect(jsonPath("$.totalMessages", is(0)))
                .andExpect(jsonPath("$.totalInteractions", is(1)));
    }

    @Test
    void testSubmitMessageIncrementsCounterAndReportsInTelemetry() throws Exception {
        AgentMessageRequest messageRequest = new AgentMessageRequest(
                "GPT Agent",
                "gpt@openai.com",
                "Inquiry about audit timeline"
        );

        mockMvc.perform(post("/api/v1/agent/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.senderName", is("GPT Agent")))
                .andExpect(jsonPath("$.senderEmail", is("gpt@openai.com")))
                .andExpect(jsonPath("$.message", is("Inquiry about audit timeline")))
                .andExpect(jsonPath("$.status", is("SENT")));

        mockMvc.perform(get("/api/v1/agent/telemetry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders", is(0)))
                .andExpect(jsonPath("$.totalMessages", is(1)))
                .andExpect(jsonPath("$.totalInteractions", is(1)));
    }

    @Test
    void testMultipleInteractionsAreAggregatedAccuratelyInTelemetry() throws Exception {
        AgentOrderRequest order1 = new AgentOrderRequest("Agent Alpha", "alpha@agent.com", "AUDIT", "Audit service");
        AgentOrderRequest order2 = new AgentOrderRequest("Agent Beta", "beta@agent.com", "AUTOMATION", "Automation service");
        AgentMessageRequest msg1 = new AgentMessageRequest("Agent Gamma", "gamma@agent.com", "Direct message 1");

        mockMvc.perform(post("/api/v1/agent/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/agent/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/agent/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(msg1)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/agent/telemetry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders", is(2)))
                .andExpect(jsonPath("$.totalMessages", is(1)))
                .andExpect(jsonPath("$.totalInteractions", is(3)));

        // Verify telemetry alias endpoint as well
        mockMvc.perform(get("/api/v1/telemetry/agent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders", is(2)))
                .andExpect(jsonPath("$.totalMessages", is(1)))
                .andExpect(jsonPath("$.totalInteractions", is(3)));
    }

    @Test
    void testLlmInstructionsEndpointReturns200TextPlain() throws Exception {
        mockMvc.perform(get("/api/v1/agent/llms.txt"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(containsString("Dmitry Efremov Site - AI Agent API Specification")))
                .andExpect(content().string(containsString("POST /api/v1/agent/orders")));
    }

    @Test
    void testSubmitOrderValidationFailureReturns400WithInjectedTimestamp() throws Exception {
        AgentOrderRequest invalidOrder = new AgentOrderRequest("", "invalid@email.com", "MVP", "Notes");

        mockMvc.perform(post("/api/v1/agent/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOrder)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Client name is required")))
                .andExpect(jsonPath("$.timestamp", containsString("2026-10-12T12:00:00Z")));
    }
}
