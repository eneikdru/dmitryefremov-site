package com.dmitryefremov.site.contact.dto;

public class ContactMessageRequest {

    private String senderName;
    private String senderEmail;
    private String subject;
    private String content;

    public ContactMessageRequest() {
    }

    public ContactMessageRequest(String senderName, String senderEmail, String subject, String content) {
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
