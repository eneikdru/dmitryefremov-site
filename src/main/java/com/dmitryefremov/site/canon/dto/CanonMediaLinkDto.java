package com.dmitryefremov.site.canon.dto;

import com.dmitryefremov.site.canon.domain.MediaType;

import java.time.OffsetDateTime;

public class CanonMediaLinkDto {

    private Long id;
    private Long canonItemId;
    private String canonSlug;
    private String canonTitle;
    private MediaType mediaType;
    private Long mediaId;
    private String relationshipType;
    private String description;
    private OffsetDateTime createdAt;

    public CanonMediaLinkDto() {
    }

    public CanonMediaLinkDto(Long id, Long canonItemId, String canonSlug, String canonTitle, MediaType mediaType, Long mediaId, String relationshipType, String description, OffsetDateTime createdAt) {
        this.id = id;
        this.canonItemId = canonItemId;
        this.canonSlug = canonSlug;
        this.canonTitle = canonTitle;
        this.mediaType = mediaType;
        this.mediaId = mediaId;
        this.relationshipType = relationshipType;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCanonItemId() {
        return canonItemId;
    }

    public void setCanonItemId(Long canonItemId) {
        this.canonItemId = canonItemId;
    }

    public String getCanonSlug() {
        return canonSlug;
    }

    public void setCanonSlug(String canonSlug) {
        this.canonSlug = canonSlug;
    }

    public String getCanonTitle() {
        return canonTitle;
    }

    public void setCanonTitle(String canonTitle) {
        this.canonTitle = canonTitle;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
