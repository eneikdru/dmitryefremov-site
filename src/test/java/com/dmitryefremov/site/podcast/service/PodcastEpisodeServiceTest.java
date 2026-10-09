package com.dmitryefremov.site.podcast.service;

import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.domain.PodcastTimecode;
import com.dmitryefremov.site.podcast.dto.PodcastEpisodeDto;
import com.dmitryefremov.site.podcast.dto.PodcastEpisodePageDto;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PodcastEpisodeServiceTest {

    @Mock
    private PodcastEpisodeRepository podcastEpisodeRepository;

    private PodcastEpisodeService podcastEpisodeService;

    @BeforeEach
    void setUp() {
        podcastEpisodeService = new PodcastEpisodeService(podcastEpisodeRepository);
    }

    @Test
    void testGetEpisodesReturnsPaginatedDto() {
        OffsetDateTime now = OffsetDateTime.of(2026, 10, 9, 12, 0, 0, 0, ZoneOffset.UTC);
        PodcastEpisode episode = new PodcastEpisode(
                "systems-ep1",
                "Systems & Reality",
                "Thesis text",
                "Description text",
                "https://cdn.example.com/audio.mp3",
                "https://youtube.com/watch?v=123",
                1800,
                now
        );
        episode.addTimecode(new PodcastTimecode(0, "Intro", "Intro description", 1));

        Page<PodcastEpisode> page = new PageImpl<>(List.of(episode), PageRequest.of(0, 20), 1);
        when(podcastEpisodeRepository.findAllByOrderByPublishedAtDesc(any())).thenReturn(page);

        PodcastEpisodePageDto result = podcastEpisodeService.getEpisodes(0, 20);

        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(20);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

        PodcastEpisodeDto dto = result.getContent().get(0);
        assertThat(dto.getSlug()).isEqualTo("systems-ep1");
        assertThat(dto.getTitle()).isEqualTo("Systems & Reality");
        assertThat(dto.getThesis()).isEqualTo("Thesis text");
        assertThat(dto.getTimecodes()).hasSize(1);
        assertThat(dto.getTimecodes().get(0).getTitle()).isEqualTo("Intro");
    }

    @Test
    void testGetEpisodesThrowsOnInvalidPagination() {
        assertThatThrownBy(() -> podcastEpisodeService.getEpisodes(-1, 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page index must not be less than zero");

        assertThatThrownBy(() -> podcastEpisodeService.getEpisodes(0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page size must be between 1 and 100");

        assertThatThrownBy(() -> podcastEpisodeService.getEpisodes(0, 101))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page size must be between 1 and 100");
    }

    @Test
    void testGetEpisodeBySlugFound() {
        OffsetDateTime now = OffsetDateTime.of(2026, 10, 9, 12, 0, 0, 0, ZoneOffset.UTC);
        PodcastEpisode episode = new PodcastEpisode(
                "systems-ep1",
                "Systems & Reality",
                "Thesis text",
                "Full description",
                "https://cdn.example.com/audio.mp3",
                "https://youtube.com/watch?v=123",
                3600,
                now
        );
        episode.addTimecode(new PodcastTimecode(0, "Start", "Start segment", 1));
        episode.addTimecode(new PodcastTimecode(600, "Theory", "Theory segment", 2));

        when(podcastEpisodeRepository.findBySlug("systems-ep1")).thenReturn(Optional.of(episode));

        Optional<PodcastEpisodeDto> result = podcastEpisodeService.getEpisodeBySlug("systems-ep1");

        assertThat(result).isPresent();
        PodcastEpisodeDto dto = result.get();
        assertThat(dto.getSlug()).isEqualTo("systems-ep1");
        assertThat(dto.getThesis()).isEqualTo("Thesis text");
        assertThat(dto.getTimecodes()).hasSize(2);
        assertThat(dto.getTimecodes())
                .extracting("title")
                .containsExactly("Start", "Theory");
    }

    @Test
    void testGetEpisodeBySlugNotFound() {
        when(podcastEpisodeRepository.findBySlug("unknown")).thenReturn(Optional.empty());

        Optional<PodcastEpisodeDto> result = podcastEpisodeService.getEpisodeBySlug("unknown");

        assertThat(result).isEmpty();
    }
}
