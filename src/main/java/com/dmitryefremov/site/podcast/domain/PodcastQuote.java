package com.dmitryefremov.site.podcast.domain;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "podcast_quotes")
public class PodcastQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private PodcastEpisode episode;

    @Column(name = "quote_text", nullable = false)
    private String quoteText;

    @Column(name = "speaker")
    private String speaker;

    @Column(name = "time_offset_seconds")
    private Integer timeOffsetSeconds;

    @Column(name = "concept_links")
    private String conceptLinks;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    public PodcastQuote() {
    }

    public PodcastQuote(String quoteText, String speaker, Integer timeOffsetSeconds, String conceptLinks, Integer displayOrder) {
        this.quoteText = quoteText;
        this.speaker = speaker;
        this.timeOffsetSeconds = timeOffsetSeconds;
        this.conceptLinks = conceptLinks;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
    }

    public Long getId() {
        return id;
    }

    public PodcastEpisode getEpisode() {
        return episode;
    }

    public void setEpisode(PodcastEpisode episode) {
        this.episode = episode;
    }

    public String getQuoteText() {
        return quoteText;
    }

    public void setQuoteText(String quoteText) {
        this.quoteText = quoteText;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public Integer getTimeOffsetSeconds() {
        return timeOffsetSeconds;
    }

    public void setTimeOffsetSeconds(Integer timeOffsetSeconds) {
        this.timeOffsetSeconds = timeOffsetSeconds;
    }

    public String getConceptLinks() {
        return conceptLinks;
    }

    public void setConceptLinks(String conceptLinks) {
        this.conceptLinks = conceptLinks;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PodcastQuote that = (PodcastQuote) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
