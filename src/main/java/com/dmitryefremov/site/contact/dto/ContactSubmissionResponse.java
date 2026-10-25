package com.dmitryefremov.site.contact.dto;

import java.time.OffsetDateTime;

public class ContactSubmissionResponse {

    private Long id;
    private String senderName;
    private String senderEmail;
    private String subject;
    private String content;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public ContactSubmissionResponse() {
    }

    public ContactSubmissionResponse(Long id, String senderName, String senderEmail, String subject, String content, String status, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.subject = subject;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
