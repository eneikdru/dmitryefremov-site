package com.dmitryefremov.site.canon.dto;

import com.dmitryefremov.site.canon.domain.CanonRelationshipType;
import java.time.OffsetDateTime;

public class CanonCrossReferenceDto {

    private Long id;
    private String sourceSlug;
    private String sourceTitle;
    private String targetSlug;
    private String targetTitle;
    private CanonRelationshipType relationshipType;
    private String description;
    private OffsetDateTime createdAt;

    public CanonCrossReferenceDto() {
    }

    public CanonCrossReferenceDto(Long id, String sourceSlug, String sourceTitle, String targetSlug, String targetTitle, CanonRelationshipType relationshipType, String description, OffsetDateTime createdAt) {
        this.id = id;
        this.sourceSlug = sourceSlug;
        this.sourceTitle = sourceTitle;
        this.targetSlug = targetSlug;
        this.targetTitle = targetTitle;
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

    public String getSourceSlug() {
        return sourceSlug;
    }

    public void setSourceSlug(String sourceSlug) {
        this.sourceSlug = sourceSlug;
    }

    public String getSourceTitle() {
        return sourceTitle;
    }

    public void setSourceTitle(String sourceTitle) {
        this.sourceTitle = sourceTitle;
    }

    public String getTargetSlug() {
        return targetSlug;
    }

    public void setTargetSlug(String targetSlug) {
        this.targetSlug = targetSlug;
    }

    public String getTargetTitle() {
        return targetTitle;
    }

    public void setTargetTitle(String targetTitle) {
        this.targetTitle = targetTitle;
    }

    public CanonRelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(CanonRelationshipType relationshipType) {
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
