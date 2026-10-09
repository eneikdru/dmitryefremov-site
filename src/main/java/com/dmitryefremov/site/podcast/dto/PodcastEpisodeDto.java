package com.dmitryefremov.site.podcast.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class PodcastEpisodeDto {
    private Long id;
    private String slug;
    private String title;
    private String thesis;
    private String description;
    private String audioUrl;
    private String mediaLink;
    private Integer durationSeconds;
    private OffsetDateTime publishedAt;
    private OffsetDateTime createdAt;
    private List<PodcastTimecodeDto> timecodes = new ArrayList<>();

    public PodcastEpisodeDto() {
    }

    public PodcastEpisodeDto(Long id, String slug, String title, String thesis, String description,
                             String audioUrl, String mediaLink, Integer durationSeconds,
                             OffsetDateTime publishedAt, OffsetDateTime createdAt,
                             List<PodcastTimecodeDto> timecodes) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.thesis = thesis;
        this.description = description;
        this.audioUrl = audioUrl;
        this.mediaLink = mediaLink;
        this.durationSeconds = durationSeconds;
        this.publishedAt = publishedAt;
        this.createdAt = createdAt;
        if (timecodes != null) {
            this.timecodes = timecodes;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getThesis() {
        return thesis;
    }

    public void setThesis(String thesis) {
        this.thesis = thesis;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getMediaLink() {
        return mediaLink;
    }

    public void setMediaLink(String mediaLink) {
        this.mediaLink = mediaLink;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
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

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<PodcastTimecodeDto> getTimecodes() {
        return timecodes;
    }

    public void setTimecodes(List<PodcastTimecodeDto> timecodes) {
        this.timecodes = timecodes;
    }
}
