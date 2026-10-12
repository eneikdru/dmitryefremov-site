package com.dmitryefremov.site.canon.domain;

import jakarta.persistence.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "canon_cross_references", uniqueConstraints = {
    @UniqueConstraint(name = "uk_canon_xref_source_target_rel", columnNames = {"source_id", "target_id", "relationship_type"})
}, indexes = {
    @Index(name = "idx_canon_xref_source", columnList = "source_id"),
    @Index(name = "idx_canon_xref_target", columnList = "target_id")
})
public class CanonCrossReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private CanonItem source;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_id", nullable = false)
    private CanonItem target;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false, length = 50)
    private CanonRelationshipType relationshipType;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public CanonCrossReference() {
        this.createdAt = OffsetDateTime.now(Clock.systemUTC());
    }

    public CanonCrossReference(CanonItem source, CanonItem target, CanonRelationshipType relationshipType, String description) {
        this(source, target, relationshipType, description, Clock.systemUTC());
    }

    public CanonCrossReference(CanonItem source, CanonItem target, CanonRelationshipType relationshipType, String description, Clock clock) {
        this.source = source;
        this.target = target;
        this.relationshipType = relationshipType;
        this.description = description;
        this.createdAt = OffsetDateTime.now(clock != null ? clock : Clock.systemUTC());
    }

    public Long getId() {
        return id;
    }

    public CanonItem getSource() {
        return source;
    }

    public void setSource(CanonItem source) {
        this.source = source;
    }

    public CanonItem getTarget() {
        return target;
    }

    public void setTarget(CanonItem target) {
        this.target = target;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CanonCrossReference that = (CanonCrossReference) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
