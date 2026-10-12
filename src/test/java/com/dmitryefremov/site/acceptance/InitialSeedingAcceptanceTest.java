package com.dmitryefremov.site.acceptance;

import com.dmitryefremov.site.article.domain.Article;
import com.dmitryefremov.site.article.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
}
