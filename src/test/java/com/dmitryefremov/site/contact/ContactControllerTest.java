package com.dmitryefremov.site.contact;

import com.dmitryefremov.site.contact.controller.ContactController;
import com.dmitryefremov.site.contact.dto.ContactRequestDto;
import com.dmitryefremov.site.contact.dto.ContactResponseDto;
import com.dmitryefremov.site.contact.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContactService contactService;

    @MockBean
    private Clock clock;

    private OffsetDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = OffsetDateTime.ofInstant(Instant.parse("2026-10-25T10:00:00Z"), ZoneOffset.UTC);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(clock.instant()).thenReturn(testTime.toInstant());
    }

    @Test
    @DisplayName("POST /api/v1/contact with valid payload returns 201 Created and response DTO")
    void testSubmitContactMessage_ValidPayload() throws Exception {
        ContactRequestDto request = new ContactRequestDto(
                "Alice Smith",
                "alice@example.com",
                "Collaboration Proposal",
                "I would like to submit a collaboration proposal for review."
        );

        ContactResponseDto responseDto = new ContactResponseDto(
                101L,
                "Alice Smith",
                "alice@example.com",
                "Collaboration Proposal",
                "I would like to submit a collaboration proposal for review.",
                "NEW",
                true,
                testTime,
                testTime
        );

        when(contactService.submitContactMessage(any(ContactRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(101)))
                .andExpect(jsonPath("$.senderName", is("Alice Smith")))
                .andExpect(jsonPath("$.senderEmail", is("alice@example.com")))
                .andExpect(jsonPath("$.subject", is("Collaboration Proposal")))
                .andExpect(jsonPath("$.content", is("I would like to submit a collaboration proposal for review.")))
                .andExpect(jsonPath("$.status", is("NEW")))
                .andExpect(jsonPath("$.acknowledgementSent", is(true)))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/contact alias endpoint returns 201 Created and response DTO")
    void testSubmitContactMessage_AliasEndpoint() throws Exception {
        ContactRequestDto request = new ContactRequestDto(
                "Bob Builder",
                "bob@example.com",
                "Inquiry",
                "Quick question regarding services."
        );

        ContactResponseDto responseDto = new ContactResponseDto(
                102L,
                "Bob Builder",
                "bob@example.com",
                "Inquiry",
                "Quick question regarding services.",
                "NEW",
                true,
                testTime,
                testTime
        );

        when(contactService.submitContactMessage(any(ContactRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(102)))
                .andExpect(jsonPath("$.senderName", is("Bob Builder")))
                .andExpect(jsonPath("$.acknowledgementSent", is(true)));
    }

    @Test
    @DisplayName("POST /api/v1/contact with invalid payload returns 400 Bad Request with validation errors")
    void testSubmitContactMessage_InvalidPayload() throws Exception {
        ContactRequestDto invalidRequest = new ContactRequestDto(
                "",
                "invalid-email-format",
                "Subject",
                ""
        );

        mockMvc.perform(post("/api/v1/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Validation failed")))
                .andExpect(jsonPath("$.errors", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @DisplayName("GET /api/v1/contact/{id} returns contact message when present")
    void testGetContactMessageById_Success() throws Exception {
        ContactResponseDto responseDto = new ContactResponseDto(
                101L,
                "Alice Smith",
                "alice@example.com",
                "Subject",
                "Content body",
                "NEW",
                true,
                testTime,
                testTime
        );

        when(contactService.getContactMessageById(101L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/contact/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(101)))
                .andExpect(jsonPath("$.senderEmail", is("alice@example.com")));
    }
}
