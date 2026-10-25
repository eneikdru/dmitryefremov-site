package com.dmitryefremov.site.contact.controller;

import com.dmitryefremov.site.contact.dto.ContactSubmissionRequest;
import com.dmitryefremov.site.contact.dto.ContactSubmissionResponse;
import com.dmitryefremov.site.contact.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<ContactSubmissionResponse> submitContactMessage(
            @Valid @RequestBody ContactSubmissionRequest request) {
        ContactSubmissionResponse response = contactService.submitMessage(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
