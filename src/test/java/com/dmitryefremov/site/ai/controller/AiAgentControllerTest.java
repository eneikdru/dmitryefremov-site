package com.dmitryefremov.site.ai.controller;

import com.dmitryefremov.site.ai.domain.AiDirectMessage;
import com.dmitryefremov.site.ai.domain.AiServiceOrder;
import com.dmitryefremov.site.ai.dto.AiDirectMessageResponseDto;
import com.dmitryefremov.site.ai.dto.AiServiceOrderResponseDto;
import com.dmitryefremov.site.ai.repository.AiDirectMessageRepository;
import com.dmitryefremov.site.ai.repository.AiServiceOrderRepository;
import com.dmitryefremov.site.ai.service.AiAgentService;
import com.dmitryefremov.site.ai.service.AiRateLimiterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AiAgentController.class)
@Import({AiAgentService.class, AiRateLimiterService.class})
class AiAgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiServiceOrderRepository orderRepository;

    @MockBean
    private AiDirectMessageRepository messageRepository;

    @MockBean
    private Clock clock;

    @SpyBean
    private AiRateLimiterService rateLimiterService;

    private final Instant fixedInstant = Instant.parse("2026-10-12T12:00:00Z");

    @BeforeEach
    void setUp() {
        given(clock.instant()).willReturn(fixedInstant);
        given(clock.getZone()).willReturn(ZoneOffset.UTC);
        given(clock.millis()).willReturn(fixedInstant.toEpochMilli());
        rateLimiterService.reset();
    }

    @Test
    void testCreateOrderSuccessReturnsCreatedAndAcknowledgement() throws Exception {
        AiServiceOrder savedOrder = new AiServiceOrder(
                "Alice AI User",
                "alice@example.com",
                "chatgpt-agent-001",
                "MVP",
                "Build MVP for logistics startup",
                OffsetDateTime.ofInstant(fixedInstant, ZoneOffset.UTC)
        );

        given(orderRepository.save(any(AiServiceOrder.class))).willReturn(savedOrder);

        String jsonPayload = """
                {
                  "userName": "Alice AI User",
                  "userEmail": "alice@example.com",
                  "agentIdentifier": "chatgpt-agent-001",
                  "serviceType": "MVP",
                  "requirements": "Build MVP for logistics startup"
                }
                """;

        mockMvc.perform(post("/api/v1/ai/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.message").value("Service order for 'MVP' received successfully. We will follow up shortly via alice@example.com"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testCreateOrderInvalidPayloadReturnsBadRequest() throws Exception {
        String jsonPayload = """
                {
                  "userName": "",
                  "userEmail": "invalid-email-format",
                  "serviceType": ""
                }
                """;

        mockMvc.perform(post("/api/v1/ai/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Validation failed")));
    }

    @Test
    void testCreateMessageSuccessReturnsCreatedAndAcknowledgement() throws Exception {
        AiDirectMessage savedMessage = new AiDirectMessage(
                "Bob Assistant",
                "bob@example.com",
                "claude-agent-002",
                "Hello, I am reaching out regarding automated system integration.",
                OffsetDateTime.ofInstant(fixedInstant, ZoneOffset.UTC)
        );

        given(messageRepository.save(any(AiDirectMessage.class))).willReturn(savedMessage);

        String jsonPayload = """
                {
                  "senderName": "Bob Assistant",
                  "senderEmail": "bob@example.com",
                  "agentIdentifier": "claude-agent-002",
                  "content": "Hello, I am reaching out regarding automated system integration."
                }
                """;

        mockMvc.perform(post("/api/v1/ai/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.message").value("Direct message received successfully. Thank you for reaching out!"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testRateLimitExceededReturnsTooManyRequests() throws Exception {
        AiServiceOrder savedOrder = new AiServiceOrder(
                "Alice", "alice@example.com", "rate-test-agent", "MVP", "Req",
                OffsetDateTime.ofInstant(fixedInstant, ZoneOffset.UTC)
        );
        given(orderRepository.save(any(AiServiceOrder.class))).willReturn(savedOrder);

        String jsonPayload = """
                {
                  "userName": "Alice",
                  "userEmail": "alice@example.com",
                  "agentIdentifier": "rate-test-agent",
                  "serviceType": "MVP"
                }
                """;

        // Send 10 allowed requests
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/api/v1/ai/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isCreated());
        }

        // 11th request should be rejected with 429
        mockMvc.perform(post("/api/v1/ai/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.message").value("Rate limit exceeded. Please try again later."));
    }
}
