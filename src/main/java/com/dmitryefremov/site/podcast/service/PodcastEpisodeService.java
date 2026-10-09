package com.dmitryefremov.site.podcast.service;

import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.domain.PodcastTimecode;
import com.dmitryefremov.site.podcast.dto.PodcastEpisodeDto;
import com.dmitryefremov.site.podcast.dto.PodcastEpisodePageDto;
import com.dmitryefremov.site.podcast.dto.PodcastTimecodeDto;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("sitePodcastEpisodeService")
@Transactional(readOnly = true)
public class PodcastEpisodeService {

    private final PodcastEpisodeRepository podcastEpisodeRepository;

    public PodcastEpisodeService(PodcastEpisodeRepository podcastEpisodeRepository) {
        this.podcastEpisodeRepository = podcastEpisodeRepository;
    }

    public PodcastEpisodePageDto getEpisodes(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<PodcastEpisode> episodePage = podcastEpisodeRepository.findAllByOrderByPublishedAtDesc(pageable);

        List<PodcastEpisodeDto> dtos = episodePage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PodcastEpisodePageDto(
                dtos,
                episodePage.getNumber(),
                episodePage.getSize(),
                episodePage.getTotalElements(),
                episodePage.getTotalPages()
        );
    }

    public Optional<PodcastEpisodeDto> getEpisodeBySlug(String slug) {
        return podcastEpisodeRepository.findBySlug(slug)
                .map(this::mapToDto);
    }

    public PodcastEpisodeDto mapToDto(PodcastEpisode entity) {
        List<PodcastTimecodeDto> timecodeDtos = entity.getTimecodes().stream()
                .map(this::mapTimecodeToDto)
                .collect(Collectors.toList());

        return new PodcastEpisodeDto(
                entity.getId(),
                entity.getSlug(),
                entity.getTitle(),
                entity.getThesis(),
                entity.getDescription(),
                entity.getAudioUrl(),
                entity.getMediaLink(),
                entity.getDurationSeconds(),
                entity.getPublishedAt(),
                entity.getCreatedAt(),
                timecodeDtos
        );
    }

    private PodcastTimecodeDto mapTimecodeToDto(PodcastTimecode entity) {
        return new PodcastTimecodeDto(
                entity.getId(),
                entity.getTimeOffsetSeconds(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDisplayOrder()
        );
    }
}
