package com.dmitryefremov.site.spark.controller;

import com.dmitryefremov.site.spark.dto.TelegramWebhookResponseDto;
import com.dmitryefremov.site.spark.service.TelegramWebhookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TelegramWebhookController {

    private final TelegramWebhookService telegramWebhookService;
    private final String expectedSecretToken;

    public TelegramWebhookController(
            TelegramWebhookService telegramWebhookService,
            @Value("${telegram.webhook.secret-token:}") String expectedSecretToken) {
        this.telegramWebhookService = telegramWebhookService;
        this.expectedSecretToken = expectedSecretToken;
    }

    @PostMapping("/telegram/webhook")
    public ResponseEntity<TelegramWebhookResponseDto> receiveWebhook(
            @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false) String secretToken,
            @RequestBody String rawPayload) {

        if (expectedSecretToken != null && !expectedSecretToken.isBlank()) {
            if (secretToken == null || !expectedSecretToken.equals(secretToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }

        TelegramWebhookResponseDto response = telegramWebhookService.processWebhookPayload(rawPayload);
        return ResponseEntity.ok(response);
    }
}
