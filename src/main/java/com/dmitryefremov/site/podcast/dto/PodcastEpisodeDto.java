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
    private String youtubeUrl;
    private String telegramUrl;
    private String spotifyUrl;
    private String applePodcastsUrl;
    private String platforms;
    private String conceptLinks;
    private String metadata;
    private Integer durationSeconds;
    private OffsetDateTime publishedAt;
    private OffsetDateTime createdAt;
    private List<PodcastTimecodeDto> timecodes = new ArrayList<>();
    private List<PodcastQuoteDto> quotes = new ArrayList<>();

    public PodcastEpisodeDto() {
    }

    public PodcastEpisodeDto(Long id, String slug, String title, String thesis, String description,
                             String audioUrl, String mediaLink, Integer durationSeconds,
                             OffsetDateTime publishedAt, OffsetDateTime createdAt,
                             List<PodcastTimecodeDto> timecodes) {
        this(id, slug, title, thesis, description, audioUrl, mediaLink, null, null, null, null, null, durationSeconds, publishedAt, createdAt, timecodes);
    }

    public PodcastEpisodeDto(Long id, String slug, String title, String thesis, String description,
                             String audioUrl, String mediaLink, String youtubeUrl, String telegramUrl,
                             String spotifyUrl, String applePodcastsUrl, String platforms,
                             Integer durationSeconds, OffsetDateTime publishedAt, OffsetDateTime createdAt,
                             List<PodcastTimecodeDto> timecodes) {
        this(id, slug, title, thesis, description, audioUrl, mediaLink, youtubeUrl, telegramUrl, spotifyUrl, applePodcastsUrl, platforms, null, durationSeconds, publishedAt, createdAt, timecodes, null);
    }

    public PodcastEpisodeDto(Long id, String slug, String title, String thesis, String description,
                             String audioUrl, String mediaLink, String youtubeUrl, String telegramUrl,
                             String spotifyUrl, String applePodcastsUrl, String platforms,
                             String conceptLinks, Integer durationSeconds, OffsetDateTime publishedAt,
                             OffsetDateTime createdAt, List<PodcastTimecodeDto> timecodes,
                             List<PodcastQuoteDto> quotes) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.thesis = thesis;
        this.description = description;
        this.audioUrl = audioUrl;
        this.mediaLink = mediaLink;
        this.youtubeUrl = youtubeUrl;
        this.telegramUrl = telegramUrl;
        this.spotifyUrl = spotifyUrl;
        this.applePodcastsUrl = applePodcastsUrl;
        this.platforms = platforms;
        this.conceptLinks = conceptLinks;
        this.durationSeconds = durationSeconds;
        this.publishedAt = publishedAt;
        this.createdAt = createdAt;
        if (timecodes != null) {
            this.timecodes = timecodes;
        }
        if (quotes != null) {
            this.quotes = quotes;
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

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getTelegramUrl() {
        return telegramUrl;
    }

    public void setTelegramUrl(String telegramUrl) {
        this.telegramUrl = telegramUrl;
    }

    public String getSpotifyUrl() {
        return spotifyUrl;
    }

    public void setSpotifyUrl(String spotifyUrl) {
        this.spotifyUrl = spotifyUrl;
    }

    public String getApplePodcastsUrl() {
        return applePodcastsUrl;
    }

    public void setApplePodcastsUrl(String applePodcastsUrl) {
        this.applePodcastsUrl = applePodcastsUrl;
    }

    public String getPlatforms() {
        return platforms;
    }

    public void setPlatforms(String platforms) {
        this.platforms = platforms;
    }

    public String getConceptLinks() {
        return conceptLinks;
    }

    public void setConceptLinks(String conceptLinks) {
        this.conceptLinks = conceptLinks;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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

    public List<PodcastQuoteDto> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<PodcastQuoteDto> quotes) {
        this.quotes = quotes;
    }
}
