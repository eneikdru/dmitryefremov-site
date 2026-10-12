package com.dmitryefremov.site.spark.controller;

import com.dmitryefremov.site.Application;
import com.dmitryefremov.site.spark.domain.Spark;
import com.dmitryefremov.site.spark.repository.SparkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class, properties = {
    "telegram.webhook.secret-token=test_secret_token_123"
})
@AutoConfigureMockMvc
class TelegramWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SparkRepository sparkRepository;

    @BeforeEach
    void setUp() {
        sparkRepository.deleteAll();
    }

    @Test
    void testReceiveWebhook_ValidSecretToken_PersistsSpark() throws Exception {
        String payload = """
            {
              "update_id": 999123,
              "channel_post": {
                "message_id": 5001,
                "chat": {
                  "id": -100123456789,
                  "title": "Dmitry Efremov | MVP Pragmatics",
                  "type": "channel"
                },
                "date": 1700000000,
                "text": "Quantized thought on #autonomous engineering and system boundaries.",
                "author_signature": "Dmitry Efremov"
              }
            }
            """;

        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .header("X-Telegram-Bot-Api-Secret-Token", "test_secret_token_123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Spark ingested successfully"))
                .andExpect(jsonPath("$.sparkId").exists());

        Optional<Spark> savedOpt = sparkRepository.findByTelegramMessageId(5001L);
        assertThat(savedOpt).isPresent();
        Spark saved = savedOpt.get();
        assertThat(saved.getContent()).contains("Quantized thought");
        assertThat(saved.getDomain()).isEqualTo("Autonomous Engineering");
        assertThat(saved.getAuthor()).isEqualTo("Dmitry Efremov");
        assertThat(saved.getTelegramChatId()).isEqualTo(-100123456789L);
    }

    @Test
    void testReceiveWebhook_InvalidSecretToken_ReturnsUnauthorized() throws Exception {
        String payload = """
            {
              "channel_post": {
                "message_id": 5002,
                "text": "Unauthorized attempt"
              }
            }
            """;

        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .header("X-Telegram-Bot-Api-Secret-Token", "wrong_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());

        assertThat(sparkRepository.findByTelegramMessageId(5002L)).isEmpty();
    }

    @Test
    void testReceiveWebhook_MissingSecretToken_ReturnsUnauthorized() throws Exception {
        String payload = """
            {
              "channel_post": {
                "message_id": 5003,
                "text": "Missing header attempt"
              }
            }
            """;

        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());

        assertThat(sparkRepository.findByTelegramMessageId(5003L)).isEmpty();
    }

    @Test
    void testReceiveWebhook_DuplicateMessageId_UpdatesExistingSpark() throws Exception {
        String payload1 = """
            {
              "channel_post": {
                "message_id": 5004,
                "text": "Original thought on #a2a agentic economy.",
                "date": 1700000000
              }
            }
            """;

        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .header("X-Telegram-Bot-Api-Secret-Token", "test_secret_token_123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Spark ingested successfully"));

        String payload2 = """
            {
              "channel_post": {
                "message_id": 5004,
                "text": "Updated thought on #a2a agentic economy with edits.",
                "date": 1700000050
              }
            }
            """;

        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .header("X-Telegram-Bot-Api-Secret-Token", "test_secret_token_123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Spark updated successfully"));

        assertThat(sparkRepository.count()).isEqualTo(1);
        Spark updated = sparkRepository.findByTelegramMessageId(5004L).orElseThrow();
        assertThat(updated.getContent()).isEqualTo("Updated thought on #a2a agentic economy with edits.");
    }
}
