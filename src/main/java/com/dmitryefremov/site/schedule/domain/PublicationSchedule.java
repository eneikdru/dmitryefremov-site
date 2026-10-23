package com.dmitryefremov.site.schedule.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "publication_schedules")
public class PublicationSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 50)
    private String platform;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "topic_summary")
    private String topicSummary;

    @Column(name = "scheduled_at")
    private OffsetDateTime scheduledAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public PublicationSchedule() {
    }

    public PublicationSchedule(Long id, String title, String platform, String status, String topicSummary, OffsetDateTime scheduledAt, OffsetDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.platform = platform;
        this.status = status;
        this.topicSummary = topicSummary;
        this.scheduledAt = scheduledAt;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTopicSummary() {
        return topicSummary;
    }

    public void setTopicSummary(String topicSummary) {
        this.topicSummary = topicSummary;
    }

    public OffsetDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(OffsetDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
