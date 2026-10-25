package com.dmitryefremov.site.contact;

import com.dmitryefremov.site.contact.domain.ContactMessage;
import com.dmitryefremov.site.contact.dto.ContactSubmissionRequest;
import com.dmitryefremov.site.contact.dto.ContactSubmissionResponse;
import com.dmitryefremov.site.contact.repository.ContactMessageRepository;
import com.dmitryefremov.site.contact.service.ContactService;
import com.dmitryefremov.site.contact.service.EmailAcknowledgmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContactServiceTest {

    @Mock
    private ContactMessageRepository contactMessageRepository;

    @Mock
    private EmailAcknowledgmentService emailAcknowledgmentService;

    @InjectMocks
    private ContactService contactService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSubmitMessage() {
        ContactSubmissionRequest request = new ContactSubmissionRequest("Test User", "test@test.com", "Subject", "Content body");

        ContactMessage savedMsg = new ContactMessage("Test User", "test@test.com", "Subject", "Content body") {
            @Override
            public Long getId() {
                return 1L;
            }
        };
        savedMsg.setCreatedAt(OffsetDateTime.now());
        savedMsg.setUpdatedAt(OffsetDateTime.now());

        when(contactMessageRepository.save(any(ContactMessage.class))).thenReturn(savedMsg);

        ContactSubmissionResponse response = contactService.submitMessage(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getSenderName()).isEqualTo("Test User");
        assertThat(response.getStatus()).isEqualTo("NEW");

        verify(emailAcknowledgmentService, times(1))
                .dispatchAcknowledgment("Test User", "test@test.com", "Subject", "Content body");
    }
}
