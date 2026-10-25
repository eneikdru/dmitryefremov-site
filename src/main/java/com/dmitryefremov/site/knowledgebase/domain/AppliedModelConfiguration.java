package com.dmitryefremov.site.knowledgebase.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "applied_model_configurations")
public class AppliedModelConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concept_id")
    private KnowledgeBaseConcept concept;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(name = "version", nullable = false)
    private String version = "1.0.0";

    @Column(name = "configuration_json", nullable = false, columnDefinition = "TEXT")
    private String configurationJson;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public AppliedModelConfiguration() {
    }

    public AppliedModelConfiguration(KnowledgeBaseConcept concept, String modelName, String version, String configurationJson, Boolean isActive) {
        this.concept = concept;
        this.modelName = modelName;
        this.version = version != null ? version : "1.0.0";
        this.configurationJson = configurationJson;
        this.isActive = isActive != null ? isActive : true;
    }

    public Long getId() { return id; }

    public KnowledgeBaseConcept getConcept() { return concept; }
    public void setConcept(KnowledgeBaseConcept concept) { this.concept = concept; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getConfigurationJson() { return configurationJson; }
    public void setConfigurationJson(String configurationJson) { this.configurationJson = configurationJson; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public OffsetDateTime getCreatedAt() { return createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppliedModelConfiguration that = (AppliedModelConfiguration) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
