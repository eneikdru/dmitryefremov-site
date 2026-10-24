package com.dmitryefremov.site.essay.controller;

import com.dmitryefremov.site.essay.dto.EssayDto;
import com.dmitryefremov.site.essay.dto.EssayPageDto;
import com.dmitryefremov.site.essay.service.EssayService;
import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.OffsetDateTime;

@RestController("siteEssayController")
@RequestMapping("/api/v1/essays")
public class EssayController {

    private final EssayService essayService;
    private final Clock clock;

    public EssayController(@Qualifier("siteEssayService") EssayService essayService,
                           @Autowired(required = false) Clock clock) {
        this.essayService = essayService;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    @GetMapping
    public ResponseEntity<EssayPageDto> listEssays(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        EssayPageDto result = essayService.getEssays(page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getEssayBySlug(@PathVariable String slug) {
        return essayService.getEssayBySlug(slug)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    ErrorResponseDto error = new ErrorResponseDto(
                            HttpStatus.NOT_FOUND.value(),
                            "Essay with slug '" + slug + "' was not found",
                            OffsetDateTime.now(clock)
                    );
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
    }

    @PostMapping
    public ResponseEntity<EssayDto> createEssay(@RequestBody EssayDto dto) {
        EssayDto created = essayService.createEssay(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
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
