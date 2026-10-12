package com.dmitryefremov.site.spark.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "sparks", indexes = {
    @Index(name = "idx_sparks_slug", columnList = "slug"),
    @Index(name = "idx_sparks_domain", columnList = "domain")
})
public class Spark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "domain", nullable = false)
    private String domain;

    @Column(name = "telegram_url")
    private String telegramUrl;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Spark() {
    }

    public Spark(String slug, String title, String content, String domain, String telegramUrl, OffsetDateTime publishedAt) {
        this.slug = slug;
        this.title = title;
        this.content = content;
        this.domain = domain;
        this.telegramUrl = telegramUrl;
        this.publishedAt = publishedAt;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getTelegramUrl() {
        return telegramUrl;
    }

    public void setTelegramUrl(String telegramUrl) {
        this.telegramUrl = telegramUrl;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spark spark = (Spark) o;
        return Objects.equals(id, spark.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
