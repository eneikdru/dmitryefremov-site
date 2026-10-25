package com.dmitryefremov.site.spark.controller;

import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import com.dmitryefremov.site.spark.dto.SparkDto;
import com.dmitryefremov.site.spark.service.SparkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1")
public class SparkController {

    private final SparkService sparkService;
    private final String configuredSecretToken;
    private final Clock clock;

    public SparkController(
            SparkService sparkService,
            @Value("${telegram.webhook.secret-token:}") String configuredSecretToken,
            @Autowired(required = false) Clock clock) {
        this.sparkService = sparkService;
        this.configuredSecretToken = configuredSecretToken;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    @PostMapping(value = {"/telegram/webhook", "/sparks/webhook"}, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE, MediaType.ALL_VALUE})
    public ResponseEntity<?> handleTelegramWebhook(
            @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false) String secretTokenHeader,
            @RequestBody String rawJsonPayload) {
        if (configuredSecretToken != null && !configuredSecretToken.isBlank()) {
            if (secretTokenHeader == null || !configuredSecretToken.equals(secretTokenHeader)) {
                ErrorResponseDto error = new ErrorResponseDto(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized Telegram webhook request: secret token mismatch",
                        OffsetDateTime.now(clock)
                );
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
        }

        SparkDto savedSpark = sparkService.processTelegramWebhook(rawJsonPayload);
        return ResponseEntity.ok(savedSpark);
    }

    @GetMapping("/sparks")
    public ResponseEntity<Page<SparkDto>> listSparks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String domain) {
        Page<SparkDto> sparks = sparkService.getSparks(page, size, domain);
        return ResponseEntity.ok(sparks);
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
