package com.dmitryefremov.site.knowledgebase.dto;

import java.time.OffsetDateTime;

public class AppliedModelConfigurationDto {
    private Long id;
    private Long conceptId;
    private String conceptSlug;
    private String modelName;
    private String version;
    private String configurationJson;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConceptId() { return conceptId; }
    public void setConceptId(Long conceptId) { this.conceptId = conceptId; }

    public String getConceptSlug() { return conceptSlug; }
    public void setConceptSlug(String conceptSlug) { this.conceptSlug = conceptSlug; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getConfigurationJson() { return configurationJson; }
    public void setConfigurationJson(String configurationJson) { this.configurationJson = configurationJson; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
