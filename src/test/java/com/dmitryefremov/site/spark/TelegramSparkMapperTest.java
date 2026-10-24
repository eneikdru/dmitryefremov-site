package com.dmitryefremov.site.spark;

import com.dmitryefremov.site.spark.domain.Spark;
import com.dmitryefremov.site.spark.mapper.TelegramSparkMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TelegramSparkMapperTest {

    private TelegramSparkMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TelegramSparkMapper(new ObjectMapper());
    }

    @Test
    void testMapChannelPostPayload() {
        String jsonPayload = """
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

        Spark spark = mapper.mapTelegramPayload(jsonPayload);

        assertThat(spark.getTelegramMessageId()).isEqualTo(4096L);
        assertThat(spark.getTelegramChatId()).isEqualTo(-1001987654321L);
        assertThat(spark.getContent()).contains("Exploring agentic economy");
        assertThat(spark.getDomain()).isEqualTo("Agentic Economy (A2A)");
        assertThat(spark.getTags()).isEqualTo("a2a,strategy");
        assertThat(spark.getAuthor()).isEqualTo("Dmitry Efremov");
        assertThat(spark.getPublishedAt().toEpochSecond()).isEqualTo(1760304000L);
    }

    @Test
    void testMapMessagePayloadWithUser() {
        String jsonPayload = """
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

        Spark spark = mapper.mapTelegramPayload(jsonPayload);

        assertThat(spark.getTelegramMessageId()).isEqualTo(128L);
        assertThat(spark.getTelegramChatId()).isEqualTo(999888L);
        assertThat(spark.getDomain()).isEqualTo("Philosophy of Mind");
        assertThat(spark.getTags()).isEqualTo("philosophy,mind");
        assertThat(spark.getAuthor()).isEqualTo("@efremov_mvp");
    }

    @Test
    void testMapCaptionFieldWhenTextIsMissing() {
        String jsonPayload = """
                {
                  "update_id": 200,
                  "channel_post": {
                    "message_id": 512,
                    "chat": { "id": -100100 },
                    "date": 1760304000,
                    "caption": "Media post on autonomous engineering architecture #architecture"
                  }
                }
                """;

        Spark spark = mapper.mapTelegramPayload(jsonPayload);

        assertThat(spark.getTelegramMessageId()).isEqualTo(512L);
        assertThat(spark.getContent()).isEqualTo("Media post on autonomous engineering architecture #architecture");
        assertThat(spark.getPublishedAt().toEpochSecond()).isEqualTo(1760304000L);
        assertThat(spark.getDomain()).isEqualTo("Autonomous Engineering");
        assertThat(spark.getTags()).isEqualTo("architecture");
    }

    @Test
    void testMapUserWithFirstNameWhenUsernameMissing() {
        String jsonPayload = """
                {
                  "message_id": 77,
                  "from": {
                    "id": 123,
                    "first_name": "Alexander"
                  },
                  "date": 1760304000,
                  "text": "Pragmatic business strategy notes #strategy #mvp"
                }
                """;

        Spark spark = mapper.mapTelegramPayload(jsonPayload);

        assertThat(spark.getTelegramMessageId()).isEqualTo(77L);
        assertThat(spark.getAuthor()).isEqualTo("Alexander");
        assertThat(spark.getPublishedAt().toEpochSecond()).isEqualTo(1760304000L);
        assertThat(spark.getDomain()).isEqualTo("Pragmatic Business Strategy");
        assertThat(spark.getTags()).isEqualTo("strategy,mvp");
    }

    @Test
    void testMapInvalidJsonThrowsIllegalArgumentException() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mapper.mapTelegramPayload("invalid-json-content");
        });
    }
}
