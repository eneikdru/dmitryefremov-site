package com.dmitryefremov.site.ai;

import com.dmitryefremov.site.ai.domain.AiDirectMessage;
import com.dmitryefremov.site.ai.domain.AiServiceOrder;
import com.dmitryefremov.site.ai.notification.AuthorNotification;
import com.dmitryefremov.site.ai.notification.AuthorNotificationService;
import com.dmitryefremov.site.ai.repository.AiDirectMessageRepository;
import com.dmitryefremov.site.ai.repository.AiServiceOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AiAgentLifecycleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AiServiceOrderRepository orderRepository;

    @Autowired
    private AiDirectMessageRepository messageRepository;

    @Autowired
    private AuthorNotificationService notificationService;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        messageRepository.deleteAll();
        notificationService.clearDispatchedNotifications();
    }

    @Test
    void testAgentDiscoveryDocs() throws Exception {
        // Discovery 1: llms.txt
        mockMvc.perform(get("/llms.txt"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(containsString("# Dmitry Efremov Personal Site & Service API for AI Agents")))
                .andExpect(content().string(containsString("POST /api/v1/ai/orders")))
                .andExpect(content().string(containsString("POST /api/v1/ai/messages")));

        // Discovery 2: OpenAPI JSON Spec
        mockMvc.perform(get("/api/v1/ai/docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.openapi").value("3.0.3"))
                .andExpect(jsonPath("$.info.title").value("AI Agent API Specification"))
                .andExpect(jsonPath("$.paths['/api/v1/ai/orders']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/ai/messages']").exists());
    }

    @Test
    void testFullLifecycleOrderSubmissionWithRealisticServiceCategories() throws Exception {
        String[] realisticCategories = {"MVP", "AUDIT", "AUTOMATION"};

        for (String category : realisticCategories) {
            String jsonPayload = String.format("""
                    {
                      "userName": "Agent User",
                      "userEmail": "user@example.com",
                      "agentIdentifier": "chatgpt-4o",
                      "serviceType": "%s",
                      "requirements": "Need professional %s services for automated pipeline"
                    }
                    """, category, category);

            // Submit order via API
            mockMvc.perform(post("/api/v1/ai/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonPayload))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value("NEW"))
                    .andExpect(jsonPath("$.message").value(containsString("Service order for '" + category + "' received successfully.")))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        // Verify stored in DB
        List<AiServiceOrder> storedOrders = orderRepository.findAll();
        assertThat(storedOrders).hasSize(3);
        assertThat(storedOrders).extracting(AiServiceOrder::getServiceType)
                .containsExactlyInAnyOrder("MVP", "AUDIT", "AUTOMATION");

        // Verify Author Notifications dispatched
        List<AuthorNotification> notifications = notificationService.getDispatchedNotifications();
        assertThat(notifications).hasSize(3);
        assertThat(notifications).allMatch(n -> "SERVICE_ORDER".equals(n.getSubmissionType()));
        assertThat(notifications).allMatch(n -> "author@dmitryefremov.com".equals(n.getRecipientEmail()));
    }

    @Test
    void testFullLifecycleDirectMessageSubmission() throws Exception {
        String jsonPayload = """
                {
                  "senderName": "Claude Assistant",
                  "senderEmail": "claude@example.com",
                  "agentIdentifier": "claude-3-5-sonnet",
                  "content": "Hello author, inquiring about availability for technical consultation."
                }
                """;

        // Submit direct message via API
        mockMvc.perform(post("/api/v1/ai/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.message").value("Direct message received successfully. Thank you for reaching out!"))
                .andExpect(jsonPath("$.timestamp").exists());

        // Verify stored in DB
        List<AiDirectMessage> storedMessages = messageRepository.findAll();
        assertThat(storedMessages).hasSize(1);
        AiDirectMessage msg = storedMessages.get(0);
        assertThat(msg.getSenderName()).isEqualTo("Claude Assistant");
        assertThat(msg.getSenderEmail()).isEqualTo("claude@example.com");
        assertThat(msg.getAgentIdentifier()).isEqualTo("claude-3-5-sonnet");
        assertThat(msg.getContent()).isEqualTo("Hello author, inquiring about availability for technical consultation.");

        // Verify Author Notification dispatched
        List<AuthorNotification> notifications = notificationService.getDispatchedNotifications();
        assertThat(notifications).hasSize(1);
        AuthorNotification notification = notifications.get(0);
        assertThat(notification.getSubmissionType()).isEqualTo("DIRECT_MESSAGE");
        assertThat(notification.getSenderName()).isEqualTo("Claude Assistant");
        assertThat(notification.getSenderEmail()).isEqualTo("claude@example.com");
        assertThat(notification.getAgentIdentifier()).isEqualTo("claude-3-5-sonnet");
        assertThat(notification.getContent()).isEqualTo("Hello author, inquiring about availability for technical consultation.");
    }
}
