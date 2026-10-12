package com.dmitryefremov.site.ai.controller;

import com.dmitryefremov.site.ai.domain.AiDirectMessage;
import com.dmitryefremov.site.ai.domain.AiServiceOrder;
import com.dmitryefremov.site.ai.dto.AiDirectMessageRequest;
import com.dmitryefremov.site.ai.dto.AiServiceOrderRequest;
import com.dmitryefremov.site.ai.service.AiSubmissionService;
import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/ai/submissions")
public class AiSubmissionController {

    private final AiSubmissionService aiSubmissionService;
    private final Clock clock;

    @Autowired
    public AiSubmissionController(@Qualifier("siteAiSubmissionService") AiSubmissionService aiSubmissionService,
                                 @Autowired(required = false) Clock clock) {
        this.aiSubmissionService = aiSubmissionService;
        this.clock = clock != null ? clock : (aiSubmissionService != null ? aiSubmissionService.getClock() : Clock.systemUTC());
    }

    public AiSubmissionController(AiSubmissionService aiSubmissionService) {
        this(aiSubmissionService, null);
    }

    @PostMapping("/orders")
    public ResponseEntity<AiServiceOrder> createServiceOrder(@RequestBody AiServiceOrderRequest request) {
        AiServiceOrder order = aiSubmissionService.createServiceOrder(
                request.getUserName(),
                request.getUserEmail(),
                request.getAgentIdentifier(),
                request.getServiceType(),
                request.getRequirements()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PostMapping("/messages")
    public ResponseEntity<AiDirectMessage> createDirectMessage(@RequestBody AiDirectMessageRequest request) {
        AiDirectMessage message = aiSubmissionService.createDirectMessage(
                request.getSenderName(),
                request.getSenderEmail(),
                request.getAgentIdentifier(),
                request.getContent()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                OffsetDateTime.now(clock)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
