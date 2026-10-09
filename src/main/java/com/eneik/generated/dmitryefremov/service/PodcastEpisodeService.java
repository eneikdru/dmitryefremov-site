package com.eneik.generated.dmitryefremov.service;

import com.eneik.generated.dmitryefremov.model.PodcastEpisode;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PodcastEpisodeService {
    private final ConcurrentHashMap<String, PodcastEpisode> episodes = new ConcurrentHashMap<>();

    public PodcastEpisodeService() {
        PodcastEpisode sample = new PodcastEpisode(
            "pod-1",
            "ep-01-systems-reality",
            "Systems and Reality: Episode 1",
            "Discussion on systems thinking, bottleneck analysis, and technical brutalism.",
            "https://dmitryefremov.com/podcasts/ep-01.mp3",
            Instant.parse("2026-02-01T12:00:00Z")
        );
        episodes.put(sample.getSlug(), sample);
    }

    public List<PodcastEpisode> getAllEpisodes() {
        return new ArrayList<>(episodes.values());
    }

    public Optional<PodcastEpisode> findBySlug(String slug) {
        return Optional.ofNullable(episodes.get(slug));
    }

    public void addEpisode(PodcastEpisode episode) {
        episodes.put(episode.getSlug(), episode);
    }
}
