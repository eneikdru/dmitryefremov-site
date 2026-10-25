package com.dmitryefremov.site.contact.service;

import com.dmitryefremov.site.contact.domain.ContactMessage;
import com.dmitryefremov.site.contact.dto.ContactSubmissionRequest;
import com.dmitryefremov.site.contact.dto.ContactSubmissionResponse;
import com.dmitryefremov.site.contact.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactService {

    private final ContactMessageRepository contactMessageRepository;
    private final EmailAcknowledgmentService emailAcknowledgmentService;

    public ContactService(ContactMessageRepository contactMessageRepository, EmailAcknowledgmentService emailAcknowledgmentService) {
        this.contactMessageRepository = contactMessageRepository;
        this.emailAcknowledgmentService = emailAcknowledgmentService;
    }

    @Transactional
    public ContactSubmissionResponse submitMessage(ContactSubmissionRequest request) {
        ContactMessage contactMessage = new ContactMessage(
                request.getSenderName(),
                request.getSenderEmail(),
                request.getSubject(),
                request.getContent()
        );

        ContactMessage savedMessage = contactMessageRepository.save(contactMessage);

        emailAcknowledgmentService.dispatchAcknowledgment(
                savedMessage.getSenderName(),
                savedMessage.getSenderEmail(),
                savedMessage.getSubject(),
                savedMessage.getContent()
        );

        return toDto(savedMessage);
    }

    private ContactSubmissionResponse toDto(ContactMessage message) {
        return new ContactSubmissionResponse(
                message.getId(),
                message.getSenderName(),
                message.getSenderEmail(),
                message.getSubject(),
                message.getContent(),
                message.getStatus(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}
