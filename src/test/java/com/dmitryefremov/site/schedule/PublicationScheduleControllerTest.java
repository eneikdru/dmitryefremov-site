package com.dmitryefremov.site.schedule;

import com.dmitryefremov.site.schedule.domain.PublicationSchedule;
import com.dmitryefremov.site.schedule.repository.PublicationScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PublicationScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PublicationScheduleRepository publicationScheduleRepository;

    @BeforeEach
    void setUp() {
        publicationScheduleRepository.deleteAll();

        PublicationSchedule schedule1 = new PublicationSchedule(
                null,
                "Telegram Release #1",
                "TELEGRAM",
                "SCHEDULED",
                "Topic about distributed systems",
                OffsetDateTime.of(2026, 10, 20, 12, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 10, 10, 10, 0, 0, 0, ZoneOffset.UTC)
        );

        PublicationSchedule schedule2 = new PublicationSchedule(
                null,
                "YouTube Release #1",
                "YOUTUBE",
                "IN_PRODUCTION",
                "Topic about software engineering",
                OffsetDateTime.of(2026, 10, 22, 14, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 10, 11, 11, 0, 0, 0, ZoneOffset.UTC)
        );

        PublicationSchedule schedule3 = new PublicationSchedule(
                null,
                "Article Release #1",
                "ARTICLE",
                "PUBLISHED",
                "Longread about TOC",
                OffsetDateTime.of(2026, 10, 25, 9, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 10, 12, 8, 0, 0, 0, ZoneOffset.UTC)
        );

        publicationScheduleRepository.save(schedule1);
        publicationScheduleRepository.save(schedule2);
        publicationScheduleRepository.save(schedule3);
    }

    @Test
    void testGetAllSchedulesReturnsJsonArray() throws Exception {
        mockMvc.perform(get("/api/v1/schedule")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$[0].title", is("Telegram Release #1")))
                .andExpect(jsonPath("$[0].platform", is("TELEGRAM")))
                .andExpect(jsonPath("$[0].status", is("SCHEDULED")))
                .andExpect(jsonPath("$[0].topicSummary", is("Topic about distributed systems")))
                .andExpect(jsonPath("$[1].title", is("YouTube Release #1")))
                .andExpect(jsonPath("$[2].title", is("Article Release #1")));
    }

    @Test
    void testFilterByPlatformExactCase() throws Exception {
        mockMvc.perform(get("/api/v1/schedule")
                        .param("platform", "TELEGRAM")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].title", is("Telegram Release #1")))
                .andExpect(jsonPath("$[0].platform", is("TELEGRAM")));
    }

    @Test
    void testFilterByPlatformCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/v1/schedule")
                        .param("platform", "youtube")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].title", is("YouTube Release #1")))
                .andExpect(jsonPath("$[0].platform", is("YOUTUBE")));
    }

    @Test
    void testFilterByNonExistentPlatformReturnsEmptyArray() throws Exception {
        mockMvc.perform(get("/api/v1/schedule")
                        .param("platform", "NON_EXISTENT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void testAliasEndpointsReturnJsonArray() throws Exception {
        mockMvc.perform(get("/api/v1/schedules")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$.length()", is(3)));

        mockMvc.perform(get("/api/v1/publication-schedules")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$.length()", is(3)));
    }
}
