package com.dmitryefremov.site.podcast.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "podcast_episodes")
public class PodcastEpisode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "thesis")
    private String thesis;

    @Column(name = "description")
    private String description;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "media_link")
    private String mediaLink;

    @Column(name = "youtube_url")
    private String youtubeUrl;

    @Column(name = "telegram_url")
    private String telegramUrl;

    @Column(name = "spotify_url")
    private String spotifyUrl;

    @Column(name = "apple_podcasts_url")
    private String applePodcastsUrl;

    @Column(name = "platforms")
    private String platforms;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "episode", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC, timeOffsetSeconds ASC")
    private List<PodcastTimecode> timecodes = new ArrayList<>();

    public PodcastEpisode() {
    }

    public PodcastEpisode(String slug, String title, String thesis, String description, String audioUrl, String mediaLink, Integer durationSeconds, OffsetDateTime publishedAt) {
        this.slug = slug;
        this.title = title;
        this.thesis = thesis;
        this.description = description;
        this.audioUrl = audioUrl;
        this.mediaLink = mediaLink;
        this.durationSeconds = durationSeconds;
        this.publishedAt = publishedAt;
    }

    public PodcastEpisode(String slug, String title, String thesis, String description, String audioUrl, String mediaLink,
                          String youtubeUrl, String telegramUrl, String spotifyUrl, String applePodcastsUrl, String platforms,
                          Integer durationSeconds, OffsetDateTime publishedAt) {
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
        this.durationSeconds = durationSeconds;
        this.publishedAt = publishedAt;
    }

    public void addTimecode(PodcastTimecode timecode) {
        timecodes.add(timecode);
        timecode.setEpisode(this);
    }

    public void removeTimecode(PodcastTimecode timecode) {
        timecodes.remove(timecode);
        timecode.setEpisode(null);
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

    public List<PodcastTimecode> getTimecodes() {
        return timecodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PodcastEpisode that = (PodcastEpisode) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
