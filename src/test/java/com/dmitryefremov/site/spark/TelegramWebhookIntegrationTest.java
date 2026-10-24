package com.dmitryefremov.site.spark;

import com.dmitryefremov.site.spark.domain.Spark;
import com.dmitryefremov.site.spark.repository.SparkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.telegram.webhook-secret=test-secret-token-123")
@AutoConfigureMockMvc
@Transactional
class TelegramWebhookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SparkRepository sparkRepository;

    @BeforeEach
    void setUp() {
        sparkRepository.deleteAll();
    }

    @Test
    void testTelegramWebhookProcessingAndPersistenceForChannelPost() throws Exception {
        String channelPostPayload = """
                {
                  "update_id": 987654321,
                  "channel_post": {
                    "message_id": 4096,
                    "sender_chat": {
                      "id": -1001987654321,
                      "title": "Dmitry Efremov Thought Stream",
                      "type": "channel"
                    },
                    "chat": {
                      "id": -1001987654321,
                      "title": "Dmitry Efremov Thought Stream",
                      "type": "channel"
                    },
                    "date": 1760304000,
                    "text": "Exploring agentic economy and A2A protocols for autonomous agents. #a2a #strategy",
                    "author_signature": "Dmitry Efremov"
                  }
                }
                """;

        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .header("X-Telegram-Bot-Api-Secret-Token", "test-secret-token-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(channelPostPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telegramMessageId").value(4096))
                .andExpect(jsonPath("$.telegramChatId").value(-1001987654321L))
                .andExpect(jsonPath("$.domain").value("Agentic Economy (A2A)"))
                .andExpect(jsonPath("$.tags").value("a2a,strategy"))
                .andExpect(jsonPath("$.author").value("Dmitry Efremov"));

        // Verify entity persisted in database
        Optional<Spark> savedOpt = sparkRepository.findByTelegramMessageId(4096L);
        assertThat(savedOpt).isPresent();
        Spark saved = savedOpt.get();
        assertThat(saved.getContent()).contains("Exploring agentic economy");
        assertThat(saved.getDomain()).isEqualTo("Agentic Economy (A2A)");

        // Verify endpoint GET /api/v1/sparks surfaces the saved spark message
        mockMvc.perform(get("/api/v1/sparks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].telegramMessageId").value(4096))
                .andExpect(jsonPath("$.content[0].content").value(saved.getContent()));
    }

    @Test
    void testTelegramWebhookProcessingAndPersistenceForUserMessage() throws Exception {
        String messagePayload = """
                {
                  "update_id": 123456789,
                  "message": {
                    "message_id": 128,
                    "from": {
                      "id": 999888,
                      "is_bot": false,
                      "first_name": "Dmitry",
                      "username": "efremov_mvp"
                    },
                    "chat": {
                      "id": 999888,
                      "first_name": "Dmitry",
                      "username": "efremov_mvp",
                      "type": "private"
                    },
                    "date": 1760304000,
                    "text": "Philosophy of mind and qualia in extended mind theory. #philosophy #mind"
                  }
                }
                """;

        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .header("X-Telegram-Bot-Api-Secret-Token", "test-secret-token-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(messagePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telegramMessageId").value(128))
                .andExpect(jsonPath("$.author").value("@efremov_mvp"))
                .andExpect(jsonPath("$.domain").value("Philosophy of Mind"));

        // Verify retrieval with domain filter
        mockMvc.perform(get("/api/v1/sparks")
                        .param("domain", "Philosophy of Mind"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].telegramMessageId").value(128))
                .andExpect(jsonPath("$.content[0].domain").value("Philosophy of Mind"));
    }

    @Test
    void testIdempotentWebhookProcessingUpdatesExistingRecord() throws Exception {
        String initialPayload = """
                {
                  "update_id": 10001,
                  "channel_post": {
                    "message_id": 5000,
                    "chat": { "id": -100100 },
                    "date": 1760304000,
                    "text": "Draft initial autonomous engineering insight #engineering"
                  }
                }
                """;

        String updatedPayload = """
                {
                  "update_id": 10002,
                  "channel_post": {
                    "message_id": 5000,
                    "chat": { "id": -100100 },
                    "date": 1760304100,
                    "text": "Final updated autonomous engineering insight #engineering #architecture"
                  }
                }
                """;

        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .header("X-Telegram-Bot-Api-Secret-Token", "test-secret-token-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(initialPayload))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .header("X-Telegram-Bot-Api-Secret-Token", "test-secret-token-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPayload))
                .andExpect(status().isOk());

        assertThat(sparkRepository.count()).isEqualTo(1);
        Optional<Spark> sparkOpt = sparkRepository.findByTelegramMessageId(5000L);
        assertThat(sparkOpt).isPresent();
        assertThat(sparkOpt.get().getContent()).contains("Final updated autonomous engineering insight");
        assertThat(sparkOpt.get().getTags()).isEqualTo("engineering,architecture");
    }

    @Test
    void testInvalidWebhookPayloadReturnsBadRequest() throws Exception {
        String invalidPayload = """
                {
                  "update_id": 99999,
                  "message": {
                    "message_id": 7777
                  }
                }
                """;

        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .header("X-Telegram-Bot-Api-Secret-Token", "test-secret-token-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Telegram update message or post contains no text or caption content"));
    }

    @Test
    void testMalformedJsonPayloadReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .header("X-Telegram-Bot-Api-Secret-Token", "test-secret-token-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid_json_body"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testUnauthorizedWebhookRequestRejectedWhenSecretTokenMissingOrInvalid() throws Exception {
        String payload = """
                {
                  "update_id": 12345,
                  "message": {
                    "message_id": 99,
                    "text": "Unauthorized text message"
                  }
                }
                """;

        // Missing header
        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Unauthorized or invalid Telegram webhook secret token"));

        // Invalid header
        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .header("X-Telegram-Bot-Api-Secret-Token", "wrong-secret-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Unauthorized or invalid Telegram webhook secret token"));
    }
}
