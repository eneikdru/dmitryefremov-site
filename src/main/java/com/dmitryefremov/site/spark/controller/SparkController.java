package com.dmitryefremov.site.spark.controller;

import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import com.dmitryefremov.site.spark.dto.SparkPageDto;
import com.dmitryefremov.site.spark.service.SparkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.OffsetDateTime;

@RestController("siteSparkController")
@RequestMapping("/api/v1/sparks")
public class SparkController {

    private final SparkService sparkService;
    private final Clock clock;

    public SparkController(@Qualifier("siteSparkService") SparkService sparkService,
                           @Autowired(required = false) Clock clock) {
        this.sparkService = sparkService;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    @GetMapping
    public ResponseEntity<SparkPageDto> listSparks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String domain) {
        SparkPageDto result = sparkService.getSparks(page, size, domain);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getSparkBySlug(@PathVariable String slug) {
        return sparkService.getSparkBySlug(slug)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    ErrorResponseDto error = new ErrorResponseDto(
                            HttpStatus.NOT_FOUND.value(),
                            "Spark with slug '" + slug + "' was not found",
                            OffsetDateTime.now(clock)
                    );
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
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
