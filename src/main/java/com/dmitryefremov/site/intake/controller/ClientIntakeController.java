package com.dmitryefremov.site.intake.controller;

import com.dmitryefremov.site.intake.dto.ClientIntakeRequestDto;
import com.dmitryefremov.site.intake.dto.ClientIntakeResponseDto;
import com.dmitryefremov.site.intake.service.ClientIntakeService;
import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/intake")
public class ClientIntakeController {

    private final ClientIntakeService clientIntakeService;
    private final Clock clock;

    public ClientIntakeController(ClientIntakeService clientIntakeService, Clock clock) {
        this.clientIntakeService = clientIntakeService;
        this.clock = clock;
    }

    @PostMapping("/wishlists")
    public ResponseEntity<ClientIntakeResponseDto> submitWishlist(@Valid @RequestBody ClientIntakeRequestDto requestDto) {
        ClientIntakeResponseDto response = clientIntakeService.createIntake(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
