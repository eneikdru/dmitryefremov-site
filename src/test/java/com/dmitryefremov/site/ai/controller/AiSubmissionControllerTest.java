package com.dmitryefremov.site.ai.controller;

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

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AiSubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AiServiceOrderRepository aiServiceOrderRepository;

    @Autowired
    private AiDirectMessageRepository aiDirectMessageRepository;

    @BeforeEach
    void setUp() {
        aiServiceOrderRepository.deleteAll();
        aiDirectMessageRepository.deleteAll();
    }

    @Test
    void createServiceOrder_returnsCreatedAndOrderDetails() throws Exception {
        Map<String, String> request = Map.of(
                "userName", "Carol",
                "userEmail", "carol@example.com",
                "agentIdentifier", "gpt-assistant",
                "serviceType", "Automation",
                "requirements", "Automate deployment pipeline"
        );

        mockMvc.perform(post("/api/v1/ai/submissions/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.userName").value("Carol"))
                .andExpect(jsonPath("$.userEmail").value("carol@example.com"))
                .andExpect(jsonPath("$.agentIdentifier").value("gpt-assistant"))
                .andExpect(jsonPath("$.serviceType").value("Automation"))
                .andExpect(jsonPath("$.requirements").value("Automate deployment pipeline"));
    }

    @Test
    void createDirectMessage_returnsCreatedAndMessageDetails() throws Exception {
        Map<String, String> request = Map.of(
                "senderName", "Dave",
                "senderEmail", "dave@example.com",
                "agentIdentifier", "claude-bot",
                "content", "Need help with AI agent setup"
        );

        mockMvc.perform(post("/api/v1/ai/submissions/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.senderName").value("Dave"))
                .andExpect(jsonPath("$.senderEmail").value("dave@example.com"))
                .andExpect(jsonPath("$.agentIdentifier").value("claude-bot"))
                .andExpect(jsonPath("$.content").value("Need help with AI agent setup"));
    }

    @Test
    void createOrder_returnsBadRequestOnInvalidServiceType() throws Exception {
        Map<String, String> request = Map.of(
                "userName", "Carol",
                "serviceType", "  "
        );

        mockMvc.perform(post("/api/v1/ai/submissions/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("Service type cannot be null or empty")));
    }
}
