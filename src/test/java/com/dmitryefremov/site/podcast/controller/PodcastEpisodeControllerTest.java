package com.dmitryefremov.site.podcast.controller;

import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.domain.PodcastTimecode;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PodcastEpisodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PodcastEpisodeRepository podcastEpisodeRepository;

    private PodcastEpisode savedEpisode;

    @BeforeEach
    void setUp() {
        podcastEpisodeRepository.deleteAll();

        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 9, 12, 0, 0, 0, ZoneOffset.UTC);
        PodcastEpisode episode = new PodcastEpisode(
                "ep-01-systems-reality",
                "Системы и реальность: Выпуск 1",
                "Тезисы о системах и теориях ограничений.",
                "Описание выпуска подкаста.",
                "https://cdn.dmitryefremov.com/podcasts/ep1.mp3",
                "https://youtube.com/watch?v=ep1",
                3600,
                publishedAt
        );

        PodcastTimecode timecode1 = new PodcastTimecode(0, "Введение", "Старт подкаста", 1);
        PodcastTimecode timecode2 = new PodcastTimecode(300, "Основная тема", "Системный подход", 2);

        episode.addTimecode(timecode1);
        episode.addTimecode(timecode2);

        savedEpisode = podcastEpisodeRepository.save(episode);
    }

    @Test
    void testGetAllEpisodes() throws Exception {
        mockMvc.perform(get("/api/podcasts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].slug", is("ep-01-systems-reality")))
                .andExpect(jsonPath("$[0].title", is("Системы и реальность: Выпуск 1")))
                .andExpect(jsonPath("$[0].audioUrl", is("https://cdn.dmitryefremov.com/podcasts/ep1.mp3")))
                .andExpect(jsonPath("$[0].durationSeconds", is(3600)));
    }

    @Test
    void testGetEpisodeBySlugWithThesesAndTimecodes() throws Exception {
        mockMvc.perform(get("/api/podcasts/ep-01-systems-reality")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("ep-01-systems-reality")))
                .andExpect(jsonPath("$.title", is("Системы и реальность: Выпуск 1")))
                .andExpect(jsonPath("$.thesis", is("Тезисы о системах и теориях ограничений.")))
                .andExpect(jsonPath("$.description", is("Описание выпуска подкаста.")))
                .andExpect(jsonPath("$.mediaLink", is("https://youtube.com/watch?v=ep1")))
                .andExpect(jsonPath("$.timecodes", hasSize(2)))
                .andExpect(jsonPath("$.timecodes[0].title", is("Введение")))
                .andExpect(jsonPath("$.timecodes[0].timeOffsetSeconds", is(0)))
                .andExpect(jsonPath("$.timecodes[1].title", is("Основная тема")))
                .andExpect(jsonPath("$.timecodes[1].timeOffsetSeconds", is(300)));
    }

    @Test
    void testGetEpisodeById() throws Exception {
        mockMvc.perform(get("/api/podcasts/" + savedEpisode.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedEpisode.getId().intValue())))
                .andExpect(jsonPath("$.slug", is("ep-01-systems-reality")));
    }

    @Test
    void testGetEpisodeNotFound() throws Exception {
        mockMvc.perform(get("/api/podcasts/non-existent-slug")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
