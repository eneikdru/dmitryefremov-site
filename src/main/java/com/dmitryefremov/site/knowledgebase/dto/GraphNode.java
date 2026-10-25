package com.dmitryefremov.site.knowledgebase.dto;

import java.time.OffsetDateTime;

public class GraphNode {

    private Long id;
    private String slug;
    private String title;
    private SearchDomain domain;
    private String summary;
    private String canonicalUrl;
    private OffsetDateTime publishedAt;

    public GraphNode() {
    }

    public GraphNode(Long id, String slug, String title, SearchDomain domain, String summary, String canonicalUrl, OffsetDateTime publishedAt) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.domain = domain;
        this.summary = summary;
        this.canonicalUrl = canonicalUrl;
        this.publishedAt = publishedAt;
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

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public void setCanonicalUrl(String canonicalUrl) {
        this.canonicalUrl = canonicalUrl;
    }

    public OffsetDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(OffsetDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
}
