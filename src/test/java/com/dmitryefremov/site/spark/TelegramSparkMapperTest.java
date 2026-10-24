package com.dmitryefremov.site.spark;

import com.dmitryefremov.site.spark.domain.Spark;
import com.dmitryefremov.site.spark.mapper.TelegramSparkMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class TelegramSparkMapperTest {

    private TelegramSparkMapper mapper;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.ofEpochSecond(1760300000L), ZoneOffset.UTC);
        mapper = new TelegramSparkMapper(new ObjectMapper(), fixedClock);
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
    void testMapEditedChannelPostWithCyrillicHashtagsAndChatTitleAuthorFallback() {
        String jsonPayload = """
                {
                  "update_id": 999111,
                  "edited_channel_post": {
                    "message_id": 512,
                    "chat": {
                      "id": -100555,
                      "title": "Ефремов MVP Channel",
                      "type": "channel"
                    },
                    "edit_date": 1760305000,
                    "caption": "Новый тезис про архитектуру систем и AI. #архитектура #ии_агенты"
                  }
                }
                """;

        Spark spark = mapper.mapTelegramPayload(jsonPayload);

        assertThat(spark.getTelegramMessageId()).isEqualTo(512L);
        assertThat(spark.getTelegramChatId()).isEqualTo(-100555L);
        assertThat(spark.getContent()).contains("Новый тезис про архитектуру");
        assertThat(spark.getAuthor()).isEqualTo("Ефремов MVP Channel");
        assertThat(spark.getTags()).isEqualTo("архитектура,ии_агенты");
        assertThat(spark.getPublishedAt().toEpochSecond()).isEqualTo(1760305000L);
    }

    @Test
    void testMapMissingDateUsesFixedClock() {
        String jsonPayload = """
                {
                  "update_id": 888222,
                  "edited_message": {
                    "message_id": 1024,
                    "chat": {
                      "id": 777
                    },
                    "text": "Pragmatic business strategy and MVP execution."
                  }
                }
                """;

        Spark spark = mapper.mapTelegramPayload(jsonPayload);

        assertThat(spark.getTelegramMessageId()).isEqualTo(1024L);
        assertThat(spark.getTelegramChatId()).isEqualTo(777L);
        assertThat(spark.getDomain()).isEqualTo("Pragmatic Business Strategy");
        assertThat(spark.getPublishedAt().toEpochSecond()).isEqualTo(1760300000L);
    }

    @Test
    void testMapNullOrBlankPayloadThrowsIllegalArgumentException() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> mapper.mapTelegramPayload(null));
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> mapper.mapTelegramPayload("   "));
    }

    @Test
    void testMapPayloadWithoutTextOrCaptionThrowsIllegalArgumentException() {
        String jsonPayload = """
                {
                  "update_id": 111,
                  "message": {
                    "message_id": 999,
                    "chat": { "id": 123 }
                  }
                }
                """;
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> mapper.mapTelegramPayload(jsonPayload));
    }
}
