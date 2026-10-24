package com.dmitryefremov.site.canon.domain;

import jakarta.persistence.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "canon_items", indexes = {
    @Index(name = "idx_canon_items_slug", columnList = "slug"),
    @Index(name = "idx_canon_items_domain", columnList = "domain"),
    @Index(name = "idx_canon_items_type", columnList = "type")
})
public class CanonItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private CanonType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "domain", nullable = false, length = 100)
    private CanonDomain domain;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "source", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CanonCrossReference> outgoingReferences = new ArrayList<>();

    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CanonCrossReference> incomingReferences = new ArrayList<>();

    @OneToMany(mappedBy = "canonItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CanonMediaLink> mediaLinks = new ArrayList<>();

    public CanonItem() {
        this.createdAt = OffsetDateTime.now(Clock.systemUTC());
    }

    public CanonItem(String slug, String title, CanonType type, CanonDomain domain, String summary, String content, OffsetDateTime publishedAt) {
        this(slug, title, type, domain, summary, content, publishedAt, Clock.systemUTC());
    }

    public CanonItem(String slug, String title, CanonType type, CanonDomain domain, String summary, String content, OffsetDateTime publishedAt, Clock clock) {
        this.slug = slug;
        this.title = title;
        this.type = type;
        this.domain = domain;
        this.summary = summary;
        this.content = content;
        this.publishedAt = publishedAt;
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

    public CanonType getType() {
        return type;
    }

    public void setType(CanonType type) {
        this.type = type;
    }

    public CanonDomain getDomain() {
        return domain;
    }

    public void setDomain(CanonDomain domain) {
        this.domain = domain;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OffsetDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(OffsetDateTime publishedAt) {
        this.publishedAt = publishedAt;
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

    public List<CanonCrossReference> getOutgoingReferences() {
        return outgoingReferences;
    }

    public List<CanonCrossReference> getIncomingReferences() {
        return incomingReferences;
    }

    public List<CanonMediaLink> getMediaLinks() {
        return mediaLinks;
    }

    public void addMediaLink(CanonMediaLink link) {
        mediaLinks.add(link);
        link.setCanonItem(this);
    }

    public void removeMediaLink(CanonMediaLink link) {
        mediaLinks.remove(link);
        link.setCanonItem(null);
    }

    public void addOutgoingReference(CanonCrossReference reference) {
        outgoingReferences.add(reference);
        reference.setSource(this);
    }

    public void removeOutgoingReference(CanonCrossReference reference) {
        outgoingReferences.remove(reference);
        reference.setSource(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CanonItem canonItem = (CanonItem) o;
        return Objects.equals(id, canonItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
