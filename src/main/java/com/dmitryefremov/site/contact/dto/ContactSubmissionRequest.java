package com.dmitryefremov.site.contact.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContactSubmissionRequest {

    @NotBlank(message = "Sender name is required")
    @Size(min = 2, max = 255, message = "Sender name must be between 2 and 255 characters")
    private String senderName;

    @NotBlank(message = "Sender email is required")
    @Email(message = "Sender email must be a valid email address")
    @Size(max = 255, message = "Sender email must be at most 255 characters")
    private String senderEmail;

    @Size(max = 255, message = "Subject must be at most 255 characters")
    private String subject;

    @NotBlank(message = "Content is required")
    @Size(min = 5, max = 5000, message = "Content must be between 5 and 5000 characters")
    private String content;

    public ContactSubmissionRequest() {
    }

    public ContactSubmissionRequest(String senderName, String senderEmail, String subject, String content) {
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
