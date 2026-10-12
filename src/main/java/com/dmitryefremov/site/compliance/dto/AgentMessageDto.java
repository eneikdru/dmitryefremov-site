package com.dmitryefremov.site.compliance.dto;

import java.time.OffsetDateTime;

public class AgentMessageDto {
    private Long id;
    private String userIdentifier;
    private String senderName;
    private String messageText;
    private OffsetDateTime createdAt;

    public AgentMessageDto() {
    }

    public AgentMessageDto(Long id, String userIdentifier, String senderName, String messageText, OffsetDateTime createdAt) {
        this.id = id;
        this.userIdentifier = userIdentifier;
        this.senderName = senderName;
        this.messageText = messageText;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
