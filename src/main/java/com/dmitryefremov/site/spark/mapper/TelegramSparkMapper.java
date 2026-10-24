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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TelegramSparkMapper {

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#([\\p{L}\\p{N}_]+)");

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
        if (rawJsonPayload == null || rawJsonPayload.isBlank()) {
            throw new IllegalArgumentException("Payload cannot be empty");
        }

        try {
            JsonNode root = objectMapper.readTree(rawJsonPayload);
            if (root == null || !root.isObject()) {
                throw new IllegalArgumentException("Invalid Telegram payload: JSON root must be an object");
            }

            if (!root.has("update_id") || root.get("update_id").isNull()) {
                throw new IllegalArgumentException("Invalid Telegram payload: missing update_id");
            }

            JsonNode message = root.has("channel_post") ? root.get("channel_post") :
                             root.has("message") ? root.get("message") :
                             root.has("edited_channel_post") ? root.get("edited_channel_post") :
                             root.has("edited_message") ? root.get("edited_message") : null;

            if (message == null || !message.isObject()) {
                throw new IllegalArgumentException("Invalid Telegram payload: missing message or channel_post data");
            }

            Long messageId = message.has("message_id") && !message.get("message_id").isNull()
                    ? message.get("message_id").asLong()
                    : null;

            if (messageId == null) {
                throw new IllegalArgumentException("Invalid Telegram payload: missing message_id");
            }

            Long chatId = null;
            if (message.has("chat") && message.get("chat").has("id") && !message.get("chat").get("id").isNull()) {
                chatId = message.get("chat").get("id").asLong();
            }

            String content = "";
            if (message.has("text") && !message.get("text").isNull()) {
                content = message.get("text").asText();
            } else if (message.has("caption") && !message.get("caption").isNull()) {
                content = message.get("caption").asText();
            }

            if (content == null || content.isBlank()) {
                throw new IllegalArgumentException("Invalid Telegram payload: message content is empty");
            }

            OffsetDateTime publishedAt = OffsetDateTime.now(clock);
            if (message.has("date") && !message.get("date").isNull()) {
                long epochSecond = message.get("date").asLong();
                publishedAt = OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneOffset.UTC);
            } else if (message.has("edit_date") && !message.get("edit_date").isNull()) {
                long epochSecond = message.get("edit_date").asLong();
                publishedAt = OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneOffset.UTC);
            }

            String author = null;
            if (message.has("author_signature") && !message.get("author_signature").asText().isBlank()) {
                author = message.get("author_signature").asText();
            } else if (message.has("from")) {
                JsonNode from = message.get("from");
                if (from.has("username") && !from.get("username").asText().isBlank()) {
                    author = "@" + from.get("username").asText();
                } else if (from.has("first_name") && !from.get("first_name").asText().isBlank()) {
                    author = from.get("first_name").asText();
                }
            }
            if ((author == null || author.isBlank()) && message.has("chat")) {
                JsonNode chat = message.get("chat");
                if (chat.has("title") && !chat.get("title").asText().isBlank()) {
                    author = chat.get("title").asText();
                } else if (chat.has("username") && !chat.get("username").asText().isBlank()) {
                    author = "@" + chat.get("username").asText();
                }
            }

            String domain = extractDomainFromContent(content);
            String tags = extractTagsFromContent(content);

            return new Spark(messageId, chatId, content, domain, tags, author, publishedAt);
        } catch (IllegalArgumentException e) {
            throw e;
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
        if (content == null || content.isBlank()) return "";
        List<String> tags = new ArrayList<>();
        Matcher matcher = HASHTAG_PATTERN.matcher(content);
        while (matcher.find()) {
            String tag = matcher.group(1);
            if (!tags.contains(tag)) {
                tags.add(tag);
            }
        }
        return String.join(",", tags);
    }
}
