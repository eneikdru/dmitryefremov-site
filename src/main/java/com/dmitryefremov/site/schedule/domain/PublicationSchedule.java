package com.dmitryefremov.site.schedule.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "publication_schedules")
public class PublicationSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    private PublicationPlatform platform;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PublicationStatus status;

    @Column(name = "scheduled_at", nullable = false)
    private OffsetDateTime scheduledAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public PublicationSchedule() {
    }

    public PublicationSchedule(PublicationPlatform platform, String topic, PublicationStatus status, OffsetDateTime scheduledAt) {
        validateInvariants(platform, topic, status, scheduledAt);
        this.platform = platform;
        this.topic = topic;
        this.status = status;
        this.scheduledAt = scheduledAt;
    }

    @PrePersist
    protected void onCreate() {
        validateInvariants(platform, topic, status, scheduledAt);
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        validateInvariants(platform, topic, status, scheduledAt);
        updatedAt = OffsetDateTime.now();
    }

    private void validateInvariants(PublicationPlatform platform, String topic, PublicationStatus status, OffsetDateTime scheduledAt) {
        if (platform == null) {
            throw new IllegalArgumentException("Platform must not be null");
        }
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Topic must not be null or blank");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status must not be null");
        }
        if (scheduledAt == null) {
            throw new IllegalArgumentException("Scheduled timestamp must not be null");
        }
    }

    public Long getId() {
        return id;
    }

    public PublicationPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(PublicationPlatform platform) {
        this.platform = platform;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public PublicationStatus getStatus() {
        return status;
    }

    public void setStatus(PublicationStatus status) {
        this.status = status;
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

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicationSchedule that = (PublicationSchedule) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
