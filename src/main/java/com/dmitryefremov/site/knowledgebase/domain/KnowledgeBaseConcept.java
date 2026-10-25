package com.dmitryefremov.site.knowledgebase.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "knowledge_base_concepts")
public class KnowledgeBaseConcept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "concept", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<KnowledgeBaseTag> tags = new java.util.ArrayList<>();

    public KnowledgeBaseConcept() {
    }

    public KnowledgeBaseConcept(String slug, String title, String category, String description, String content) {
        this.slug = slug;
        this.title = title;
        this.category = category;
        this.description = description;
        this.content = content;
    }

    public Long getId() { return id; }

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

    public OffsetDateTime getCreatedAt() { return createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public java.util.List<KnowledgeBaseTag> getTags() { return tags; }
    public void setTags(java.util.List<KnowledgeBaseTag> tags) { this.tags = tags; }

    public void addTag(KnowledgeBaseTag tag) {
        tags.add(tag);
        tag.setConcept(this);
    }

    public void removeTag(KnowledgeBaseTag tag) {
        tags.remove(tag);
        tag.setConcept(null);
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
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
