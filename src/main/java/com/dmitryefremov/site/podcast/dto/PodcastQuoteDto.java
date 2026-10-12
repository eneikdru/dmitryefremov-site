package com.dmitryefremov.site.podcast.dto;

public class PodcastQuoteDto {
    private Long id;
    private String quoteText;
    private String speaker;
    private Integer timeOffsetSeconds;
    private String conceptLinks;
    private Integer displayOrder;

    public PodcastQuoteDto() {
    }

    public PodcastQuoteDto(Long id, String quoteText, String speaker, Integer timeOffsetSeconds, String conceptLinks, Integer displayOrder) {
        this.id = id;
        this.quoteText = quoteText;
        this.speaker = speaker;
        this.timeOffsetSeconds = timeOffsetSeconds;
        this.conceptLinks = conceptLinks;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
