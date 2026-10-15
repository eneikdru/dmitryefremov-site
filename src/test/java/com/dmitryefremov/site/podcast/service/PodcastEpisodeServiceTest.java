package com.dmitryefremov.site.podcast.service;

import com.dmitryefremov.site.podcast.domain.EpisodeTopic;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    void testGetEpisodesSuccess() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 9, 12, 0, 0, 0, ZoneOffset.UTC);
        PodcastEpisode episode = new PodcastEpisode(
                "systems-ep1",
                "Systems Ep 1",
                "Thesis text",
                "Description text",
                "https://dmitryefremov.com/podcast/ep1.mp3",
                "https://youtube.com/watch?v=ep1",
                3600,
                publishedAt
        );

        Pageable pageable = PageRequest.of(0, 20);
        when(podcastEpisodeRepository.findAllByOrderByPublishedAtDesc(pageable))
                .thenReturn(new PageImpl<>(List.of(episode), pageable, 1));

        PodcastEpisodePageDto result = podcastEpisodeService.getEpisodes(0, 20);

        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(20);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getSlug()).isEqualTo("systems-ep1");
    }

    @Test
    void testGetEpisodesInvalidPaginationThrowsException() {
        assertThatThrownBy(() -> podcastEpisodeService.getEpisodes(-1, 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Page index must not be less than zero");

        assertThatThrownBy(() -> podcastEpisodeService.getEpisodes(0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Page size must be between 1 and 100");

        assertThatThrownBy(() -> podcastEpisodeService.getEpisodes(0, 101))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Page size must be between 1 and 100");
    }

    @Test
    void testGetEpisodeBySlugFound() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 9, 12, 0, 0, 0, ZoneOffset.UTC);
        PodcastEpisode episode = new PodcastEpisode(
                "systems-ep1",
                "Systems Ep 1",
                "Thesis text",
                "Description text",
                "https://dmitryefremov.com/podcast/ep1.mp3",
                "https://youtube.com/watch?v=ep1",
                3600,
                publishedAt
        );

        PodcastTimecode tc1 = new PodcastTimecode(0, "Start", "Start section", 1);
        PodcastTimecode tc2 = new PodcastTimecode(600, "Theory", "Theory section", 2);
        episode.addTimecode(tc1);
        episode.addTimecode(tc2);

        EpisodeTopic t1 = new EpisodeTopic("Ruth Barcan Marcus", 0, 1800, "Modal logic topic", 1);
        episode.addTopic(t1);

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
        assertThat(dto.getTopics()).hasSize(1);
        assertThat(dto.getTopics().get(0).getTopicName()).isEqualTo("Ruth Barcan Marcus");
        assertThat(dto.getTopics().get(0).getStartTimeSeconds()).isEqualTo(0);
        assertThat(dto.getTopics().get(0).getEndTimeSeconds()).isEqualTo(1800);
    }

    @Test
    void testGetEpisodeBySlugNotFound() {
        when(podcastEpisodeRepository.findBySlug("unknown")).thenReturn(Optional.empty());

        Optional<PodcastEpisodeDto> result = podcastEpisodeService.getEpisodeBySlug("unknown");

        assertThat(result).isEmpty();
    }
}
