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
    @DisplayName("GET /api/v1/compliance/export with email parameter should export user data")
    void testExportUserDataWithEmailParamApi() throws Exception {
        String email = "compliance-user@example.com";
        serviceOrderRepository.save(new ServiceOrder(email, "CONSULTING", "Strategy consultation", "PENDING", OffsetDateTime.now()));
        directMessageRepository.save(new DirectMessage(email, "Message via email param", OffsetDateTime.now()));

        mockMvc.perform(get("/api/v1/compliance/export")
                        .param("email", email)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(email)))
                .andExpect(jsonPath("$.orders", hasSize(1)))
                .andExpect(jsonPath("$.orders[0].serviceType", is("CONSULTING")))
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0].content", is("Message via email param")));
    }

    @Test
    @DisplayName("DELETE /api/v1/compliance/erasure with email parameter should erase user data")
    void testEraseUserDataWithEmailParamApi() throws Exception {
        String email = "erasure-user@example.com";
        serviceOrderRepository.save(new ServiceOrder(email, "MVP", "App build", "PENDING", OffsetDateTime.now()));
        directMessageRepository.save(new DirectMessage(email, "Delete message", OffsetDateTime.now()));

        mockMvc.perform(delete("/api/v1/compliance/erasure")
                        .param("email", email)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(email)))
                .andExpect(jsonPath("$.ordersDeleted", is(1)))
                .andExpect(jsonPath("$.messagesDeleted", is(1)));

        mockMvc.perform(get("/api/v1/compliance/export")
                        .param("email", email)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orders", hasSize(0)))
                .andExpect(jsonPath("$.messages", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/v1/compliance/export with empty userId should return 400 Bad Request")
    void testMissingUserIdReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/compliance/export")
                        .param("userId", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("User ID or email address cannot be null or empty")));
    }

    @Test
    @DisplayName("GET /api/v1/compliance/export should isolate and return exactly test user's data and no one else's")
    void testExportUserDataIsolation() throws Exception {
        String userA = "user-a-export-isolation";
        String userB = "user-b-export-isolation";

        serviceOrderRepository.save(new ServiceOrder(userA, "MVP", "User A order", "PENDING", OffsetDateTime.now()));
        directMessageRepository.save(new DirectMessage(userA, "User A message", OffsetDateTime.now()));

        serviceOrderRepository.save(new ServiceOrder(userB, "AUDIT", "User B order", "PENDING", OffsetDateTime.now()));
        directMessageRepository.save(new DirectMessage(userB, "User B message", OffsetDateTime.now()));

        mockMvc.perform(get("/api/v1/compliance/export")
                        .param("userId", userA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(userA)))
                .andExpect(jsonPath("$.orders", hasSize(1)))
                .andExpect(jsonPath("$.orders[0].details", is("User A order")))
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0].content", is("User A message")));
    }

    @Test
    @DisplayName("DELETE /api/v1/compliance/erasure should erase target user's data while preserving other users' data")
    void testErasureUserDataIsolation() throws Exception {
        String userToErase = "user-to-erase-isolation";
        String userToKeep = "user-to-keep-isolation";

        serviceOrderRepository.save(new ServiceOrder(userToErase, "AUTOMATION", "Erase order", "PENDING", OffsetDateTime.now()));
        directMessageRepository.save(new DirectMessage(userToErase, "Erase message", OffsetDateTime.now()));

        serviceOrderRepository.save(new ServiceOrder(userToKeep, "MVP", "Keep order", "PENDING", OffsetDateTime.now()));
        directMessageRepository.save(new DirectMessage(userToKeep, "Keep message", OffsetDateTime.now()));

        mockMvc.perform(delete("/api/v1/compliance/erasure")
                        .param("userId", userToErase)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(userToErase)))
                .andExpect(jsonPath("$.ordersDeleted", is(1)))
                .andExpect(jsonPath("$.messagesDeleted", is(1)));

        // Confirm erased user has no remaining data
        mockMvc.perform(get("/api/v1/compliance/export")
                        .param("userId", userToErase)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orders", hasSize(0)))
                .andExpect(jsonPath("$.messages", hasSize(0)));

        // Confirm other user's data remains intact
        mockMvc.perform(get("/api/v1/compliance/export")
                        .param("userId", userToKeep)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orders", hasSize(1)))
                .andExpect(jsonPath("$.orders[0].details", is("Keep order")))
                .andExpect(jsonPath("$.messages", hasSize(1)))
                .andExpect(jsonPath("$.messages[0].content", is("Keep message")));
    }
}
