package com.dmitryefremov.site.spark;

import com.dmitryefremov.site.spark.repository.SparkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "telegram.webhook.secret-token=expected-secret-123")
@Transactional
class TelegramWebhookSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SparkRepository sparkRepository;

    @BeforeEach
    void setUp() {
        sparkRepository.deleteAll();
    }

    @Test
    void testWebhookRejectsMissingSecretToken() throws Exception {
        String payload = """
                {
                  "update_id": 1000,
                  "channel_post": {
                    "message_id": 10,
                    "date": 1760304000,
                    "text": "Testing missing secret token"
                  }
                }
                """;

        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Unauthorized Telegram webhook request: secret token mismatch"));
    }

    @Test
    void testWebhookRejectsInvalidSecretToken() throws Exception {
        String payload = """
                {
                  "update_id": 1001,
                  "channel_post": {
                    "message_id": 11,
                    "date": 1760304000,
                    "text": "Testing invalid secret token"
                  }
                }
                """;

        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .header("X-Telegram-Bot-Api-Secret-Token", "wrong-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void testWebhookAcceptsValidSecretToken() throws Exception {
        String payload = """
                {
                  "update_id": 1002,
                  "channel_post": {
                    "message_id": 12,
                    "date": 1760304000,
                    "text": "Testing valid secret token #architecture"
                  }
                }
                """;

        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .header("X-Telegram-Bot-Api-Secret-Token", "expected-secret-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telegramMessageId").value(12));
    }
}
