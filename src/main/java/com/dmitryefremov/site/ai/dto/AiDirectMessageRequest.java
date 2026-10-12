package com.dmitryefremov.site.ai.dto;

public class AiDirectMessageRequest {

    private String senderName;
    private String senderEmail;
    private String agentIdentifier;
    private String content;

    public AiDirectMessageRequest() {
    }

    public AiDirectMessageRequest(String senderName, String senderEmail, String agentIdentifier, String content) {
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.agentIdentifier = agentIdentifier;
        this.content = content;
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
}
