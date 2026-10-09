package com.dmitryefremov.site.article.dto;

import java.time.OffsetDateTime;

public class ArticleDto {
    private Long id;
    private String slug;
    private String title;
    private String summary;
    private String content;
    private OffsetDateTime publishedAt;
    private OffsetDateTime createdAt;

    public ArticleDto() {
    }

    public ArticleDto(Long id, String slug, String title, String summary, String content, OffsetDateTime publishedAt, OffsetDateTime createdAt) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.publishedAt = publishedAt;
        this.createdAt = createdAt;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
