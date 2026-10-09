package com.dmitryefremov.site.podcast.controller;

import com.dmitryefremov.site.podcast.dto.PodcastEpisodeDto;
import com.dmitryefremov.site.podcast.dto.PodcastEpisodePageDto;
import com.dmitryefremov.site.podcast.dto.PodcastTimecodeDto;
import com.dmitryefremov.site.podcast.service.PodcastEpisodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PodcastEpisodeController.class)
class PodcastEpisodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PodcastEpisodeService podcastEpisodeService;

    @Test
    void testListPodcastEpisodesReturns200() throws Exception {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 9, 12, 0, 0, 0, ZoneOffset.UTC);
        PodcastTimecodeDto timecode = new PodcastTimecodeDto(1L, 0, "Intro", "Intro section", 1);
        PodcastEpisodeDto episode = new PodcastEpisodeDto(
                10L,
                "systems-ep1",
                "Systems and Reality Ep 1",
                "Core thesis",
                "Description",
                "https://dmitryefremov.com/podcast/ep1.mp3",
                "https://youtube.com/watch?v=ep1",
                3600,
                publishedAt,
                publishedAt,
                List.of(timecode)
        );

        PodcastEpisodePageDto pageDto = new PodcastEpisodePageDto(List.of(episode), 0, 20, 1L, 1);

        when(podcastEpisodeService.getEpisodes(0, 20)).thenReturn(pageDto);

        mockMvc.perform(get("/api/v1/podcasts")
                        .param("page", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(20)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].slug", is("systems-ep1")))
                .andExpect(jsonPath("$.content[0].title", is("Systems and Reality Ep 1")))
                .andExpect(jsonPath("$.content[0].thesis", is("Core thesis")));
    }

    @Test
    void testGetPodcastEpisodeBySlugReturns200WithFullDetails() throws Exception {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 9, 12, 0, 0, 0, ZoneOffset.UTC);
        PodcastTimecodeDto tc1 = new PodcastTimecodeDto(101L, 0, "Introduction", "Intro description", 1);
        PodcastTimecodeDto tc2 = new PodcastTimecodeDto(102L, 600, "Deep Dive into Deontic Logic", "Deontic logic explanation", 2);

        PodcastEpisodeDto episode = new PodcastEpisodeDto(
                1L,
                "deontic-logic-and-systems",
                "Deontic Logic and Systems",
                "Systems logic thesis",
                "Full episode description",
                "https://dmitryefremov.com/podcast/ep2.mp3",
                "https://youtube.com/watch?v=ep2",
                2400,
                publishedAt,
                publishedAt,
                List.of(tc1, tc2)
        );

        when(podcastEpisodeService.getEpisodeBySlug("deontic-logic-and-systems"))
                .thenReturn(Optional.of(episode));

        mockMvc.perform(get("/api/v1/podcasts/deontic-logic-and-systems")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.slug", is("deontic-logic-and-systems")))
                .andExpect(jsonPath("$.title", is("Deontic Logic and Systems")))
                .andExpect(jsonPath("$.thesis", is("Systems logic thesis")))
                .andExpect(jsonPath("$.description", is("Full episode description")))
                .andExpect(jsonPath("$.audioUrl", is("https://dmitryefremov.com/podcast/ep2.mp3")))
                .andExpect(jsonPath("$.timecodes", hasSize(2)))
                .andExpect(jsonPath("$.timecodes[0].title", is("Introduction")))
                .andExpect(jsonPath("$.timecodes[1].title", is("Deep Dive into Deontic Logic")));
    }

    @Test
    void testGetPodcastEpisodeBySlugNotFoundReturns404() throws Exception {
        when(podcastEpisodeService.getEpisodeBySlug("non-existent-slug"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/podcasts/non-existent-slug")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Podcast episode with slug 'non-existent-slug' was not found")));
    }

    @Test
    void testListPodcastEpisodesInvalidPaginationReturns400() throws Exception {
        when(podcastEpisodeService.getEpisodes(anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException("Page size must be between 1 and 100"));

        mockMvc.perform(get("/api/v1/podcasts")
                        .param("page", "0")
                        .param("size", "150")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Page size must be between 1 and 100")));
    }
}
