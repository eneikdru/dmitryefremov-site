package com.dmitryefremov.site.contact;

import com.dmitryefremov.site.contact.domain.ContactMessage;
import com.dmitryefremov.site.contact.dto.ContactMessageRequest;
import com.dmitryefremov.site.contact.dto.ContactMessageResponse;
import com.dmitryefremov.site.contact.repository.ContactMessageRepository;
import com.dmitryefremov.site.contact.service.ContactMessageService;
import com.dmitryefremov.site.contact.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactMessageServiceTest {

    @Mock
    private ContactMessageRepository repository;

    @Mock
    private EmailService emailService;

    private ContactMessageService service;

    @BeforeEach
    void setUp() {
        service = new ContactMessageService(repository, emailService);
    }

    @Test
    void processSubmission_savesMessageAndSendsEmail() {
        ContactMessageRequest request = new ContactMessageRequest("Alice", "alice@example.com", "Test", "Hello!");

        Clock clock = Clock.fixed(Instant.parse("2026-10-24T12:00:00Z"), ZoneOffset.UTC);
        ContactMessage savedMessage = new ContactMessage("Alice", "alice@example.com", "Test", "Hello!", clock);
        when(repository.save(any(ContactMessage.class))).thenReturn(savedMessage);

        ContactMessageResponse response = service.processSubmission(request);

        assertThat(response.getSenderName()).isEqualTo("Alice");
        assertThat(response.getSenderEmail()).isEqualTo("alice@example.com");
        assertThat(response.getSubject()).isEqualTo("Test");
        assertThat(response.getContent()).isEqualTo("Hello!");
        assertThat(response.getStatus()).isEqualTo("NEW");

        verify(repository).save(any(ContactMessage.class));
        verify(emailService).sendAcknowledgement("alice@example.com", "Alice");
    }

    @Test
    void processSubmission_validatesRequiredFields() {
        ContactMessageRequest requestNoName = new ContactMessageRequest(null, "alice@example.com", "Test", "Hello!");
        assertThatThrownBy(() -> service.processSubmission(requestNoName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sender name");

        ContactMessageRequest requestNoEmail = new ContactMessageRequest("Alice", "", "Test", "Hello!");
        assertThatThrownBy(() -> service.processSubmission(requestNoEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sender email");

        ContactMessageRequest requestNoContent = new ContactMessageRequest("Alice", "alice@example.com", "Test", "   ");
        assertThatThrownBy(() -> service.processSubmission(requestNoContent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Content");
    }
}
