package com.dmitryefremov.site.canon.dto;

import com.dmitryefremov.site.canon.domain.CanonDomain;
import com.dmitryefremov.site.canon.domain.CanonType;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class CanonItemDto {

    private Long id;
    private String slug;
    private String title;
    private CanonType type;
    private CanonDomain domain;
    private String summary;
    private String content;
    private OffsetDateTime publishedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<CanonCrossReferenceDto> outgoingReferences = new ArrayList<>();
    private List<CanonCrossReferenceDto> incomingReferences = new ArrayList<>();

    public CanonItemDto() {
    }

    public CanonItemDto(Long id, String slug, String title, CanonType type, CanonDomain domain, String summary, String content, OffsetDateTime publishedAt, OffsetDateTime createdAt, OffsetDateTime updatedAt, List<CanonCrossReferenceDto> outgoingReferences, List<CanonCrossReferenceDto> incomingReferences) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.type = type;
        this.domain = domain;
        this.summary = summary;
        this.content = content;
        this.publishedAt = publishedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.outgoingReferences = outgoingReferences != null ? outgoingReferences : new ArrayList<>();
        this.incomingReferences = incomingReferences != null ? incomingReferences : new ArrayList<>();
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

    public CanonType getType() {
        return type;
    }

    public void setType(CanonType type) {
        this.type = type;
    }

    public CanonDomain getDomain() {
        return domain;
    }

    public void setDomain(CanonDomain domain) {
        this.domain = domain;
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

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<CanonCrossReferenceDto> getOutgoingReferences() {
        return outgoingReferences;
    }

    public void setOutgoingReferences(List<CanonCrossReferenceDto> outgoingReferences) {
        this.outgoingReferences = outgoingReferences;
    }

    public List<CanonCrossReferenceDto> getIncomingReferences() {
        return incomingReferences;
    }

    public void setIncomingReferences(List<CanonCrossReferenceDto> incomingReferences) {
        this.incomingReferences = incomingReferences;
    }
}
