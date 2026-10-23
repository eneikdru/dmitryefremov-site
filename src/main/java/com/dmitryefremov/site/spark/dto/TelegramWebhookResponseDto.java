package com.dmitryefremov.site.spark.dto;

import com.dmitryefremov.site.spark.domain.Spark;

import java.time.OffsetDateTime;

public class TelegramWebhookResponseDto {

    private boolean success;
    private String message;
    private Long sparkId;

    private Long id;
    private Long telegramMessageId;
    private Long telegramChatId;
    private String content;
    private String domain;
    private String tags;
    private String author;
    private OffsetDateTime publishedAt;
    private OffsetDateTime createdAt;

    public TelegramWebhookResponseDto() {
    }

    public TelegramWebhookResponseDto(boolean success, String message, Spark spark) {
        this.success = success;
        this.message = message;
        if (spark != null) {
            this.sparkId = spark.getId();
            this.id = spark.getId();
            this.telegramMessageId = spark.getTelegramMessageId();
            this.telegramChatId = spark.getTelegramChatId();
            this.content = spark.getContent();
            this.domain = spark.getDomain();
            this.tags = spark.getTags();
            this.author = spark.getAuthor();
            this.publishedAt = spark.getPublishedAt();
            this.createdAt = spark.getCreatedAt();
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getSparkId() {
        return sparkId;
    }

    public void setSparkId(Long sparkId) {
        this.sparkId = sparkId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
