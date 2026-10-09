package com.dmitryefremov.site.podcast.domain;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "podcast_timecodes")
public class PodcastTimecode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private PodcastEpisode episode;

    @Column(name = "time_offset_seconds", nullable = false)
    private Integer timeOffsetSeconds;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    public PodcastTimecode() {
    }

    public PodcastTimecode(Integer timeOffsetSeconds, String title, String description, Integer displayOrder) {
        this.timeOffsetSeconds = timeOffsetSeconds;
        this.title = title;
        this.description = description;
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

    public Integer getTimeOffsetSeconds() {
        return timeOffsetSeconds;
    }

    public void setTimeOffsetSeconds(Integer timeOffsetSeconds) {
        this.timeOffsetSeconds = timeOffsetSeconds;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        PodcastTimecode that = (PodcastTimecode) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
