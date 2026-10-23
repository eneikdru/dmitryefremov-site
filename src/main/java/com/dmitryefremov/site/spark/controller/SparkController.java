package com.dmitryefremov.site.spark.controller;

import com.dmitryefremov.site.spark.dto.SparkDto;
import com.dmitryefremov.site.spark.service.SparkService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SparkController {

    private final SparkService sparkService;

    public SparkController(SparkService sparkService) {
        this.sparkService = sparkService;
    }

    @PostMapping(value = "/sparks/webhook", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE, MediaType.ALL_VALUE})
    public ResponseEntity<SparkDto> handleTelegramWebhook(@RequestBody String rawJsonPayload) {
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
}
