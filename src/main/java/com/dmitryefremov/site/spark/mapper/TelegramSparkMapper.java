package com.dmitryefremov.site.spark.mapper;

import com.dmitryefremov.site.spark.domain.Spark;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class TelegramSparkMapper {

    private final ObjectMapper objectMapper;
    private final Clock clock;

    public TelegramSparkMapper(ObjectMapper objectMapper) {
        this(objectMapper, Clock.systemUTC());
    }

    @Autowired
    public TelegramSparkMapper(ObjectMapper objectMapper, @Autowired(required = false) Clock clock) {
        this.objectMapper = objectMapper;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    public Spark mapTelegramPayload(String rawJsonPayload) {
        try {
            JsonNode root = objectMapper.readTree(rawJsonPayload);
            JsonNode message = root.has("channel_post") ? root.get("channel_post") :
                             root.has("message") ? root.get("message") : root;

            Long messageId = message.has("message_id") ? message.get("message_id").asLong() : null;

            Long chatId = null;
            if (message.has("chat") && message.get("chat").has("id")) {
                chatId = message.get("chat").get("id").asLong();
            }

            String content = "";
            if (message.has("text")) {
                content = message.get("text").asText();
            } else if (message.has("caption")) {
                content = message.get("caption").asText();
            }

            OffsetDateTime publishedAt = OffsetDateTime.now(clock);
            if (message.has("date")) {
                long epochSecond = message.get("date").asLong();
                publishedAt = OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneOffset.UTC);
            }

            String author = null;
            if (message.has("author_signature")) {
                author = message.get("author_signature").asText();
            } else if (message.has("from")) {
                JsonNode from = message.get("from");
                if (from.has("username")) {
                    author = "@" + from.get("username").asText();
                } else if (from.has("first_name")) {
                    author = from.get("first_name").asText();
                }
            }

            String domain = extractDomainFromContent(content);
            String tags = extractTagsFromContent(content);

            Spark spark = new Spark(messageId, chatId, content, domain, tags, author, publishedAt);
            return spark;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse Telegram payload: " + e.getMessage(), e);
        }
    }

    private String extractDomainFromContent(String content) {
        if (content == null) return "General";
        String lower = content.toLowerCase();
        if (lower.contains("autonomous engineering") || lower.contains("engineering") || lower.contains("architecture")) {
            return "Autonomous Engineering";
        } else if (lower.contains("agentic economy") || lower.contains("a2a") || lower.contains("agent")) {
            return "Agentic Economy (A2A)";
        } else if (lower.contains("business") || lower.contains("strategy") || lower.contains("mvp")) {
            return "Pragmatic Business Strategy";
        } else if (lower.contains("philosophy") || lower.contains("mind") || lower.contains("qualia") || lower.contains("canon")) {
            return "Philosophy of Mind";
        }
        return "General";
    }

    private String extractTagsFromContent(String content) {
        if (content == null) return "";
        StringBuilder tags = new StringBuilder();
        String[] words = content.split("\\s+");
        for (String word : words) {
            if (word.startsWith("#") && word.length() > 1) {
                if (tags.length() > 0) {
                    tags.append(",");
                }
                tags.append(word.substring(1));
            }
        }
        return tags.toString();
    }
}
