package com.dmitryefremov.site.knowledgebase.dto;

public class GraphLink {

    private Long id;
    private String sourceSlug;
    private String sourceTitle;
    private String targetSlug;
    private String targetTitle;
    private String relationshipType;
    private String description;

    public GraphLink() {
    }

    public GraphLink(Long id, String sourceSlug, String sourceTitle, String targetSlug, String targetTitle, String relationshipType, String description) {
        this.id = id;
        this.sourceSlug = sourceSlug;
        this.sourceTitle = sourceTitle;
        this.targetSlug = targetSlug;
        this.targetTitle = targetTitle;
        this.relationshipType = relationshipType;
        this.description = description;
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
}
