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
}
