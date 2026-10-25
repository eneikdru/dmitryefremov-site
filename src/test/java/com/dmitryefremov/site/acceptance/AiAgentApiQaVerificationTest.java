package com.dmitryefremov.site.acceptance;

import com.dmitryefremov.site.ai.service.AiRateLimiterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AiAgentApiQaVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AiRateLimiterService aiRateLimiterService;

    @BeforeEach
    void setUp() {
        aiRateLimiterService.reset();
    }

    @Test
    @DisplayName("Given AI agent order and message endpoints, when invalid payloads are submitted, then structured 400 error responses are returned")
    void testInvalidPayloadsReturnStructuredErrorResponses() throws Exception {
        // Invalid AI service order payload (blank user name and invalid email format)
        String invalidAiOrderPayload = """
                {
                  "userName": "",
                  "userEmail": "not-an-email",
                  "serviceType": ""
                }
                """;

        mockMvc.perform(post("/api/v1/ai/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidAiOrderPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Validation failed")))
                .andExpect(jsonPath("$.timestamp").exists());

        // Invalid AI direct message payload (blank sender name and blank content)
        String invalidAiMessagePayload = """
                {
                  "senderName": "",
                  "senderEmail": "invalid-email",
                  "content": ""
                }
                """;

        mockMvc.perform(post("/api/v1/ai/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidAiMessagePayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Validation failed")))
                .andExpect(jsonPath("$.timestamp").exists());

        // Invalid agent order payload (missing client name)
        String invalidAgentOrderPayload = """
                {
                  "clientName": "",
                  "clientEmail": "client@example.com",
                  "serviceType": "MVP"
                }
                """;

        mockMvc.perform(post("/api/v1/agent/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidAgentOrderPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Client name is required")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Given AI agent endpoints under load, when rate limit is exceeded, then excessive requests are blocked with HTTP 429")
    void testRateLimiterBlocksExcessiveRequestsUnderLoad() throws Exception {
        String validOrderPayload = """
                {
                  "userName": "Load Test Agent",
                  "userEmail": "loadtest@example.com",
                  "agentIdentifier": "load-test-bot-001",
                  "serviceType": "MVP",
                  "requirements": "Load testing request"
                }
                """;

        // Capacity is 10 tokens per window per key
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/api/v1/ai/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validOrderPayload))
                    .andExpect(status().isCreated());
        }

        // 11th request must be rate limited
        mockMvc.perform(post("/api/v1/ai/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrderPayload))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status", is(429)))
                .andExpect(jsonPath("$.message", is("Rate limit exceeded. Please try again later.")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Given discovery and manifest URLs, when fetched, then valid agent configurations and specs are returned")
    void testManifestUrlsReturnValidConfigurations() throws Exception {
        // Root llms.txt discovery
        mockMvc.perform(get("/llms.txt"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(containsString("# Dmitry Efremov Personal Site & Service API for AI Agents")));

        // .well-known/llms.txt content index
        mockMvc.perform(get("/.well-known/llms.txt"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(containsString("# Dmitry Efremov Canon Index")));

        // .well-known/ai-agent.json JSON discovery
        mockMvc.perform(get("/.well-known/ai-agent.json"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.articles").isArray())
                .andExpect(jsonPath("$.essays").isArray())
                .andExpect(jsonPath("$.podcasts").isArray());

        // OpenAPI Spec endpoint
        mockMvc.perform(get("/api/v1/ai/docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.openapi", is("3.0.3")))
                .andExpect(jsonPath("$.info.title", is("AI Agent API Specification")));

        // Agent interaction manifest endpoint
        mockMvc.perform(get("/api/v1/agent/llms.txt"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(containsString("Dmitry Efremov Site - AI Agent API Specification")));
    }
}
