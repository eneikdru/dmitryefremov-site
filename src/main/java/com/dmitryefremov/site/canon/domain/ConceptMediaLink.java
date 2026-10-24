package com.dmitryefremov.site.canon.domain;

import com.dmitryefremov.site.article.domain.Article;
import com.dmitryefremov.site.essay.domain.Essay;
import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.spark.domain.Spark;
import jakarta.persistence.*;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "concept_media_links", indexes = {
    @Index(name = "idx_concept_media_canon_id", columnList = "canon_item_id"),
    @Index(name = "idx_concept_media_podcast_id", columnList = "podcast_episode_id"),
    @Index(name = "idx_concept_media_essay_id", columnList = "essay_id"),
    @Index(name = "idx_concept_media_spark_id", columnList = "spark_id"),
    @Index(name = "idx_concept_media_article_id", columnList = "article_id"),
    @Index(name = "idx_concept_media_type", columnList = "media_type")
})
public class ConceptMediaLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "canon_item_id", nullable = false)
    private CanonItem canonItem;

    @Column(name = "media_type", nullable = false, length = 50)
    private String mediaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "podcast_episode_id")
    private PodcastEpisode podcastEpisode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essay_id")
    private Essay essay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spark_id")
    private Spark spark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(name = "relationship_type", nullable = false, length = 50)
    private String relationshipType = "DISCUSSES";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public ConceptMediaLink() {
        this.createdAt = OffsetDateTime.now(Clock.systemUTC());
    }

    public ConceptMediaLink(CanonItem canonItem, String mediaType, String relationshipType, String description, Clock clock) {
        this.canonItem = canonItem;
        this.mediaType = mediaType;
        this.relationshipType = relationshipType != null ? relationshipType : "DISCUSSES";
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

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public PodcastEpisode getPodcastEpisode() {
        return podcastEpisode;
    }

    public void setPodcastEpisode(PodcastEpisode podcastEpisode) {
        this.podcastEpisode = podcastEpisode;
        if (podcastEpisode != null) {
            this.mediaType = "PODCAST";
        }
    }

    public Essay getEssay() {
        return essay;
    }

    public void setEssay(Essay essay) {
        this.essay = essay;
        if (essay != null) {
            this.mediaType = "ESSAY";
        }
    }

    public Spark getSpark() {
        return spark;
    }

    public void setSpark(Spark spark) {
        this.spark = spark;
        if (spark != null) {
            this.mediaType = "SPARK";
        }
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
        if (article != null) {
            this.mediaType = "ARTICLE";
        }
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
        ConceptMediaLink that = (ConceptMediaLink) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
