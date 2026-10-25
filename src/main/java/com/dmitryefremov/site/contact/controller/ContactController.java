package com.dmitryefremov.site.contact.controller;

import com.dmitryefremov.site.contact.dto.ContactRequestDto;
import com.dmitryefremov.site.contact.dto.ContactResponseDto;
import com.dmitryefremov.site.contact.service.ContactService;
import com.dmitryefremov.site.intake.dto.ErrorResponseDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ContactController {

    private final ContactService contactService;
    private final Clock clock;

    @Autowired
    public ContactController(ContactService contactService,
                             @Autowired(required = false) Clock clock) {
        this.contactService = contactService;
        this.clock = clock != null ? clock : (contactService != null ? contactService.getClock() : Clock.systemUTC());
    }

    public ContactController(ContactService contactService) {
        this(contactService, null);
    }

    @PostMapping({"/api/v1/contact", "/api/contact"})
    public ResponseEntity<ContactResponseDto> submitContactMessage(@Valid @RequestBody ContactRequestDto request) {
        ContactResponseDto response = contactService.submitContactMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping({"/api/v1/contact/{id}", "/api/contact/{id}"})
    public ResponseEntity<ContactResponseDto> getContactMessageById(@PathVariable("id") Long id) {
        ContactResponseDto response = contactService.getContactMessageById(id);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ErrorResponseDto.ValidationErrorDto> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorResponseDto.ValidationErrorDto(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed for contact request payload",
                OffsetDateTime.now(clock),
                validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                OffsetDateTime.now(clock)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred while processing the contact submission",
                OffsetDateTime.now(clock)
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
