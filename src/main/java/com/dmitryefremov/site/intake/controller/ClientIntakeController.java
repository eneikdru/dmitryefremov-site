package com.dmitryefremov.site.intake.controller;

import com.dmitryefremov.site.intake.dto.ClientIntakeRequestDto;
import com.dmitryefremov.site.intake.dto.ClientIntakeResponseDto;
import com.dmitryefremov.site.intake.dto.ErrorResponseDto;
import com.dmitryefremov.site.intake.service.ClientIntakeService;
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
@RequestMapping("/api/intake")
public class ClientIntakeController {

    private final ClientIntakeService intakeService;
    private final Clock clock;

    @Autowired
    public ClientIntakeController(ClientIntakeService intakeService,
                                  @Autowired(required = false) Clock clock) {
        this.intakeService = intakeService;
        this.clock = clock != null ? clock : (intakeService != null ? intakeService.getClock() : Clock.systemUTC());
    }

    public ClientIntakeController(ClientIntakeService intakeService) {
        this(intakeService, null);
    }

    @PostMapping
    public ResponseEntity<ClientIntakeResponseDto> submitIntake(@Valid @RequestBody ClientIntakeRequestDto request) {
        ClientIntakeResponseDto response = intakeService.submitIntake(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
                "Validation failed for intake request payload",
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
                "An unexpected error occurred while processing the intake request",
                OffsetDateTime.now(clock)
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
