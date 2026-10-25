package com.dmitryefremov.site.contact.service;

public interface EmailAcknowledgmentService {
    void dispatchAcknowledgment(String senderName, String senderEmail, String subject, String content);
}
