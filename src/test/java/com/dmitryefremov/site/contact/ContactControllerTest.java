package com.dmitryefremov.site.contact;

import com.dmitryefremov.site.contact.controller.ContactController;
import com.dmitryefremov.site.contact.dto.ContactMessageRequest;
import com.dmitryefremov.site.contact.dto.ContactMessageResponse;
import com.dmitryefremov.site.contact.service.ContactMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContactMessageService service;

    @Test
    void submitContactMessage_returnsCreated() throws Exception {
        ContactMessageRequest request = new ContactMessageRequest("Bob", "bob@example.com", "Inquiry", "Hello");
        ContactMessageResponse response = new ContactMessageResponse(1L, "Bob", "bob@example.com", "Inquiry", "Hello", "NEW", OffsetDateTime.now());

        when(service.processSubmission(any(ContactMessageRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.senderName").value("Bob"))
                .andExpect(jsonPath("$.senderEmail").value("bob@example.com"))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void submitContactMessage_badRequestOnValidationError() throws Exception {
        ContactMessageRequest request = new ContactMessageRequest("", "bob@example.com", "Inquiry", "Hello");

        when(service.processSubmission(any(ContactMessageRequest.class))).thenThrow(new IllegalArgumentException("Invalid"));

        mockMvc.perform(post("/api/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
