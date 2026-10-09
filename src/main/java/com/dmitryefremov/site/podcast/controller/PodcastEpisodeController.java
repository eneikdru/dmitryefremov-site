package com.dmitryefremov.site.podcast.controller;

import com.dmitryefremov.site.podcast.dto.PodcastEpisodeDetailDto;
import com.dmitryefremov.site.podcast.dto.PodcastEpisodeSummaryDto;
import com.dmitryefremov.site.podcast.service.PodcastEpisodeDomainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class PodcastEpisodeController {

    private final PodcastEpisodeDomainService podcastEpisodeDomainService;

    public PodcastEpisodeController(PodcastEpisodeDomainService podcastEpisodeDomainService) {
        this.podcastEpisodeDomainService = podcastEpisodeDomainService;
    }

    @GetMapping({"/api/podcasts", "/api/episodes"})
    public ResponseEntity<List<PodcastEpisodeSummaryDto>> getAllEpisodes() {
        return ResponseEntity.ok(podcastEpisodeDomainService.getAllEpisodeSummaries());
    }

    @GetMapping({"/api/podcasts/{identifier}", "/api/episodes/{identifier}"})
    public ResponseEntity<PodcastEpisodeDetailDto> getEpisodeDetail(@PathVariable String identifier) {
        return podcastEpisodeDomainService.getEpisodeDetailBySlugOrId(identifier)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
