package com.dmitryefremov.site.acceptance;

import com.dmitryefremov.site.essay.domain.Essay;
import com.dmitryefremov.site.essay.repository.EssayRepository;
import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CanonicalPodcastsAndEssaysSeedingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PodcastEpisodeRepository podcastEpisodeRepository;

    @Autowired
    private com.dmitryefremov.site.essay.repository.PublishedEssayRepository publishedEssayRepository;

    @Test
    @DisplayName("Verify canonical podcast episodes 01, 02, and 03 are correctly seeded with timecodes")
    void testCanonicalPodcastEpisodesSeeded() throws Exception {
        // Episode 01: Ruth Barcan Marcus
        Optional<PodcastEpisode> ep1 = podcastEpisodeRepository.findBySlug("episode-01-ruth-barcan-marcus");
        assertThat(ep1).isPresent();
        assertThat(ep1.get().getTitle()).contains("Ruth Barcan Marcus");
        assertThat(ep1.get().getAudioUrl()).isEqualTo("https://dmitryefremov.com/media/podcast/ep01-ruth-barcan-marcus.mp3");
        assertThat(ep1.get().getTimecodes()).hasSize(3);

        // Episode 02: Paul Thagard
        Optional<PodcastEpisode> ep2 = podcastEpisodeRepository.findBySlug("episode-02-paul-thagard");
        assertThat(ep2).isPresent();
        assertThat(ep2.get().getTitle()).contains("Paul Thagard");
        assertThat(ep2.get().getAudioUrl()).isEqualTo("https://dmitryefremov.com/media/podcast/ep02-paul-thagard.mp3");
        assertThat(ep2.get().getTimecodes()).hasSize(3);

        // Episode 03: Susan Hurley
        Optional<PodcastEpisode> ep3 = podcastEpisodeRepository.findBySlug("episode-03-susan-hurley");
        assertThat(ep3).isPresent();
        assertThat(ep3.get().getTitle()).contains("Susan Hurley");
        assertThat(ep3.get().getAudioUrl()).isEqualTo("https://dmitryefremov.com/media/podcast/ep03-susan-hurley.mp3");
        assertThat(ep3.get().getTimecodes()).hasSize(3);

        // Verify API endpoint response for Episode 01
        mockMvc.perform(get("/api/v1/podcasts/episode-01-ruth-barcan-marcus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("episode-01-ruth-barcan-marcus"))
                .andExpect(jsonPath("$.title").value(containsString("Ruth Barcan Marcus")));
    }

    @Test
    @DisplayName("Verify curated standalone essays are correctly inserted in essays table")
    void testCuratedEssaysSeeded() throws Exception {
        // Verify Essay 1 in published_essays
        Optional<com.dmitryefremov.site.essay.domain.PublishedEssay> essay1 = publishedEssayRepository.findBySlug("quantified-modal-logic-and-essential-invariants");
        assertThat(essay1).isPresent();
        assertThat(essay1.get().getTitle()).contains("Quantified Modal Logic");
        assertThat(essay1.get().getContent()).contains("Executive Summary");

        // Verify Essay 2 in published_essays
        Optional<com.dmitryefremov.site.essay.domain.PublishedEssay> essay2 = publishedEssayRepository.findBySlug("explanatory-coherence-and-agentic-architecture");
        assertThat(essay2).isPresent();
        assertThat(essay2.get().getTitle()).contains("Explanatory Coherence");

        // Verify Essay 3 in published_essays
        Optional<com.dmitryefremov.site.essay.domain.PublishedEssay> essay3 = publishedEssayRepository.findBySlug("consciousness-in-action-and-embodied-agents");
        assertThat(essay3).isPresent();
        assertThat(essay3.get().getTitle()).contains("Consciousness in Action");

        // Verify total curated essays count in published_essays database table
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM published_essays WHERE slug IN (?, ?, ?)",
                Integer.class,
                "quantified-modal-logic-and-essential-invariants",
                "explanatory-coherence-and-agentic-architecture",
                "consciousness-in-action-and-embodied-agents"
        );
        assertThat(count).isEqualTo(3);

        // Verify API endpoint for Essay 1
        mockMvc.perform(get("/api/v1/essays/quantified-modal-logic-and-essential-invariants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("quantified-modal-logic-and-essential-invariants"))
                .andExpect(jsonPath("$.title").value(containsString("Quantified Modal Logic")));
    }
}
