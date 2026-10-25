package com.dmitryefremov.site.contact.controller;

import com.dmitryefremov.site.contact.dto.ErrorResponseDto;
import com.dmitryefremov.site.contact.dto.ErrorResponseDto.ValidationErrorDto;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = "com.dmitryefremov.site.contact.controller")
public class ContactExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ContactExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation failed for contact submission payload", ex);
        List<ValidationErrorDto> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
                    return new ValidationErrorDto(fieldName, error.getDefaultMessage());
                })
                .collect(Collectors.toList());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed for contact submission payload",
                OffsetDateTime.now(),
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception ex) {
        log.error("Internal server error during contact processing", ex);
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                OffsetDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
