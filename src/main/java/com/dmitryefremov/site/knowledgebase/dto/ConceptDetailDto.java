package com.dmitryefremov.site.knowledgebase.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class ConceptDetailDto {
    private Long id;
    private String slug;
    private String title;
    private String category;
    private String description;
    private String content;
    private List<String> tags;
    private List<AppliedModelConfigurationDto> appliedModels;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public List<AppliedModelConfigurationDto> getAppliedModels() { return appliedModels; }
    public void setAppliedModels(List<AppliedModelConfigurationDto> appliedModels) { this.appliedModels = appliedModels; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
