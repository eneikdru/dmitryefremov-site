package com.dmitryefremov.site.compliance.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "agent_messages", indexes = {
    @Index(name = "idx_agent_messages_user_identifier", columnList = "user_identifier")
})
public class AgentMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_identifier", nullable = false)
    private String userIdentifier;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "message_text", nullable = false)
    private String messageText;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public AgentMessage() {
    }

    public AgentMessage(String userIdentifier, String senderName, String messageText, OffsetDateTime createdAt) {
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
