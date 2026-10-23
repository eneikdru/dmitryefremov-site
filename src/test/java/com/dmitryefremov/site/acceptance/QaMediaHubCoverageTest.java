package com.dmitryefremov.site.acceptance;

import com.dmitryefremov.site.podcast.domain.EpisodeTopic;
import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.domain.PodcastQuote;
import com.dmitryefremov.site.podcast.domain.PodcastTimecode;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class QaMediaHubCoverageTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PodcastEpisodeRepository podcastEpisodeRepository;

    @Test
    @DisplayName("Given a mocked episode payload, When sync pipeline & schema validation execute, Then podcast data sync and endpoint contracts succeed")
    void testPodcastEpisodeSyncAndSchemaValidation() throws Exception {
        String slug = "qa-sync-episode-test";
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        PodcastEpisode episode = new PodcastEpisode(
                slug,
                "QA Media Hub Sync Episode: Philosophy of Mind",
                "Thesis on cognitive systems and mind-body interaction",
                "Full episode content detailing intellectual insights from Chalmers and Clark",
                "https://dmitryefremov.com/media/podcast/qa-sync-ep.mp3",
                "https://youtube.com/watch?v=qa-sync-ep",
                "https://youtube.com/watch?v=qa-sync-ep",
                "https://t.me/efremov_mvp/101",
                "https://spotify.com/episode/qa-sync-ep",
                "https://podcasts.apple.com/podcast/qa-sync-ep",
                "YouTube, Telegram, Spotify, Apple Podcasts",
                5400,
                now
        );

        PodcastTimecode tc1 = new PodcastTimecode(0, "Introduction", "Opening section", 1);
        PodcastTimecode tc2 = new PodcastTimecode(1800, "Extended Mind Hypothesis", "Andy Clark & David Chalmers analysis", 2);
        episode.addTimecode(tc1);
        episode.addTimecode(tc2);

        EpisodeTopic topic1 = new EpisodeTopic("Andy Clark", 1800, 3600, "Extended Mind", 1);
        EpisodeTopic topic2 = new EpisodeTopic("David Chalmers", 3600, 5400, "Hard Problem of Consciousness", 2);
        episode.addTopic(topic1);
        episode.addTopic(topic2);

        PodcastQuote quote1 = new PodcastQuote("Mind extends into external artifacts", "Andy Clark", 2100, "extended-mind", 1);
        episode.addQuote(quote1);

        // Save through repository (Sync Pipeline verification)
        PodcastEpisode saved = podcastEpisodeRepository.save(episode);
        assertThat(saved.getId()).isNotNull();

        // Retrieve and verify DB sync
        Optional<PodcastEpisode> retrieved = podcastEpisodeRepository.findBySlug(slug);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getTitle()).contains("Philosophy of Mind");
        assertThat(retrieved.get().getTimecodes()).hasSize(2);
        assertThat(retrieved.get().getTopics()).hasSize(2);
        assertThat(retrieved.get().getQuotes()).hasSize(1);

        // API Endpoint & Schema Contract Validation
        mockMvc.perform(get("/api/v1/podcasts/" + slug)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug", is(slug)))
                .andExpect(jsonPath("$.title", is("QA Media Hub Sync Episode: Philosophy of Mind")))
                .andExpect(jsonPath("$.audioUrl", is("https://dmitryefremov.com/media/podcast/qa-sync-ep.mp3")))
                .andExpect(jsonPath("$.youtubeUrl", is("https://youtube.com/watch?v=qa-sync-ep")))
                .andExpect(jsonPath("$.telegramUrl", is("https://t.me/efremov_mvp/101")))
                .andExpect(jsonPath("$.durationSeconds", is(5400)))
                .andExpect(jsonPath("$.timecodes", hasSize(2)))
                .andExpect(jsonPath("$.timecodes[0].title", is("Introduction")))
                .andExpect(jsonPath("$.timecodes[1].title", is("Extended Mind Hypothesis")))
                .andExpect(jsonPath("$.topics", hasSize(2)))
                .andExpect(jsonPath("$.topics[0].topicName", is("Andy Clark")))
                .andExpect(jsonPath("$.topics[1].topicName", is("David Chalmers")))
                .andExpect(jsonPath("$.quotes", hasSize(1)))
                .andExpect(jsonPath("$.quotes[0].quoteText", is("Mind extends into external artifacts")))
                .andExpect(jsonPath("$.quotes[0].speaker", is("Andy Clark")));
    }

    @Test
    @DisplayName("Verify playback and episode list data sync endpoints for seeded canonical media hub content")
    void testSeededCanonicalEpisodesMediaSync() throws Exception {
        mockMvc.perform(get("/api/v1/podcasts")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(3)));

        // Verify canonical episode 1
        mockMvc.perform(get("/api/v1/podcasts/episode-01-ruth-barcan-marcus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("episode-01-ruth-barcan-marcus")))
                .andExpect(jsonPath("$.audioUrl", notNullValue()))
                .andExpect(jsonPath("$.timecodes", not(empty())));
    }
}
