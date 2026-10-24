package com.dmitryefremov.site.acceptance;

import com.dmitryefremov.site.article.repository.ArticleRepository;
import com.dmitryefremov.site.essay.domain.Essay;
import com.dmitryefremov.site.essay.domain.PublishedEssay;
import com.dmitryefremov.site.essay.repository.EssayRepository;
import com.dmitryefremov.site.essay.repository.PublishedEssayRepository;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import com.dmitryefremov.site.spark.repository.SparkRepository;
import org.junit.jupiter.api.BeforeEach;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CoreExperienceQaVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EssayRepository essayRepository;

    @Autowired
    private PublishedEssayRepository publishedEssayRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PodcastEpisodeRepository podcastEpisodeRepository;

    @Autowired
    private SparkRepository sparkRepository;

    private OffsetDateTime fixedTime;

    @BeforeEach
    void setUp() {
        fixedTime = OffsetDateTime.of(2026, 10, 15, 12, 0, 0, 0, ZoneOffset.UTC);

        // Seed raw teleprompter draft in internal essays table
        Essay draftTeleprompter = new Essay(
                "teleprompter-script-raw-1",
                "Raw Teleprompter Script Draft",
                "Internal teleprompter draft summary",
                "Raw script contents for internal video production /mnt/HC_Volume_106932004/podcast/episodes",
                fixedTime
        );
        essayRepository.save(draftTeleprompter);

        // Seed curated published essay in published_essays table
        PublishedEssay publishedEssay = new PublishedEssay(
                "published-curated-article-1",
                "Deontic Logic and System Invariants",
                "Public curated essay summary",
                "Refined public content without raw teleprompter scripts or internal telemetry.",
                "Dmitry Efremov",
                fixedTime
        );
        publishedEssayRepository.save(publishedEssay);

        essayRepository.flush();
        publishedEssayRepository.flush();
    }

    @Test
    @DisplayName("Given public API, when explicitly attempting to load raw teleprompter script file paths, then returns 404 Not Found")
    void testPublicApiRefusesRawTeleprompterScriptWith404() throws Exception {
        mockMvc.perform(get("/api/v1/articles/teleprompter-raw-script-path")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));

        mockMvc.perform(get("/api/v1/podcasts/episodes/teleprompter-raw-script-path")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given public API endpoints, when requested, then public content contains zero raw teleprompter file paths or internal telemetry leakage")
    void testPublicApiZeroTelemetryAndTeleprompterPathLeakage() throws Exception {
        // Test /api/v1/articles
        mockMvc.perform(get("/api/v1/articles")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("[SYS_CORE]"))))
                .andExpect(content().string(not(containsString("/mnt/HC_Volume_106932004"))));

        // Test /api/v1/podcasts
        mockMvc.perform(get("/api/v1/podcasts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("[SYS_CORE]"))))
                .andExpect(content().string(not(containsString("/mnt/HC_Volume_106932004"))));
    }

    @Test
    @DisplayName("Given empty database, when accessing public content endpoints, then clean empty payloads are returned without fake authors (@alexp) or dummy data")
    void testEmptyDatabaseReturnsCleanEmptyStateWithoutPlaceholderContent() throws Exception {
        // Clear all public content tables
        publishedEssayRepository.deleteAll();
        essayRepository.deleteAll();
        articleRepository.deleteAll();
        podcastEpisodeRepository.deleteAll();
        sparkRepository.deleteAll();

        // Verify podcasts return empty list
        mockMvc.perform(get("/api/v1/podcasts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(content().string(not(containsString("@alexp"))))
                .andExpect(content().string(not(containsString("Deontic Logic in Autonomous Process Architecture"))));

        // Verify essays return empty list
        mockMvc.perform(get("/api/v1/essays")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(content().string(not(containsString("@alexp"))));

        // Verify articles return empty list
        mockMvc.perform(get("/api/v1/articles")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(content().string(not(containsString("@alexp"))));

        // Verify sparks return empty list
        mockMvc.perform(get("/api/v1/sparks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(content().string(not(containsString("@alexp"))));
    }

    @Test
    @DisplayName("Given public endpoints, when draft content exists alongside published content, then draft content is isolated")
    void testDraftContentIsolatedFromPublicApi() throws Exception {
        // Verify curated published essay is available
        mockMvc.perform(get("/api/v1/essays/published-curated-article-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("published-curated-article-1")))
                .andExpect(jsonPath("$.title", is("Deontic Logic and System Invariants")));

        // Verify internal draft essay slug returns 404 on public essay endpoint
        mockMvc.perform(get("/api/v1/essays/teleprompter-script-raw-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
