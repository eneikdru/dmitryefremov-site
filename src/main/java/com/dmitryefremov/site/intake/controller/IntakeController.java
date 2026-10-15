package com.dmitryefremov.site.intake.controller;

import com.dmitryefremov.site.intake.dto.IntakeResponseDto;
import com.dmitryefremov.site.intake.dto.IntakeSubmissionDto;
import com.dmitryefremov.site.intake.service.IntakeService;
import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/intake")
public class IntakeController {

    private final IntakeService intakeService;
    private Clock clock = Clock.systemUTC();

    public IntakeController(IntakeService intakeService) {
        this.intakeService = intakeService;
    }

    @Autowired(required = false)
    public void setClock(Clock clock) {
        if (clock != null) {
            this.clock = clock;
        }
    }

    @PostMapping
    public ResponseEntity<IntakeResponseDto> submitIntake(
            @Valid @RequestBody IntakeSubmissionDto dto,
            @RequestHeader(value = "X-A2A-Gateway", required = false) String a2aHeader) {

        if ("true".equalsIgnoreCase(a2aHeader) || "1".equalsIgnoreCase(a2aHeader)) {
            dto.setA2a(true);
        }

        IntakeResponseDto response = intakeService.submitIntake(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/audit")
    public ResponseEntity<IntakeResponseDto> submitAuditIntake(
            @Valid @RequestBody IntakeSubmissionDto dto,
            @RequestHeader(value = "X-A2A-Gateway", required = false) String a2aHeader) {
        return submitIntake(dto, a2aHeader);
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed: " + errors,
                OffsetDateTime.now(clock)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
