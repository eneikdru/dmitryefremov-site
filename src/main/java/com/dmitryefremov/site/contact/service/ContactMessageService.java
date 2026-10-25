package com.dmitryefremov.site.contact.service;

import com.dmitryefremov.site.contact.domain.ContactMessage;
import com.dmitryefremov.site.contact.dto.ContactMessageRequest;
import com.dmitryefremov.site.contact.dto.ContactMessageResponse;
import com.dmitryefremov.site.contact.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactMessageService {

    private final ContactMessageRepository repository;
    private final EmailService emailService;

    public ContactMessageService(ContactMessageRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    @Transactional
    public ContactMessageResponse processSubmission(ContactMessageRequest request) {
        if (request.getSenderName() == null || request.getSenderName().isBlank()) {
            throw new IllegalArgumentException("Sender name is required");
        }
        if (request.getSenderEmail() == null || request.getSenderEmail().isBlank()) {
            throw new IllegalArgumentException("Sender email is required");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("Content is required");
        }

        ContactMessage message = new ContactMessage(
                request.getSenderName().trim(),
                request.getSenderEmail().trim(),
                request.getSubject() != null ? request.getSubject().trim() : null,
                request.getContent().trim()
        );

        ContactMessage saved = repository.save(message);

        emailService.sendAcknowledgement(saved.getSenderEmail(), saved.getSenderName());

        return new ContactMessageResponse(
                saved.getId(),
                saved.getSenderName(),
                saved.getSenderEmail(),
                saved.getSubject(),
                saved.getContent(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }
}
