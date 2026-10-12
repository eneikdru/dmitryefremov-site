package com.dmitryefremov.site.ai;

import com.dmitryefremov.site.Application;
import com.dmitryefremov.site.agent.domain.AgentOrder;
import com.dmitryefremov.site.agent.repository.AgentOrderRepository;
import com.dmitryefremov.site.ai.domain.AiServiceOrder;
import com.dmitryefremov.site.ai.notification.AuthorNotification;
import com.dmitryefremov.site.ai.notification.AuthorNotificationService;
import com.dmitryefremov.site.ai.repository.AiDirectMessageRepository;
import com.dmitryefremov.site.ai.repository.AiServiceOrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class AiAgentApiFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AiServiceOrderRepository aiServiceOrderRepository;

    @Autowired
    private AiDirectMessageRepository aiDirectMessageRepository;

    @Autowired
    private AgentOrderRepository agentOrderRepository;

    @Autowired
    private AuthorNotificationService authorNotificationService;

    @BeforeEach
    void setUp() {
        aiServiceOrderRepository.deleteAll();
        aiDirectMessageRepository.deleteAll();
        agentOrderRepository.deleteAll();
        authorNotificationService.clearDispatchedNotifications();
    }

    @Test
    void testDiscoveryDocumentationEndpoint_returnsOpenApiSpecification() throws Exception {
        mockMvc.perform(get("/api/v1/agent/llms.txt"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(containsString("Dmitry Efremov Site - AI Agent API Specification")))
                .andExpect(content().string(containsString("POST /api/v1/agent/orders")));

        mockMvc.perform(get("/llms.txt"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(containsString("Dmitry Efremov Site - AI Agent API Specification")));
    }

    @Test
    void testAiAgentFullLifecycle_discoveryOrderNotificationAndAcknowledgement() throws Exception {
        // Step 1: Discover API spec
        mockMvc.perform(get("/api/v1/agent/llms.txt"))
                .andExpect(status().isOk());

        // Step 2: Submit service order via AI submission endpoint
        Map<String, String> orderRequest = Map.of(
                "userName", "Alice Assistant",
                "userEmail", "alice@example.com",
                "agentIdentifier", "chatgpt-4o",
                "serviceType", "MVP",
                "requirements", "Build MVP for automated customer onboarding"
        );

        mockMvc.perform(post("/api/v1/ai/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.userName").value("Alice Assistant"))
                .andExpect(jsonPath("$.userEmail").value("alice@example.com"))
                .andExpect(jsonPath("$.agentIdentifier").value("chatgpt-4o"))
                .andExpect(jsonPath("$.serviceType").value("MVP"))
                .andExpect(jsonPath("$.requirements").value("Build MVP for automated customer onboarding"));

        // Step 3: Verify DB persistence
        List<AiServiceOrder> orders = aiServiceOrderRepository.findAll();
        assertThat(orders).hasSize(1);
        AiServiceOrder savedOrder = orders.get(0);
        assertThat(savedOrder.getUserName()).isEqualTo("Alice Assistant");
        assertThat(savedOrder.getUserEmail()).isEqualTo("alice@example.com");
        assertThat(savedOrder.getServiceType()).isEqualTo("MVP");

        // Step 4: Verify author notification dispatch
        List<AuthorNotification> notifications = authorNotificationService.getDispatchedNotifications();
        assertThat(notifications).hasSize(1);
        AuthorNotification notification = notifications.get(0);
        assertThat(notification.getRecipientEmail()).isEqualTo("author@dmitryefremov.com");
        assertThat(notification.getSubmissionType()).isEqualTo("SERVICE_ORDER");
        assertThat(notification.getSenderName()).isEqualTo("Alice Assistant");
        assertThat(notification.getSenderEmail()).isEqualTo("alice@example.com");
        assertThat(notification.getAgentIdentifier()).isEqualTo("chatgpt-4o");
        assertThat(notification.getContent()).contains("MVP", "Build MVP for automated customer onboarding");
    }

    @Test
    void testServiceOrderAcceptsAllRealisticCategoriesFromBrief() throws Exception {
        String[] categories = {"MVP", "Audit", "Automation", "аудит", "автоматизация"};

        for (int i = 0; i < categories.length; i++) {
            String category = categories[i];

            Map<String, String> aiOrderRequest = Map.of(
                    "userName", "Client " + i,
                    "userEmail", "client" + i + "@example.com",
                    "agentIdentifier", "agent-" + i,
                    "serviceType", category,
                    "requirements", "Order for service category " + category
            );

            mockMvc.perform(post("/api/v1/ai/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(aiOrderRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", notNullValue()))
                    .andExpect(jsonPath("$.serviceType").value(category));

            Map<String, String> agentOrderRequest = Map.of(
                    "clientName", "Agent Client " + i,
                    "clientEmail", "agentclient" + i + "@example.com",
                    "serviceType", category,
                    "requirements", "Agent order for " + category
            );

            mockMvc.perform(post("/api/v1/agent/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(agentOrderRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", notNullValue()))
                    .andExpect(jsonPath("$.serviceType").value(category))
                    .andExpect(jsonPath("$.status").value("RECEIVED"));
        }

        List<AiServiceOrder> aiOrders = aiServiceOrderRepository.findAll();
        assertThat(aiOrders).hasSize(categories.length);

        List<AgentOrder> agentOrders = agentOrderRepository.findAll();
        assertThat(agentOrders).hasSize(categories.length);
    }

    @Test
    void testAiAgentDirectMessageLifecycle_storesAndNotifies() throws Exception {
        Map<String, String> messageRequest = Map.of(
                "senderName", "Bob Assistant",
                "senderEmail", "bob@example.com",
                "agentIdentifier", "claude-3-5-sonnet",
                "content", "Direct message inquiring about architecture consulting"
        );

        mockMvc.perform(post("/api/v1/ai/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.senderName").value("Bob Assistant"))
                .andExpect(jsonPath("$.senderEmail").value("bob@example.com"))
                .andExpect(jsonPath("$.agentIdentifier").value("claude-3-5-sonnet"))
                .andExpect(jsonPath("$.content").value("Direct message inquiring about architecture consulting"));

        assertThat(aiDirectMessageRepository.findAll()).hasSize(1);

        List<AuthorNotification> notifications = authorNotificationService.getDispatchedNotifications();
        assertThat(notifications).hasSize(1);
        AuthorNotification notification = notifications.get(0);
        assertThat(notification.getSubmissionType()).isEqualTo("DIRECT_MESSAGE");
        assertThat(notification.getSenderName()).isEqualTo("Bob Assistant");
        assertThat(notification.getContent()).isEqualTo("Direct message inquiring about architecture consulting");
    }
}
