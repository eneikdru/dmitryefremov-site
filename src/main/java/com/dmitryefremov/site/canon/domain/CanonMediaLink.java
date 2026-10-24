package com.dmitryefremov.site.canon.domain;

import jakarta.persistence.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "canon_media_links", uniqueConstraints = {
    @UniqueConstraint(name = "uk_canon_media_links_unique", columnNames = {"canon_item_id", "media_type", "media_id", "relationship_type"})
}, indexes = {
    @Index(name = "idx_canon_media_links_canon_item", columnList = "canon_item_id"),
    @Index(name = "idx_canon_media_links_media", columnList = "media_type, media_id")
})
public class CanonMediaLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "canon_item_id", nullable = false)
    private CanonItem canonItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 50)
    private MediaType mediaType;

    @Column(name = "media_id", nullable = false)
    private Long mediaId;

    @Column(name = "relationship_type", nullable = false, length = 50)
    private String relationshipType = "REFERENCES";

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public CanonMediaLink() {
        this.createdAt = OffsetDateTime.now(Clock.systemUTC());
    }

    public CanonMediaLink(CanonItem canonItem, MediaType mediaType, Long mediaId, String relationshipType, String description) {
        this(canonItem, mediaType, mediaId, relationshipType, description, Clock.systemUTC());
    }

    public CanonMediaLink(CanonItem canonItem, MediaType mediaType, Long mediaId, String relationshipType, String description, Clock clock) {
        this.canonItem = canonItem;
        this.mediaType = mediaType;
        this.mediaId = mediaId;
        this.relationshipType = relationshipType != null ? relationshipType : "REFERENCES";
        this.description = description;
        this.createdAt = OffsetDateTime.now(clock != null ? clock : Clock.systemUTC());
    }

    public Long getId() {
        return id;
    }

    public CanonItem getCanonItem() {
        return canonItem;
    }

    public void setCanonItem(CanonItem canonItem) {
        this.canonItem = canonItem;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CanonMediaLink that = (CanonMediaLink) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
