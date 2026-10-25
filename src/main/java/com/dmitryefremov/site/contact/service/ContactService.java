package com.dmitryefremov.site.contact.service;

import com.dmitryefremov.site.contact.domain.ContactMessage;
import com.dmitryefremov.site.contact.dto.ContactRequestDto;
import com.dmitryefremov.site.contact.dto.ContactResponseDto;
import com.dmitryefremov.site.contact.repository.ContactMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
public class ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    private final ContactMessageRepository contactMessageRepository;
    private final ContactEmailService emailService;
    private final Clock clock;

    @Autowired
    public ContactService(ContactMessageRepository contactMessageRepository,
                          ContactEmailService emailService,
                          @Autowired(required = false) Clock clock) {
        this.contactMessageRepository = contactMessageRepository;
        this.emailService = emailService;
        this.clock = clock != null ? clock : (emailService != null ? emailService.getClock() : Clock.systemUTC());
    }

    public ContactService(ContactMessageRepository contactMessageRepository, ContactEmailService emailService) {
        this(contactMessageRepository, emailService, Clock.systemUTC());
    }

    @Transactional
    public ContactResponseDto submitContactMessage(ContactRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Contact request body cannot be null");
        }

        String subject = (request.getSubject() != null && !request.getSubject().isBlank())
                ? request.getSubject().trim()
                : "Direct Contact Form Submission";

        ContactMessage message = new ContactMessage(
                request.getSenderName().trim(),
                request.getSenderEmail().trim(),
                subject,
                request.getContent().trim(),
                clock
        );

        ContactMessage saved = contactMessageRepository.save(message);
        logger.info("Saved contact message with id: {} from email: {}", saved.getId(), saved.getSenderEmail());

        boolean emailSent = emailService.dispatchAcknowledgement(
                saved.getSenderEmail(),
                saved.getSenderName(),
                saved.getSubject(),
                saved.getContent()
        );

        return mapToResponseDto(saved, emailSent);
    }

    @Transactional(readOnly = true)
    public ContactResponseDto getContactMessageById(Long id) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contact message not found with id: " + id));
        return mapToResponseDto(message, true);
    }

    public Clock getClock() {
        return clock;
    }

    private ContactResponseDto mapToResponseDto(ContactMessage message, boolean emailSent) {
        return new ContactResponseDto(
                message.getId(),
                message.getSenderName(),
                message.getSenderEmail(),
                message.getSubject(),
                message.getContent(),
                message.getStatus(),
                emailSent,
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}
