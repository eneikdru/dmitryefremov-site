package com.dmitryefremov.site.podcast.service;

import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.domain.PodcastTimecode;
import com.dmitryefremov.site.podcast.dto.PodcastEpisodeDetailDto;
import com.dmitryefremov.site.podcast.dto.PodcastEpisodeSummaryDto;
import com.dmitryefremov.site.podcast.dto.PodcastTimecodeDto;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PodcastEpisodeDomainService {

    private final PodcastEpisodeRepository podcastEpisodeRepository;

    public PodcastEpisodeDomainService(PodcastEpisodeRepository podcastEpisodeRepository) {
        this.podcastEpisodeRepository = podcastEpisodeRepository;
    }

    public List<PodcastEpisodeSummaryDto> getAllEpisodeSummaries() {
        return podcastEpisodeRepository.findAll(Sort.by(Sort.Direction.DESC, "publishedAt"))
                .stream()
                .map(this::toSummaryDto)
                .toList();
    }

    public Optional<PodcastEpisodeDetailDto> getEpisodeDetailBySlugOrId(String identifier) {
        Optional<PodcastEpisode> episodeOpt = podcastEpisodeRepository.findBySlug(identifier);
        if (episodeOpt.isEmpty()) {
            try {
                Long id = Long.parseLong(identifier);
                episodeOpt = podcastEpisodeRepository.findById(id);
            } catch (NumberFormatException ignored) {
                // Not a numeric ID
            }
        }
        return episodeOpt.map(this::toDetailDto);
    }

    private PodcastEpisodeSummaryDto toSummaryDto(PodcastEpisode episode) {
        return new PodcastEpisodeSummaryDto(
                episode.getId(),
                episode.getSlug(),
                episode.getTitle(),
                episode.getDescription(),
                episode.getAudioUrl(),
                episode.getMediaLink(),
                episode.getDurationSeconds(),
                episode.getPublishedAt()
        );
    }

    private PodcastEpisodeDetailDto toDetailDto(PodcastEpisode episode) {
        List<PodcastTimecodeDto> timecodeDtos = episode.getTimecodes().stream()
                .sorted(Comparator.comparing(PodcastTimecode::getDisplayOrder)
                        .thenComparing(PodcastTimecode::getTimeOffsetSeconds))
                .map(tc -> new PodcastTimecodeDto(
                        tc.getId(),
                        tc.getTimeOffsetSeconds(),
                        tc.getTitle(),
                        tc.getDescription(),
                        tc.getDisplayOrder()
                ))
                .toList();

        return new PodcastEpisodeDetailDto(
                episode.getId(),
                episode.getSlug(),
                episode.getTitle(),
                episode.getThesis(),
                episode.getDescription(),
                episode.getAudioUrl(),
                episode.getMediaLink(),
                episode.getDurationSeconds(),
                episode.getPublishedAt(),
                timecodeDtos
        );
    }
}
