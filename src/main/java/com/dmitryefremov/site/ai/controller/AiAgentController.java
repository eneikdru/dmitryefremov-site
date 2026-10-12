package com.dmitryefremov.site.ai.controller;

import com.dmitryefremov.site.ai.dto.AiDirectMessageRequestDto;
import com.dmitryefremov.site.ai.dto.AiDirectMessageResponseDto;
import com.dmitryefremov.site.ai.dto.AiServiceOrderRequestDto;
import com.dmitryefremov.site.ai.dto.AiServiceOrderResponseDto;
import com.dmitryefremov.site.ai.service.AiAgentService;
import com.dmitryefremov.site.ai.service.AiRateLimiterService;
import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ai")
public class AiAgentController {

    private final AiAgentService aiAgentService;
    private final AiRateLimiterService aiRateLimiterService;
    private final Clock clock;

    public AiAgentController(AiAgentService aiAgentService,
                             AiRateLimiterService aiRateLimiterService,
                             Clock clock) {
        this.aiAgentService = aiAgentService;
        this.aiRateLimiterService = aiRateLimiterService;
        this.clock = clock;
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@Valid @RequestBody AiServiceOrderRequestDto dto,
                                         HttpServletRequest request) {
        String clientKey = getClientKey(request, dto.getAgentIdentifier());
        if (!aiRateLimiterService.isAllowed(clientKey)) {
            ErrorResponseDto error = new ErrorResponseDto(
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    "Rate limit exceeded. Please try again later.",
                    OffsetDateTime.now(clock)
            );
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
        }

        AiServiceOrderResponseDto response = aiAgentService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/messages")
    public ResponseEntity<?> createMessage(@Valid @RequestBody AiDirectMessageRequestDto dto,
                                           HttpServletRequest request) {
        String clientKey = getClientKey(request, dto.getAgentIdentifier());
        if (!aiRateLimiterService.isAllowed(clientKey)) {
            ErrorResponseDto error = new ErrorResponseDto(
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    "Rate limit exceeded. Please try again later.",
                    OffsetDateTime.now(clock)
            );
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
        }

        AiDirectMessageResponseDto response = aiAgentService.createMessage(dto);
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

    private String getClientKey(HttpServletRequest request, String agentIdentifier) {
        if (agentIdentifier != null && !agentIdentifier.isBlank()) {
            return agentIdentifier;
        }
        String remoteAddr = request.getRemoteAddr();
        return remoteAddr != null ? remoteAddr : "anonymous";
    }
}
