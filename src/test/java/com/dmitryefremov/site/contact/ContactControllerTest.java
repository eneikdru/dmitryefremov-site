package com.dmitryefremov.site.contact;

import com.dmitryefremov.site.contact.controller.ContactController;
import com.dmitryefremov.site.contact.controller.ContactExceptionHandler;
import com.dmitryefremov.site.contact.dto.ContactSubmissionRequest;
import com.dmitryefremov.site.contact.dto.ContactSubmissionResponse;
import com.dmitryefremov.site.contact.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
@Import(ContactExceptionHandler.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContactService contactService;

    @Test
    void submitContactMessageReturns201() throws Exception {
        ContactSubmissionRequest request = new ContactSubmissionRequest("John Doe", "john@example.com", "Test Subject", "Test Content which is long enough");

        ContactSubmissionResponse response = new ContactSubmissionResponse(
                1L, "John Doe", "john@example.com", "Test Subject", "Test Content which is long enough",
                "NEW", OffsetDateTime.now(), OffsetDateTime.now());

        when(contactService.submitMessage(any(ContactSubmissionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.senderName").value("John Doe"))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void submitContactMessageInvalidPayloadReturns400() throws Exception {
        ContactSubmissionRequest request = new ContactSubmissionRequest("", "invalid-email", "Test Subject", "Shrt");

        mockMvc.perform(post("/api/v1/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed for contact submission payload"));
    }
}
