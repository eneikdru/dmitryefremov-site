package com.dmitryefremov.site.ai.notification;

import java.time.OffsetDateTime;
import java.util.Objects;

public class AuthorNotification {

    private final String recipientEmail;
    private final String submissionType;
    private final String senderName;
    private final String senderEmail;
    private final String agentIdentifier;
    private final String content;
    private final OffsetDateTime dispatchedAt;

    public AuthorNotification(String recipientEmail,
                              String submissionType,
                              String senderName,
                              String senderEmail,
                              String agentIdentifier,
                              String content,
                              OffsetDateTime dispatchedAt) {
        this.recipientEmail = recipientEmail;
        this.submissionType = submissionType;
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.agentIdentifier = agentIdentifier;
        this.content = content;
        this.dispatchedAt = dispatchedAt;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getSubmissionType() {
        return submissionType;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getAgentIdentifier() {
        return agentIdentifier;
    }

    public String getContent() {
        return content;
    }

    public OffsetDateTime getDispatchedAt() {
        return dispatchedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorNotification that = (AuthorNotification) o;
        return Objects.equals(recipientEmail, that.recipientEmail) &&
                Objects.equals(submissionType, that.submissionType) &&
                Objects.equals(senderName, that.senderName) &&
                Objects.equals(senderEmail, that.senderEmail) &&
                Objects.equals(agentIdentifier, that.agentIdentifier) &&
                Objects.equals(content, that.content) &&
                Objects.equals(dispatchedAt, that.dispatchedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipientEmail, submissionType, senderName, senderEmail, agentIdentifier, content, dispatchedAt);
    }

    @Override
    public String toString() {
        return "AuthorNotification{" +
                "recipientEmail='" + recipientEmail + '\'' +
                ", submissionType='" + submissionType + '\'' +
                ", senderName='" + senderName + '\'' +
                ", senderEmail='" + senderEmail + '\'' +
                ", agentIdentifier='" + agentIdentifier + '\'' +
                ", content='" + content + '\'' +
                ", dispatchedAt=" + dispatchedAt +
                '}';
    }
}
