package com.dmitryefremov.site.contact;

import com.dmitryefremov.site.contact.domain.ContactMessage;
import com.dmitryefremov.site.contact.dto.ContactMessagePage;
import com.dmitryefremov.site.contact.dto.ContactSubmissionRequest;
import com.dmitryefremov.site.contact.dto.ContactSubmissionResponse;
import com.dmitryefremov.site.contact.exception.ResourceNotFoundException;
import com.dmitryefremov.site.contact.repository.ContactMessageRepository;
import com.dmitryefremov.site.contact.service.ContactService;
import com.dmitryefremov.site.contact.service.EmailAcknowledgmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    void testGetMessageById() {
        ContactMessage msg = new ContactMessage("User", "u@test.com", null, "content") {
            @Override
            public Long getId() {
                return 42L;
            }
        };

        when(contactMessageRepository.findById(42L)).thenReturn(Optional.of(msg));

        ContactSubmissionResponse response = contactService.getMessageById(42L);
        assertThat(response.getId()).isEqualTo(42L);
        assertThat(response.getSenderName()).isEqualTo("User");
    }

    @Test
    void testGetMessageByIdNotFound() {
        when(contactMessageRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contactService.getMessageById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void testListMessages() {
        ContactMessage msg1 = new ContactMessage("User1", "u1@test.com", null, "content1");
        ContactMessage msg2 = new ContactMessage("User2", "u2@test.com", null, "content2");
        Page<ContactMessage> msgPage = new PageImpl<>(List.of(msg1, msg2), PageRequest.of(0, 10), 2);

        when(contactMessageRepository.findAll(any(Pageable.class))).thenReturn(msgPage);

        ContactMessagePage page = contactService.listMessages(null, null, 0, 10);
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(2);
    }
}
