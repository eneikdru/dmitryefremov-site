package com.dmitryefremov.site.agent.dto;

import java.time.OffsetDateTime;

public class AgentMessageResponse {
    private Long id;
    private String senderName;
    private String senderEmail;
    private String message;
    private String status;
    private OffsetDateTime createdAt;

    public AgentMessageResponse() {
    }

    public AgentMessageResponse(Long id, String senderName, String senderEmail, String message, String status, OffsetDateTime createdAt) {
        this.id = id;
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
