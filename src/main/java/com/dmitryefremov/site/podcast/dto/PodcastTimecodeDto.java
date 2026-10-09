package com.dmitryefremov.site.podcast.dto;

public class PodcastTimecodeDto {
    private Long id;
    private Integer timeOffsetSeconds;
    private String title;
    private String description;
    private Integer displayOrder;

    public PodcastTimecodeDto() {
    }

    public PodcastTimecodeDto(Long id, Integer timeOffsetSeconds, String title, String description, Integer displayOrder) {
        this.id = id;
        this.timeOffsetSeconds = timeOffsetSeconds;
        this.title = title;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
