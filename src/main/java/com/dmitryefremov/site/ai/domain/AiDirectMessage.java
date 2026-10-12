package com.dmitryefremov.site.ai.domain;

import jakarta.persistence.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "ai_direct_messages")
public class AiDirectMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "sender_email")
    private String senderEmail;

    @Column(name = "agent_identifier")
    private String agentIdentifier;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "status", nullable = false)
    private String status = "NEW";

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public AiDirectMessage() {
    }

    public AiDirectMessage(String senderName, String senderEmail, String agentIdentifier, String content) {
        this(senderName, senderEmail, agentIdentifier, content, Clock.systemUTC());
    }

    public AiDirectMessage(String senderName, String senderEmail, String agentIdentifier, String content, Clock clock) {
        this(senderName, senderEmail, agentIdentifier, content, clock != null ? OffsetDateTime.now(clock) : null);
    }

    public AiDirectMessage(String senderName, String senderEmail, String agentIdentifier, String content, OffsetDateTime timestamp) {
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.agentIdentifier = agentIdentifier;
        this.content = content;
        this.status = "NEW";
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
    }

    public Long getId() {
        return id;
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

    public String getAgentIdentifier() {
        return agentIdentifier;
    }

    public void setAgentIdentifier(String agentIdentifier) {
        this.agentIdentifier = agentIdentifier;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AiDirectMessage that = (AiDirectMessage) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
