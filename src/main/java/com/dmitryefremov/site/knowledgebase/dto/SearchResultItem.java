package com.dmitryefremov.site.knowledgebase.dto;

import java.time.OffsetDateTime;

public class SearchResultItem {

    private Long id;
    private String slug;
    private String title;
    private SearchDomain domain;
    private String summary;
    private String snippet;
    private String url;
    private Double relevanceScore;
    private OffsetDateTime publishedAt;
    private Integer connectedConceptCount = 0;

    public SearchResultItem() {
    }

    public SearchResultItem(Long id, String slug, String title, SearchDomain domain, String summary, String snippet, String url, Double relevanceScore, OffsetDateTime publishedAt, Integer connectedConceptCount) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.domain = domain;
        this.summary = summary;
        this.snippet = snippet;
        this.url = url;
        this.relevanceScore = relevanceScore;
        this.publishedAt = publishedAt;
        this.connectedConceptCount = connectedConceptCount != null ? connectedConceptCount : 0;
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

    public SearchDomain getDomain() {
        return domain;
    }

    public void setDomain(SearchDomain domain) {
        this.domain = domain;
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

    public Double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(Double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public OffsetDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(OffsetDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Integer getConnectedConceptCount() {
        return connectedConceptCount;
    }

    public void setConnectedConceptCount(Integer connectedConceptCount) {
        this.connectedConceptCount = connectedConceptCount;
    }
}
