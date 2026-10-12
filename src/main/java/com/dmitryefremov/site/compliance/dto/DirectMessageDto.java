package com.dmitryefremov.site.compliance.dto;

import java.time.OffsetDateTime;

public class DirectMessageDto {

    private Long id;
    private String userId;
    private String content;
    private OffsetDateTime createdAt;

    public DirectMessageDto() {
    }

    public DirectMessageDto(Long id, String userId, String content, OffsetDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
