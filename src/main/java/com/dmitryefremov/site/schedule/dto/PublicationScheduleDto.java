package com.dmitryefremov.site.schedule.dto;

import java.time.OffsetDateTime;

public class PublicationScheduleDto {

    private Long id;
    private String title;
    private String platform;
    private String status;
    private String topicSummary;
    private OffsetDateTime scheduledAt;
    private OffsetDateTime createdAt;

    public PublicationScheduleDto() {
    }

    public PublicationScheduleDto(Long id, String title, String platform, String status, String topicSummary, OffsetDateTime scheduledAt, OffsetDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.platform = platform;
        this.status = status;
        this.topicSummary = topicSummary;
        this.scheduledAt = scheduledAt;
        this.createdAt = createdAt;
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
