package com.dmitryefremov.site.spark.dto;

import com.dmitryefremov.site.spark.domain.Spark;

import java.time.OffsetDateTime;

public class SparkDto {

    private Long id;
    private String slug;
    private String title;
    private String content;
    private String domain;
    private String telegramUrl;
    private OffsetDateTime publishedAt;

    public SparkDto() {
    }

    public SparkDto(Long id, String slug, String title, String content, String domain, String telegramUrl, OffsetDateTime publishedAt) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.content = content;
        this.domain = domain;
        this.telegramUrl = telegramUrl;
        this.publishedAt = publishedAt;
    }

    public static SparkDto fromEntity(Spark entity) {
        if (entity == null) {
            return null;
        }
        return new SparkDto(
                entity.getId(),
                entity.getSlug(),
                entity.getTitle(),
                entity.getContent(),
                entity.getDomain(),
                entity.getTelegramUrl(),
                entity.getPublishedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getTelegramUrl() {
        return telegramUrl;
    }

    public void setTelegramUrl(String telegramUrl) {
        this.telegramUrl = telegramUrl;
    }

    public OffsetDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(OffsetDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
}
