package com.dmitryefremov.site.compliance.controller;

import com.dmitryefremov.site.agent.domain.AgentMessage;
import com.dmitryefremov.site.agent.domain.AgentOrder;
import com.dmitryefremov.site.agent.repository.AgentMessageRepository;
import com.dmitryefremov.site.agent.repository.AgentOrderRepository;
import com.dmitryefremov.site.ai.domain.AiDirectMessage;
import com.dmitryefremov.site.ai.domain.AiServiceOrder;
import com.dmitryefremov.site.ai.repository.AiDirectMessageRepository;
import com.dmitryefremov.site.ai.repository.AiServiceOrderRepository;
import com.dmitryefremov.site.compliance.domain.DirectMessage;
import com.dmitryefremov.site.compliance.domain.ServiceOrder;
import com.dmitryefremov.site.compliance.repository.DirectMessageRepository;
import com.dmitryefremov.site.compliance.repository.ServiceOrderRepository;
import com.dmitryefremov.site.intake.domain.ClientIntake;
import com.dmitryefremov.site.intake.repository.ClientIntakeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class UserDataComplianceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private DirectMessageRepository directMessageRepository;

    @Autowired
    private ClientIntakeRepository clientIntakeRepository;

    @Autowired
    private AiServiceOrderRepository aiServiceOrderRepository;

    @Autowired
    private AiDirectMessageRepository aiDirectMessageRepository;

    @Autowired
    private AgentOrderRepository agentOrderRepository;

    @Autowired
    private AgentMessageRepository agentMessageRepository;

    @AfterEach
    void tearDown() {
        serviceOrderRepository.deleteAll();
        directMessageRepository.deleteAll();
        clientIntakeRepository.deleteAll();
        aiServiceOrderRepository.deleteAll();
        aiDirectMessageRepository.deleteAll();
        agentOrderRepository.deleteAll();
        agentMessageRepository.deleteAll();
    }

    @Test
    public void testErasureApiRemovesUserDataIncludingIntake() throws Exception {
        String email = "test@example.com";

        ServiceOrder order = new ServiceOrder();
        order.setUserId(email);
        order.setServiceType("TEST");
        order.setDetails("details");
        order.setStatus("PENDING");
        order.setCreatedAt(OffsetDateTime.now());
        serviceOrderRepository.save(order);

        DirectMessage msg = new DirectMessage();
        msg.setUserId(email);
        msg.setContent("content");
        msg.setCreatedAt(OffsetDateTime.now());
        directMessageRepository.save(msg);

        ClientIntake intake = new ClientIntake();
        intake.setClientEmail(email);
        intake.setClientName("Test User");
        intake.setWishlistPayload("Test project");
        intake.setStatus("NEW");
        intake.setCreatedAt(OffsetDateTime.now());
        intake.setUpdatedAt(OffsetDateTime.now());
        clientIntakeRepository.save(intake);

        AiServiceOrder aiOrder = new AiServiceOrder("Test User", email, "agent1", "TEST", "req", OffsetDateTime.now());
        aiServiceOrderRepository.save(aiOrder);

        AiDirectMessage aiMsg = new AiDirectMessage("Test User", email, "agent1", "content", OffsetDateTime.now());
        aiDirectMessageRepository.save(aiMsg);

        AgentOrder agentOrder = new AgentOrder("Test User", email, "TEST", "req", "NEW", OffsetDateTime.now());
        agentOrderRepository.save(agentOrder);

        AgentMessage agentMsg = new AgentMessage("Test User", email, "content", "NEW", OffsetDateTime.now());
        agentMessageRepository.save(agentMsg);

        assertEquals(1, serviceOrderRepository.findByUserId(email).size());
        assertEquals(1, directMessageRepository.findByUserId(email).size());
        assertEquals(1, clientIntakeRepository.findByClientEmail(email).size());
        assertEquals(1, aiServiceOrderRepository.findAll().size());
        assertEquals(1, aiDirectMessageRepository.findAll().size());
        assertEquals(1, agentOrderRepository.findAll().size());
        assertEquals(1, agentMessageRepository.findAll().size());

        mockMvc.perform(delete("/api/v1/compliance/erasure").param("email", email))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.userId").value(email));

        assertEquals(0, serviceOrderRepository.findByUserId(email).size(), "Service orders not completely removed");
        assertEquals(0, directMessageRepository.findByUserId(email).size(), "Direct messages not completely removed");
        assertEquals(0, clientIntakeRepository.findByClientEmail(email).size(), "Client intake not completely removed");
        assertEquals(0, aiServiceOrderRepository.findAll().size(), "AI service orders not completely removed");
        assertEquals(0, aiDirectMessageRepository.findAll().size(), "AI direct messages not completely removed");
        assertEquals(0, agentOrderRepository.findAll().size(), "Agent orders not completely removed");
        assertEquals(0, agentMessageRepository.findAll().size(), "Agent messages not completely removed");
    }
}
