package com.dmitryefremov.site.knowledgebase.domain;

import jakarta.persistence.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "knowledge_base_tags", indexes = {
    @Index(name = "idx_kb_tags_tag_name", columnList = "tag_name"),
    @Index(name = "idx_kb_tags_concept_id", columnList = "concept_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_kb_tags_concept_tag", columnNames = {"concept_id", "tag_name"})
})
public class KnowledgeBaseTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concept_id", nullable = false)
    private KnowledgeBaseConcept concept;

    @Column(name = "tag_name", nullable = false, length = 100)
    private String tagName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public KnowledgeBaseTag() {
        this.createdAt = OffsetDateTime.now(Clock.systemUTC());
    }

    public KnowledgeBaseTag(KnowledgeBaseConcept concept, String tagName) {
        this(concept, tagName, Clock.systemUTC());
    }

    public KnowledgeBaseTag(KnowledgeBaseConcept concept, String tagName, Clock clock) {
        this.concept = concept;
        this.tagName = tagName;
        this.createdAt = OffsetDateTime.now(clock != null ? clock : Clock.systemUTC());
    }

    public Long getId() {
        return id;
    }

    public KnowledgeBaseConcept getConcept() {
        return concept;
    }

    public void setConcept(KnowledgeBaseConcept concept) {
        this.concept = concept;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

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
