package com.dmitryefremov.site.contact.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContactRequestDto {

    @NotBlank(message = "Sender name is required")
    @Size(max = 255, message = "Sender name cannot exceed 255 characters")
    @JsonAlias({"name", "sender_name"})
    private String senderName;

    @NotBlank(message = "Sender email is required")
    @Email(message = "Must be a valid email address")
    @Size(max = 255, message = "Sender email cannot exceed 255 characters")
    @JsonAlias({"email", "sender_email", "clientEmail", "client_email"})
    private String senderEmail;

    @Size(max = 255, message = "Subject cannot exceed 255 characters")
    private String subject;

    @NotBlank(message = "Content is required")
    @Size(max = 10000, message = "Content cannot exceed 10000 characters")
    @JsonAlias({"message", "body", "text", "narrative"})
    private String content;

    public ContactRequestDto() {
    }

    public ContactRequestDto(String senderName, String senderEmail, String subject, String content) {
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.subject = subject;
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
}
