package com.dmitryefremov.site.acceptance;

import com.dmitryefremov.site.ai.domain.AiDirectMessage;
import com.dmitryefremov.site.ai.domain.AiServiceOrder;
import com.dmitryefremov.site.ai.notification.AuthorNotification;
import com.dmitryefremov.site.ai.notification.AuthorNotificationService;
import com.dmitryefremov.site.ai.repository.AiDirectMessageRepository;
import com.dmitryefremov.site.ai.repository.AiServiceOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClientIntakeQaVerificationE2ETest {

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
    @DisplayName("Given E2E suite, when human intake request is simulated, then DB records it and acknowledgement mock intercepts dispatch")
    void testHumanIntakeRequestSimulatedDatabaseRecordingAndAcknowledgement() throws Exception {
        String humanPayload = """
                {
                  "userName": "Alice Human Client",
                  "userEmail": "alice@client-domain.org",
                  "agentIdentifier": "web-form-human",
                  "serviceType": "AUDIT",
                  "requirements": "High-end systems audit request for agentic architecture"
                }
                """;

        mockMvc.perform(post("/api/v1/ai/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(humanPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.message").value(containsString("Service order for 'AUDIT' received successfully.")))
                .andExpect(jsonPath("$.timestamp").exists());

        // Verify database persistence
        List<AiServiceOrder> orders = orderRepository.findAll();
        assertThat(orders).hasSize(1);
        AiServiceOrder order = orders.get(0);
        assertThat(order.getUserName()).isEqualTo("Alice Human Client");
        assertThat(order.getUserEmail()).isEqualTo("alice@client-domain.org");
        assertThat(order.getServiceType()).isEqualTo("AUDIT");

        // Verify acknowledgement mock intercepted the dispatch
        List<AuthorNotification> notifications = notificationService.getDispatchedNotifications();
        assertThat(notifications).hasSize(1);
        AuthorNotification notification = notifications.get(0);
        assertThat(notification.getSubmissionType()).isEqualTo("SERVICE_ORDER");
        assertThat(notification.getSenderName()).isEqualTo("Alice Human Client");
        assertThat(notification.getSenderEmail()).isEqualTo("alice@client-domain.org");
        assertThat(notification.getAgentIdentifier()).isEqualTo("web-form-human");
    }

    @Test
    @DisplayName("Given A2A simulated payload, when sent directly to API, then processed and acknowledged identically")
    void testA2ASimulatedPayloadProcessedAndAcknowledgedIdentically() throws Exception {
        String a2aPayload = """
                {
                  "userName": "Autonomous Agent A2A",
                  "userEmail": "agent-gateway@a2a-network.io",
                  "agentIdentifier": "a2a-gateway-agent-v1",
                  "serviceType": "MVP",
                  "requirements": "Autonomous A2A agentic turnkey platform integration request"
                }
                """;

        mockMvc.perform(post("/api/v1/ai/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(a2aPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.message").value(containsString("Service order for 'MVP' received successfully.")))
                .andExpect(jsonPath("$.timestamp").exists());

        // Verify database persistence
        List<AiServiceOrder> orders = orderRepository.findAll();
        assertThat(orders).hasSize(1);
        AiServiceOrder order = orders.get(0);
        assertThat(order.getUserName()).isEqualTo("Autonomous Agent A2A");
        assertThat(order.getUserEmail()).isEqualTo("agent-gateway@a2a-network.io");
        assertThat(order.getServiceType()).isEqualTo("MVP");

        // Verify acknowledgement intercepted identically
        List<AuthorNotification> notifications = notificationService.getDispatchedNotifications();
        assertThat(notifications).hasSize(1);
        AuthorNotification notification = notifications.get(0);
        assertThat(notification.getSubmissionType()).isEqualTo("SERVICE_ORDER");
        assertThat(notification.getSenderName()).isEqualTo("Autonomous Agent A2A");
        assertThat(notification.getSenderEmail()).isEqualTo("agent-gateway@a2a-network.io");
        assertThat(notification.getAgentIdentifier()).isEqualTo("a2a-gateway-agent-v1");
    }

    @Test
    @DisplayName("Given A2A direct message payload, when sent directly to API, then processed and acknowledged identically")
    void testA2ADirectMessagePayloadProcessedAndAcknowledgedIdentically() throws Exception {
        String a2aMessagePayload = """
                {
                  "senderName": "A2A Peer Agent",
                  "senderEmail": "peer-agent@a2a-mesh.net",
                  "agentIdentifier": "a2a-mesh-node-42",
                  "content": "A2A mesh query regarding agentic architecture deployment."
                }
                """;

        mockMvc.perform(post("/api/v1/ai/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(a2aMessagePayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.message").value("Direct message received successfully. Thank you for reaching out!"))
                .andExpect(jsonPath("$.timestamp").exists());

        // Verify database persistence
        List<AiDirectMessage> messages = messageRepository.findAll();
        assertThat(messages).hasSize(1);
        AiDirectMessage msg = messages.get(0);
        assertThat(msg.getSenderName()).isEqualTo("A2A Peer Agent");
        assertThat(msg.getSenderEmail()).isEqualTo("peer-agent@a2a-mesh.net");

        // Verify acknowledgement intercepted identically
        List<AuthorNotification> notifications = notificationService.getDispatchedNotifications();
        assertThat(notifications).hasSize(1);
        AuthorNotification notification = notifications.get(0);
        assertThat(notification.getSubmissionType()).isEqualTo("DIRECT_MESSAGE");
        assertThat(notification.getSenderName()).isEqualTo("A2A Peer Agent");
        assertThat(notification.getSenderEmail()).isEqualTo("peer-agent@a2a-mesh.net");
    }
}
