package com.dmitryefremov.site.podcast.dto;

public class EpisodeTopicDto {
    private Long id;
    private String topicName;
    private Integer startTimeSeconds;
    private Integer endTimeSeconds;
    private String description;
    private Integer displayOrder;

    public EpisodeTopicDto() {
    }

    public EpisodeTopicDto(Long id, String topicName, Integer startTimeSeconds, Integer endTimeSeconds, String description, Integer displayOrder) {
        this.id = id;
        this.topicName = topicName;
        this.startTimeSeconds = startTimeSeconds;
        this.endTimeSeconds = endTimeSeconds;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getStartTimeSeconds() {
        return startTimeSeconds;
    }

    public void setStartTimeSeconds(Integer startTimeSeconds) {
        this.startTimeSeconds = startTimeSeconds;
    }

    public Integer getEndTimeSeconds() {
        return endTimeSeconds;
    }

    public void setEndTimeSeconds(Integer endTimeSeconds) {
        this.endTimeSeconds = endTimeSeconds;
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
}
