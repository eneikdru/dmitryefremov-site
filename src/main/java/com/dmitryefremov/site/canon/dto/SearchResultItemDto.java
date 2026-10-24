package com.dmitryefremov.site.canon.dto;

import java.time.OffsetDateTime;

public class SearchResultItemDto {

    private String id;
    private String title;
    private String slug;
    private String kind; // CANON, PODCAST, ESSAY, SPARK, ARTICLE
    private String summary;
    private String snippet;
    private String url;
    private Double score;
    private OffsetDateTime publishedAt;

    public SearchResultItemDto() {
    }

    public SearchResultItemDto(String id, String title, String slug, String kind, String summary, String snippet, String url, Double score, OffsetDateTime publishedAt) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.kind = kind;
        this.summary = summary;
        this.snippet = snippet;
        this.url = url;
        this.score = score;
        this.publishedAt = publishedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public OffsetDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(OffsetDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
}
