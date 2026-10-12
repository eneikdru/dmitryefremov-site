package com.dmitryefremov.site.ai.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AiDirectMessageRequestDto {

    @NotBlank(message = "senderName must not be blank")
    private String senderName;

    @NotBlank(message = "senderEmail must not be blank")
    @Email(message = "senderEmail must be a valid email address")
    private String senderEmail;

    private String agentIdentifier;

    @NotBlank(message = "content must not be blank")
    private String content;

    public AiDirectMessageRequestDto() {
    }

    public AiDirectMessageRequestDto(String senderName, String senderEmail, String agentIdentifier, String content) {
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
