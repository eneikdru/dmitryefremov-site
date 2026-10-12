package com.dmitryefremov.site.compliance.controller;

import com.dmitryefremov.site.compliance.domain.AgentMessage;
import com.dmitryefremov.site.compliance.domain.AgentOrder;
import com.dmitryefremov.site.compliance.repository.AgentMessageRepository;
import com.dmitryefremov.site.compliance.repository.AgentOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ComplianceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgentOrderRepository orderRepository;

    @Autowired
    private AgentMessageRepository messageRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        messageRepository.deleteAll();
    }

    @Test
    void testExportUserData_returnsUserOrdersAndMessages() throws Exception {
        String targetUser = "user-test-export";
        String otherUser = "user-test-other";

        orderRepository.save(new AgentOrder(targetUser, "MVP", "MVP development", "CREATED", OffsetDateTime.now()));
        orderRepository.save(new AgentOrder(otherUser, "AUDIT", "Audit request", "CREATED", OffsetDateTime.now()));

        messageRepository.save(new AgentMessage(targetUser, "Agent 1", "Hello from agent", OffsetDateTime.now()));

        mockMvc.perform(get("/api/v1/compliance/export")
                        .param("userIdentifier", targetUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userIdentifier").value(targetUser))
                .andExpect(jsonPath("$.orders.length()").value(1))
                .andExpect(jsonPath("$.orders[0].serviceType").value("MVP"))
                .andExpect(jsonPath("$.messages.length()").value(1))
                .andExpect(jsonPath("$.messages[0].messageText").value("Hello from agent"));
    }

    @Test
    void testEraseUserData_permanentlyDeletesUserDataOnly() throws Exception {
        String targetUser = "user-test-erasure";
        String otherUser = "user-test-keep";

        orderRepository.save(new AgentOrder(targetUser, "AUTOMATION", "Automation script", "COMPLETED", OffsetDateTime.now()));
        orderRepository.save(new AgentOrder(otherUser, "AUDIT", "Audit request", "CREATED", OffsetDateTime.now()));

        messageRepository.save(new AgentMessage(targetUser, "Agent 2", "Delete me", OffsetDateTime.now()));
        messageRepository.save(new AgentMessage(otherUser, "Agent 3", "Keep me", OffsetDateTime.now()));

        mockMvc.perform(delete("/api/v1/compliance/erasure")
                        .param("userIdentifier", targetUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userIdentifier").value(targetUser))
                .andExpect(jsonPath("$.deletedOrdersCount").value(1))
                .andExpect(jsonPath("$.deletedMessagesCount").value(1))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertThat(orderRepository.findByUserIdentifier(targetUser)).isEmpty();
        assertThat(messageRepository.findByUserIdentifier(targetUser)).isEmpty();

        assertThat(orderRepository.findByUserIdentifier(otherUser)).hasSize(1);
        assertThat(messageRepository.findByUserIdentifier(otherUser)).hasSize(1);
    }
}
