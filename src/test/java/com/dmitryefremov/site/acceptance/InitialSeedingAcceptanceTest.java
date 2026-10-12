package com.dmitryefremov.site.acceptance;

import com.dmitryefremov.site.article.domain.Article;
import com.dmitryefremov.site.article.repository.ArticleRepository;
import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import com.dmitryefremov.site.spark.repository.SparkRepository;
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

    @Autowired
    private SparkRepository sparkRepository;

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
        // Test article 1
        mockMvc.perform(get("/api/v1/articles/high-end-editorial-minimalism"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("high-end-editorial-minimalism"))
                .andExpect(jsonPath("$.title").value("Эстетика инженерного молчания и редакционный минимализм"));

        // Test article 2
        mockMvc.perform(get("/api/v1/articles/deontic-operators-in-distributed-systems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("deontic-operators-in-distributed-systems"))
                .andExpect(jsonPath("$.title").value("Деонтическая логика и нормативные инварианты в распределенных сервисах"));

        // Test podcast episode 4
        mockMvc.perform(get("/api/v1/podcasts/systems-and-reality-ep4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("systems-and-reality-ep4"))
                .andExpect(jsonPath("$.title").value("Системы и реальность #4: Редакционный дизайн и деонтика интерфейсов"));

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

        // Verify publication schedules count
        Integer scheduleCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM publication_schedules WHERE title IN ('Эстетика инженерного молчания', 'Системы и реальность #4')",
                Integer.class
        );
        assertThat(scheduleCount).isEqualTo(2);
    }

    @Test
    void testAcceptanceSeededManifestosPhilosophyPodcastAndSparksExist() throws Exception {
        // Verify Tier 1 Canon Manifestos via API
        mockMvc.perform(get("/api/v1/articles/the-canon-sovereign-node-manifesto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("the-canon-sovereign-node-manifesto"))
                .andExpect(jsonPath("$.title").value("Манифест Суверенного Цифрового Узла: Автономность и Деонтика"));

        mockMvc.perform(get("/api/v1/essays/extended-mind-and-autonomous-agency-manifesto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("extended-mind-and-autonomous-agency-manifesto"))
                .andExpect(jsonPath("$.title").value("Расширенный разум и автономная агентность"));

        // Verify Tier 2 Stream Podcast Episode 5 with philosophy timecodes
        mockMvc.perform(get("/api/v1/podcasts/philosophy-of-mind-extended-cognition-ep5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("philosophy-of-mind-extended-cognition-ep5"))
                .andExpect(jsonPath("$.title").value("Системы и реальность #5: Философия разума и архитектура расширенного познания"));

        Optional<PodcastEpisode> ep5Optional = podcastEpisodeRepository.findBySlug("philosophy-of-mind-extended-cognition-ep5");
        assertThat(ep5Optional).isPresent();
        PodcastEpisode ep5 = ep5Optional.get();
        assertThat(ep5.getTimecodes()).hasSize(4);
        assertThat(ep5.getTimecodes())
                .extracting("title")
                .containsExactly(
                        "Введение: От модальной логики к сознанию",
                        "Когнитивные науки и вычислительный разум",
                        "Понятие сознания и отказ от призрака в машине",
                        "Расширенный разум и суверенная агентность"
                );

        // Verify Tier 3 Sparks via API
        mockMvc.perform(get("/api/v1/sparks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(4));

        mockMvc.perform(get("/api/v1/sparks/spark-mvp-pragmatics-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("spark-mvp-pragmatics-01"))
                .andExpect(jsonPath("$.domain").value("AUTONOMOUS_ENGINEERING"));

        assertThat(sparkRepository.count()).isEqualTo(4);
    }
}
