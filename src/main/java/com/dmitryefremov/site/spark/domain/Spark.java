package com.dmitryefremov.site.spark.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "sparks", indexes = {
    @Index(name = "idx_sparks_telegram_message_id", columnList = "telegram_message_id"),
    @Index(name = "idx_sparks_published_at", columnList = "published_at"),
    @Index(name = "idx_sparks_domain", columnList = "domain")
})
public class Spark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_message_id", unique = true)
    private Long telegramMessageId;

    @Column(name = "telegram_chat_id")
    private Long telegramChatId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "domain", length = 100)
    private String domain;

    @Column(name = "tags", length = 500)
    private String tags;

    @Column(name = "author", length = 255)
    private String author;

    @Column(name = "published_at", nullable = false)
    private OffsetDateTime publishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Spark() {
    }

    public Spark(Long telegramMessageId, Long telegramChatId, String content, String domain, String tags, String author, OffsetDateTime publishedAt) {
        this.telegramMessageId = telegramMessageId;
        this.telegramChatId = telegramChatId;
        this.content = content;
        this.domain = domain;
        this.tags = tags;
        this.author = author;
        this.publishedAt = publishedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getTelegramMessageId() {
        return telegramMessageId;
    }

    public void setTelegramMessageId(Long telegramMessageId) {
        this.telegramMessageId = telegramMessageId;
    }

    public Long getTelegramChatId() {
        return telegramChatId;
    }

    public void setTelegramChatId(Long telegramChatId) {
        this.telegramChatId = telegramChatId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public OffsetDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(OffsetDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spark spark = (Spark) o;
        return Objects.equals(id, spark.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
