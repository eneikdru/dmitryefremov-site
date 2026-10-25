package com.dmitryefremov.site.contact.controller;

import com.dmitryefremov.site.contact.dto.ContactMessageRequest;
import com.dmitryefremov.site.contact.dto.ContactMessageResponse;
import com.dmitryefremov.site.contact.service.ContactMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactMessageService contactMessageService;

    public ContactController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @PostMapping
    public ResponseEntity<ContactMessageResponse> submitContactMessage(@RequestBody ContactMessageRequest request) {
        try {
            ContactMessageResponse response = contactMessageService.processSubmission(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
