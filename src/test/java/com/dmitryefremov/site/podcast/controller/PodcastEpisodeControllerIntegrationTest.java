package com.dmitryefremov.site.podcast.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PodcastEpisodeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testListPodcastEpisodesReturns200WithMetadataAndValidAudioUrls() throws Exception {
        mockMvc.perform(get("/api/v1/podcasts")
                        .param("page", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].audioUrl", notNullValue()))
                .andExpect(jsonPath("$.content[0].audioUrl", startsWith("http")));
    }

    @Test
    void testGetPodcastEpisodeBySlugReturnsCanonicalTopicsAndMedia() throws Exception {
        mockMvc.perform(get("/api/v1/podcasts/systems-and-reality-ep1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug", is("systems-and-reality-ep1")))
                .andExpect(jsonPath("$.audioUrl", notNullValue()))
                .andExpect(jsonPath("$.topics", not(empty())))
                .andExpect(jsonPath("$.topics[0].startTimeSeconds", notNullValue()))
                .andExpect(jsonPath("$.topics[0].topicName", notNullValue()));
    }

    @Autowired
    private com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository podcastEpisodeRepository;

    @Test
    void testDraftPodcastEpisodesAreExcludedFromApiAndCounts() throws Exception {
        // Save raw teleprompter draft episode
        com.dmitryefremov.site.podcast.domain.PodcastEpisode draft = new com.dmitryefremov.site.podcast.domain.PodcastEpisode(
                "draft-teleprompter-script-ep99",
                "Draft Teleprompter Episode",
                "Raw draft thesis",
                "Draft description",
                "https://cdn.dmitryefremov.com/podcast/draft.mp3",
                "https://dmitryefremov.com/podcasts/draft",
                1800,
                null
        );
        podcastEpisodeRepository.save(draft);
        podcastEpisodeRepository.flush();

        // 1. Fetching draft by slug directly returns 404
        mockMvc.perform(get("/api/v1/podcasts/draft-teleprompter-script-ep99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // 2. Listing episodes excludes the draft slug from content
        mockMvc.perform(get("/api/v1/podcasts")
                        .param("page", "0")
                        .param("size", "100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].slug", not(hasItem("draft-teleprompter-script-ep99"))));
    }
}
