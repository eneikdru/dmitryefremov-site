package com.dmitryefremov.site.knowledgebase.domain;

import jakarta.persistence.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "knowledge_base_concepts", indexes = {
    @Index(name = "idx_kb_concepts_slug", columnList = "slug"),
    @Index(name = "idx_kb_concepts_category", columnList = "category")
})
public class KnowledgeBaseConcept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "concept", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KnowledgeBaseTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "concept", cascade = CascadeType.ALL)
    private List<AppliedModelConfiguration> modelConfigurations = new ArrayList<>();

    public KnowledgeBaseConcept() {
        this.createdAt = OffsetDateTime.now(Clock.systemUTC());
    }

    public KnowledgeBaseConcept(String slug, String title, String category, String description, String content) {
        this(slug, title, category, description, content, Clock.systemUTC());
    }

    public KnowledgeBaseConcept(String slug, String title, String category, String description, String content, Clock clock) {
        this.slug = slug;
        this.title = title;
        this.category = category;
        this.description = description;
        this.content = content;
        this.createdAt = OffsetDateTime.now(clock != null ? clock : Clock.systemUTC());
    }

    public Long getId() {
        return id;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<KnowledgeBaseTag> getTags() {
        return tags;
    }

    public void addTag(KnowledgeBaseTag tag) {
        tags.add(tag);
        tag.setConcept(this);
    }

    public void removeTag(KnowledgeBaseTag tag) {
        tags.remove(tag);
        tag.setConcept(null);
    }

    public List<AppliedModelConfiguration> getModelConfigurations() {
        return modelConfigurations;
    }

    public void addModelConfiguration(AppliedModelConfiguration config) {
        modelConfigurations.add(config);
        config.setConcept(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnowledgeBaseConcept that = (KnowledgeBaseConcept) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
