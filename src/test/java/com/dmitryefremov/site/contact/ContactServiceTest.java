package com.dmitryefremov.site.contact;

import com.dmitryefremov.site.contact.domain.ContactMessage;
import com.dmitryefremov.site.contact.dto.ContactRequestDto;
import com.dmitryefremov.site.contact.dto.ContactResponseDto;
import com.dmitryefremov.site.contact.repository.ContactMessageRepository;
import com.dmitryefremov.site.contact.service.ContactEmailService;
import com.dmitryefremov.site.contact.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactMessageRepository contactMessageRepository;

    private ContactEmailService emailService;
    private ContactService contactService;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-10-25T10:00:00Z"), ZoneOffset.UTC);
        emailService = new ContactEmailService(fixedClock);
        contactService = new ContactService(contactMessageRepository, emailService, fixedClock);
    }

    @Test
    @DisplayName("submitContactMessage saves message and dispatches email acknowledgement successfully")
    void testSubmitContactMessage_Success() {
        ContactRequestDto request = new ContactRequestDto(
                "Alice Smith",
                "alice@example.com",
                "Project Inquiry",
                "Hello Dmitry, I would like to discuss a software project."
        );

        ContactMessage savedMessage = new ContactMessage(
                "Alice Smith",
                "alice@example.com",
                "Project Inquiry",
                "Hello Dmitry, I would like to discuss a software project.",
                fixedClock
        );
        // Simulate database generated ID
        try {
            var field = ContactMessage.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(savedMessage, 101L);
        } catch (Exception e) {
            fail("Failed to set ID on savedMessage: " + e.getMessage());
        }

        when(contactMessageRepository.save(any(ContactMessage.class))).thenReturn(savedMessage);

        ContactResponseDto response = contactService.submitContactMessage(request);

        assertNotNull(response);
        assertEquals(101L, response.getId());
        assertEquals("Alice Smith", response.getSenderName());
        assertEquals("alice@example.com", response.getSenderEmail());
        assertEquals("Project Inquiry", response.getSubject());
        assertEquals("Hello Dmitry, I would like to discuss a software project.", response.getContent());
        assertEquals("NEW", response.getStatus());
        assertTrue(response.isAcknowledgementSent());

        // Verify entity saved with captor
        ArgumentCaptor<ContactMessage> captor = ArgumentCaptor.forClass(ContactMessage.class);
        verify(contactMessageRepository).save(captor.capture());
        ContactMessage captured = captor.getValue();
        assertEquals("Alice Smith", captured.getSenderName());
        assertEquals("alice@example.com", captured.getSenderEmail());

        // Verify email acknowledgement dispatched in ContactEmailService
        List<ContactEmailService.DispatchedEmail> dispatched = emailService.getDispatchedEmails();
        assertEquals(1, dispatched.size());
        assertEquals("alice@example.com", dispatched.getFirst().getRecipientEmail());
        assertEquals("Alice Smith", dispatched.getFirst().getRecipientName());
        assertTrue(dispatched.getFirst().getSubject().contains("Project Inquiry"));
    }

    @Test
    @DisplayName("submitContactMessage uses default subject when subject is blank")
    void testSubmitContactMessage_DefaultSubject() {
        ContactRequestDto request = new ContactRequestDto(
                "Bob Johnson",
                "bob@example.com",
                "",
                "Asking a quick question."
        );

        ContactMessage savedMessage = new ContactMessage(
                "Bob Johnson",
                "bob@example.com",
                "Direct Contact Form Submission",
                "Asking a quick question.",
                fixedClock
        );

        when(contactMessageRepository.save(any(ContactMessage.class))).thenReturn(savedMessage);

        ContactResponseDto response = contactService.submitContactMessage(request);

        assertNotNull(response);
        assertEquals("Direct Contact Form Submission", response.getSubject());
        verify(contactMessageRepository).save(any(ContactMessage.class));
    }

    @Test
    @DisplayName("submitContactMessage throws IllegalArgumentException when request is null")
    void testSubmitContactMessage_NullRequest() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                contactService.submitContactMessage(null)
        );
        assertEquals("Contact request body cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("getContactMessageById returns message when found")
    void testGetContactMessageById_Found() {
        ContactMessage message = new ContactMessage(
                "Charlie",
                "charlie@example.com",
                "Hello",
                "Content here",
                fixedClock
        );
        when(contactMessageRepository.findById(1L)).thenReturn(Optional.of(message));

        ContactResponseDto response = contactService.getContactMessageById(1L);

        assertNotNull(response);
        assertEquals("Charlie", response.getSenderName());
        assertEquals("charlie@example.com", response.getSenderEmail());
    }

    @Test
    @DisplayName("getContactMessageById throws IllegalArgumentException when not found")
    void testGetContactMessageById_NotFound() {
        when(contactMessageRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                contactService.getContactMessageById(999L)
        );
    }
}
