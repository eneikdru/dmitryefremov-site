package com.dmitryefremov.site.contact.controller;

import com.dmitryefremov.site.contact.dto.ContactMessagePage;
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

    @GetMapping
    public ResponseEntity<ContactMessagePage> listContactMessages(
            @RequestParam(required = false) String senderEmail,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ContactMessagePage response = contactService.listMessages(senderEmail, status, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactSubmissionResponse> getContactMessageById(@PathVariable Long id) {
        ContactSubmissionResponse response = contactService.getMessageById(id);
        return ResponseEntity.ok(response);
    }
}
