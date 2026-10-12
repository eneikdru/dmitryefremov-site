package com.dmitryefremov.site.compliance;

import com.dmitryefremov.site.compliance.domain.DirectMessage;
import com.dmitryefremov.site.compliance.domain.ServiceOrder;
import com.dmitryefremov.site.compliance.repository.DirectMessageRepository;
import com.dmitryefremov.site.compliance.repository.ServiceOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserDataComplianceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private DirectMessageRepository directMessageRepository;

    @BeforeEach
    void setUp() {
        serviceOrderRepository.deleteAll();
        directMessageRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/v1/compliance/export should return user orders and messages")
    void testExportUserDataApi() throws Exception {
        String userId = "agent-user-001";
        serviceOrderRepository.save(new ServiceOrder(userId, "MVP", "MVP app request", "PENDING", OffsetDateTime.now()));
        directMessageRepository.save(new DirectMessage(userId, "Hello from Claude agent", OffsetDateTime.now()));

        mockMvc.perform(get("/api/v1/compliance/export")
                        .param("userId", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(userId)))
                .andExpect(jsonPath("$.orders", hasSize(1)))
                .andExpect(jsonPath("$.orders[0].serviceType", is("MVP")))
                .andExpect(jsonPath("$.orders[0].details", is("MVP app request")))
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0].content", is("Hello from Claude agent")))
                .andExpect(jsonPath("$.exportedAt", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/v1/user-data/export alias should also return user orders and messages")
    void testExportUserDataAliasApi() throws Exception {
        String userId = "agent-user-002";
        serviceOrderRepository.save(new ServiceOrder(userId, "AUDIT", "Security audit", "PENDING", OffsetDateTime.now()));

        mockMvc.perform(get("/api/v1/user-data/export")
                        .param("userId", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(userId)))
                .andExpect(jsonPath("$.orders", hasSize(1)))
                .andExpect(jsonPath("$.orders[0].serviceType", is("AUDIT")));
    }

    @Test
    @DisplayName("DELETE /api/v1/compliance/erasure should permanently delete user data")
    void testEraseUserDataApi() throws Exception {
        String userId = "agent-user-to-erase";
        serviceOrderRepository.save(new ServiceOrder(userId, "AUTOMATION", "Bot automation", "PENDING", OffsetDateTime.now()));
        directMessageRepository.save(new DirectMessage(userId, "Direct message to erase", OffsetDateTime.now()));

        mockMvc.perform(delete("/api/v1/compliance/erasure")
                        .param("userId", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(userId)))
                .andExpect(jsonPath("$.ordersDeleted", is(1)))
                .andExpect(jsonPath("$.messagesDeleted", is(1)))
                .andExpect(jsonPath("$.erasedAt", notNullValue()));

        // Confirm that subsequent export returns empty lists for erased user
        mockMvc.perform(get("/api/v1/compliance/export")
                        .param("userId", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(userId)))
                .andExpect(jsonPath("$.orders", hasSize(0)))
                .andExpect(jsonPath("$.messages", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/v1/orders and POST /api/v1/messages should create user data")
    void testCreateOrderAndMessageApi() throws Exception {
        String userId = "ai-assistant-007";

        String orderJson = """
                {
                    "userId": "ai-assistant-007",
                    "serviceType": "MVP",
                    "details": "Custom Telegram bot MVP"
                }
                """;

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.userId", is(userId)))
                .andExpect(jsonPath("$.serviceType", is("MVP")));

        String messageJson = """
                {
                    "userId": "ai-assistant-007",
                    "content": "Can we schedule a consultation?"
                }
                """;

        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messageJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.userId", is(userId)))
                .andExpect(jsonPath("$.content", is("Can we schedule a consultation?")));
    }

    @Test
    @DisplayName("GET /api/v1/compliance/export with empty userId should return 400 Bad Request")
    void testMissingUserIdReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/compliance/export")
                        .param("userId", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("User ID cannot be null or empty")));
    }
}
