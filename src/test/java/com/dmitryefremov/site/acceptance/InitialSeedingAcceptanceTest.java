package com.dmitryefremov.site.acceptance;

import com.dmitryefremov.site.article.domain.Article;
import com.dmitryefremov.site.article.repository.ArticleRepository;
import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InitialSeedingAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PodcastEpisodeRepository podcastEpisodeRepository;

    @Test
    void testSeededApiCredentialsExistInDatabase() {
        Integer credentialCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM api_credentials WHERE client_id = 'test-agent-client-id'",
                Integer.class
        );
        assertThat(credentialCount).isNotNull().isEqualTo(1);

        String role = jdbcTemplate.queryForObject(
                "SELECT role FROM api_credentials WHERE client_id = 'test-agent-client-id'",
                String.class
        );
        assertThat(role).isEqualTo("CONTENT_AGENT");

        // Verify seeded demo API key
        Integer demoKeyCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM api_credentials WHERE client_id = 'demo-api-key'",
                Integer.class
        );
        assertThat(demoKeyCount).isNotNull().isEqualTo(1);

        String demoRole = jdbcTemplate.queryForObject(
                "SELECT role FROM api_credentials WHERE client_id = 'demo-api-key'",
                String.class
        );
        assertThat(demoRole).isEqualTo("DEMO_AGENT");

        // Verify seeded demo agent token
        Integer demoAgentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM agent_api_tokens WHERE agent_name = 'demo-agent'",
                Integer.class
        );
        assertThat(demoAgentCount).isNotNull().isEqualTo(1);
    }

    @Test
    void testSeededArticlesAndPodcastsAreAccessibleViaApiAndTraverseReadUpdateChain() throws Exception {
        // Read initial seeded article via API
        mockMvc.perform(get("/api/v1/articles/headless-api-agent-content-sync"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("headless-api-agent-content-sync"))
                .andExpect(jsonPath("$.title").value("Headless API и протоколы автономной синхронизации контента"));

        // Read initial seeded podcast via API
        mockMvc.perform(get("/api/v1/podcasts/autonomous-agent-content-update-ep3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("autonomous-agent-content-update-ep3"))
                .andExpect(jsonPath("$.title").value("Системы и реальность #3: Автономное обновление контента через API"));

        // Perform content update on the seeded article via repository
        Article article = articleRepository.findBySlug("headless-api-agent-content-sync").orElseThrow();
        article.setTitle("Headless API (Updated)");
        articleRepository.saveAndFlush(article);

        // Verify updated article state via API read
        mockMvc.perform(get("/api/v1/articles/headless-api-agent-content-sync"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Headless API (Updated)"));
    }

    @Test
    void testNewlySeededRussianEssaysAndPodcastWithTimecodesExistAndConformToRelationships() throws Exception {
        // Test realistic Russian article 1 navigation & content
        mockMvc.perform(get("/api/v1/articles/high-end-editorial-minimalism"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("high-end-editorial-minimalism"))
                .andExpect(jsonPath("$.title").value("Эстетика инженерного молчания и редакционный минимализм"))
                .andExpect(jsonPath("$.summary").value("Отказ от визуального шума и отладочных элементов как проявление уважения к читателю и архитектурной зрелости."))
                .andExpect(jsonPath("$.content").value(org.hamcrest.Matchers.containsString("Настоящая архитектура не заявляет о себе громкими терминальными логами")));

        // Test realistic Russian article 2 navigation & content
        mockMvc.perform(get("/api/v1/articles/deontic-operators-in-distributed-systems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("deontic-operators-in-distributed-systems"))
                .andExpect(jsonPath("$.title").value("Деонтическая логика и нормативные инварианты в распределенных сервисах"))
                .andExpect(jsonPath("$.summary").value("Формализация нормативных состояний системы через модальную деонтическую логику: обязательства, разрешения и запреты."))
                .andExpect(jsonPath("$.content").value(org.hamcrest.Matchers.containsString("Система функционирует в терминах должного")));

        // Test podcast hub initial episode loading
        mockMvc.perform(get("/api/v1/podcasts/systems-and-reality-ep4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("systems-and-reality-ep4"))
                .andExpect(jsonPath("$.title").value("Системы и реальность #4: Редакционный дизайн и деонтика интерфейсов"))
                .andExpect(jsonPath("$.audioUrl").value("https://dmitryefremov.com/media/podcast/ep4.mp3"))
                .andExpect(jsonPath("$.description").value(org.hamcrest.Matchers.containsString("В четвертом выпуске подкаста «Системы и реальность»")));

        // Verify foundational audio podcast ep5 loading
        mockMvc.perform(get("/api/v1/podcasts/foundational-scrollytelling-ep5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("foundational-scrollytelling-ep5"))
                .andExpect(jsonPath("$.audioUrl").value("https://dmitryefremov.com/media/podcast/ep5.mp3"));

        // Verify foreign key relationship and timecodes for Episode 4
        Optional<PodcastEpisode> ep4Optional = podcastEpisodeRepository.findBySlug("systems-and-reality-ep4");
        assertThat(ep4Optional).isPresent();
        PodcastEpisode ep4 = ep4Optional.get();
        assertThat(ep4.getTimecodes()).hasSize(3);
        assertThat(ep4.getTimecodes())
                .extracting("title")
                .containsExactly(
                        "Введение: Чистый UI и инженерная этика",
                        "Деонтика пользовательского опыта",
                        "Архитектура медиахаба"
                );

        // Verify foreign key relationship and timecodes for foundational Episode 5
        Optional<PodcastEpisode> ep5Optional = podcastEpisodeRepository.findBySlug("foundational-scrollytelling-ep5");
        assertThat(ep5Optional).isPresent();
        PodcastEpisode ep5 = ep5Optional.get();
        assertThat(ep5.getTimecodes()).hasSize(1);
        assertThat(ep5.getTimecodes().get(0).getTitle()).isEqualTo("Введение в High-End Scrollytelling");

        // Verify publication schedules count
        Integer scheduleCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM publication_schedules WHERE title IN ('Эстетика инженерного молчания', 'Системы и реальность #4')",
                Integer.class
        );
        assertThat(scheduleCount).isEqualTo(2);
    }
}
