package com.eneik.generated.dmitryefremov.model;

import java.time.Instant;

public class PodcastEpisode {
    private String id;
    private String slug;
    private String title;
    private String summary;
    private String audioUrl;
    private Instant publishedAt;

    public PodcastEpisode() {}

    public PodcastEpisode(String id, String slug, String title, String summary, String audioUrl, Instant publishedAt) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.summary = summary;
        this.audioUrl = audioUrl;
        this.publishedAt = publishedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }
}
