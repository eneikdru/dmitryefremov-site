package com.dmitryefremov.site.knowledgebase.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "knowledge_base_tags")
public class KnowledgeBaseTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concept_id", nullable = false)
    private KnowledgeBaseConcept concept;

    @Column(name = "tag_name", nullable = false)
    private String tagName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public KnowledgeBaseTag() {
    }

    public KnowledgeBaseTag(KnowledgeBaseConcept concept, String tagName) {
        this.concept = concept;
        this.tagName = tagName;
    }

    public Long getId() { return id; }

    public KnowledgeBaseConcept getConcept() { return concept; }
    public void setConcept(KnowledgeBaseConcept concept) { this.concept = concept; }

    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName; }

    public OffsetDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnowledgeBaseTag that = (KnowledgeBaseTag) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
