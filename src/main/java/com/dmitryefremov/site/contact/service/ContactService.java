package com.dmitryefremov.site.contact.service;

import com.dmitryefremov.site.contact.domain.ContactMessage;
import com.dmitryefremov.site.contact.dto.ContactMessagePage;
import com.dmitryefremov.site.contact.dto.ContactSubmissionRequest;
import com.dmitryefremov.site.contact.dto.ContactSubmissionResponse;
import com.dmitryefremov.site.contact.exception.ResourceNotFoundException;
import com.dmitryefremov.site.contact.repository.ContactMessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public ContactMessagePage listMessages(String senderEmail, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ContactMessage> messagePage;

        if (senderEmail != null && !senderEmail.isEmpty()) {
            messagePage = contactMessageRepository.findBySenderEmail(senderEmail, pageable);
        } else if (status != null && !status.isEmpty()) {
            messagePage = contactMessageRepository.findByStatus(status, pageable);
        } else {
            messagePage = contactMessageRepository.findAll(pageable);
        }

        List<ContactSubmissionResponse> content = messagePage.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new ContactMessagePage(
                content,
                messagePage.getNumber(),
                messagePage.getSize(),
                messagePage.getTotalElements(),
                messagePage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public ContactSubmissionResponse getMessageById(Long id) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact message not found with id: " + id));
        return toDto(message);
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
