package com.dmitryefremov.site.podcast.controller;

import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import com.dmitryefremov.site.podcast.dto.PodcastEpisodeDto;
import com.dmitryefremov.site.podcast.dto.PodcastEpisodePageDto;
import com.dmitryefremov.site.podcast.service.PodcastEpisodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/podcasts")
public class PodcastEpisodeController {

    private final PodcastEpisodeService podcastEpisodeService;

    public PodcastEpisodeController(PodcastEpisodeService podcastEpisodeService) {
        this.podcastEpisodeService = podcastEpisodeService;
    }

    @GetMapping
    public ResponseEntity<PodcastEpisodePageDto> listEpisodes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PodcastEpisodePageDto result = podcastEpisodeService.getEpisodes(page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getEpisodeBySlug(@PathVariable String slug) {
        return podcastEpisodeService.getEpisodeBySlug(slug)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    ErrorResponseDto error = new ErrorResponseDto(
                            HttpStatus.NOT_FOUND.value(),
                            "Podcast episode with slug '" + slug + "' was not found",
                            OffsetDateTime.now()
                    );
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
