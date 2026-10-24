package com.dmitryefremov.site.canon.dto;

import java.time.OffsetDateTime;

public class ConceptMediaLinkDto {

    private Long id;
    private Long canonItemId;
    private String canonItemSlug;
    private String canonItemTitle;
    private String mediaType;
    private Long podcastEpisodeId;
    private String podcastEpisodeSlug;
    private String podcastEpisodeTitle;
    private Long essayId;
    private String essaySlug;
    private String essayTitle;
    private Long sparkId;
    private String sparkContent;
    private Long articleId;
    private String articleSlug;
    private String articleTitle;
    private String relationshipType;
    private String description;
    private OffsetDateTime createdAt;

    public ConceptMediaLinkDto() {
    }

    public ConceptMediaLinkDto(Long id, Long canonItemId, String canonItemSlug, String canonItemTitle,
                               String mediaType, Long podcastEpisodeId, String podcastEpisodeSlug, String podcastEpisodeTitle,
                               Long essayId, String essaySlug, String essayTitle,
                               Long sparkId, String sparkContent,
                               Long articleId, String articleSlug, String articleTitle,
                               String relationshipType, String description, OffsetDateTime createdAt) {
        this.id = id;
        this.canonItemId = canonItemId;
        this.canonItemSlug = canonItemSlug;
        this.canonItemTitle = canonItemTitle;
        this.mediaType = mediaType;
        this.podcastEpisodeId = podcastEpisodeId;
        this.podcastEpisodeSlug = podcastEpisodeSlug;
        this.podcastEpisodeTitle = podcastEpisodeTitle;
        this.essayId = essayId;
        this.essaySlug = essaySlug;
        this.essayTitle = essayTitle;
        this.sparkId = sparkId;
        this.sparkContent = sparkContent;
        this.articleId = articleId;
        this.articleSlug = articleSlug;
        this.articleTitle = articleTitle;
        this.relationshipType = relationshipType;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCanonItemId() {
        return canonItemId;
    }

    public void setCanonItemId(Long canonItemId) {
        this.canonItemId = canonItemId;
    }

    public String getCanonItemSlug() {
        return canonItemSlug;
    }

    public void setCanonItemSlug(String canonItemSlug) {
        this.canonItemSlug = canonItemSlug;
    }

    public String getCanonItemTitle() {
        return canonItemTitle;
    }

    public void setCanonItemTitle(String canonItemTitle) {
        this.canonItemTitle = canonItemTitle;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Long getPodcastEpisodeId() {
        return podcastEpisodeId;
    }

    public void setPodcastEpisodeId(Long podcastEpisodeId) {
        this.podcastEpisodeId = podcastEpisodeId;
    }

    public String getPodcastEpisodeSlug() {
        return podcastEpisodeSlug;
    }

    public void setPodcastEpisodeSlug(String podcastEpisodeSlug) {
        this.podcastEpisodeSlug = podcastEpisodeSlug;
    }

    public String getPodcastEpisodeTitle() {
        return podcastEpisodeTitle;
    }

    public void setPodcastEpisodeTitle(String podcastEpisodeTitle) {
        this.podcastEpisodeTitle = podcastEpisodeTitle;
    }

    public Long getEssayId() {
        return essayId;
    }

    public void setEssayId(Long essayId) {
        this.essayId = essayId;
    }

    public String getEssaySlug() {
        return essaySlug;
    }

    public void setEssaySlug(String essaySlug) {
        this.essaySlug = essaySlug;
    }

    public String getEssayTitle() {
        return essayTitle;
    }

    public void setEssayTitle(String essayTitle) {
        this.essayTitle = essayTitle;
    }

    public Long getSparkId() {
        return sparkId;
    }

    public void setSparkId(Long sparkId) {
        this.sparkId = sparkId;
    }

    public String getSparkContent() {
        return sparkContent;
    }

    public void setSparkContent(String sparkContent) {
        this.sparkContent = sparkContent;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getArticleSlug() {
        return articleSlug;
    }

    public void setArticleSlug(String articleSlug) {
        this.articleSlug = articleSlug;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
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

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
