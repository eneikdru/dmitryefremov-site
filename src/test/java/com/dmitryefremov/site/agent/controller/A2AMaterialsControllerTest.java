package com.dmitryefremov.site.agent.controller;

import com.dmitryefremov.site.agent.domain.AgentApiToken;
import com.dmitryefremov.site.agent.repository.AgentApiTokenRepository;
import com.dmitryefremov.site.article.domain.Article;
import com.dmitryefremov.site.article.repository.ArticleRepository;
import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class A2AMaterialsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgentApiTokenRepository tokenRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PodcastEpisodeRepository podcastEpisodeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String validApiKey = "valid-a2a-key-123456";
    private final String credentialsTableApiKey = "cred-a2a-key-7890";
    private final String apiKeysTableApiKey = "apikeys-a2a-key-4567";

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        articleRepository.deleteAll();
        podcastEpisodeRepository.deleteAll();

        jdbcTemplate.execute("DELETE FROM api_credentials WHERE client_id = 'a2a-cred-client'");
        jdbcTemplate.execute("DELETE FROM api_keys WHERE key_identity = 'a2a-key-identity'");

        AgentApiToken token = new AgentApiToken("a2a-test-agent", validApiKey);
        tokenRepository.save(token);

        jdbcTemplate.update(
                "INSERT INTO api_credentials (client_id, api_key_hash, role, enabled, created_at) VALUES (?, ?, ?, true, CURRENT_TIMESTAMP)",
                "a2a-cred-client", credentialsTableApiKey, "AGENT_ROLE"
        );

        jdbcTemplate.update(
                "INSERT INTO api_keys (key_identity, key_hash, status, enabled, created_at) VALUES (?, ?, 'ACTIVE', true, CURRENT_TIMESTAMP)",
                "a2a-key-identity", apiKeysTableApiKey
        );

        Article article = new Article(
                "scrollytelling-a2a-materials",
                "High-End Scrollytelling & Authentic Materials",
                "Summary of authentic materials",
                "# Full authentic content body",
                OffsetDateTime.now()
        );
        articleRepository.save(article);

        PodcastEpisode episode = new PodcastEpisode(
                "a2a-podcast-materials-ep1",
                "Systems and Reality #1",
                "Podcast thesis",
                "Podcast description",
                "https://dmitryefremov.com/podcast/ep1.mp3",
                "https://dmitryefremov.com/podcast/ep1",
                1800,
                OffsetDateTime.now()
        );
        podcastEpisodeRepository.save(episode);
    }

    @Test
    void testGetMaterialsWithValidApiKeyInAuthorizationHeaderReturns200AndStructuredMaterials() throws Exception {
        mockMvc.perform(get("/api/v1/a2a/materials")
                        .header("Authorization", "Bearer " + validApiKey)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("AUTHENTIC")))
                .andExpect(jsonPath("$.extractedAt", notNullValue()))
                .andExpect(jsonPath("$.articles", hasSize(1)))
                .andExpect(jsonPath("$.articles[0].slug", is("scrollytelling-a2a-materials")))
                .andExpect(jsonPath("$.articles[0].title", is("High-End Scrollytelling & Authentic Materials")))
                .andExpect(jsonPath("$.podcasts", hasSize(1)))
                .andExpect(jsonPath("$.podcasts[0].slug", is("a2a-podcast-materials-ep1")));
    }

    @Test
    void testGetMaterialsWithValidApiKeyInXApiTokenHeaderReturns200AndStructuredMaterials() throws Exception {
        mockMvc.perform(get("/api/v1/a2a/materials")
                        .header("X-API-Token", validApiKey)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("AUTHENTIC")))
                .andExpect(jsonPath("$.articles", hasSize(1)))
                .andExpect(jsonPath("$.podcasts", hasSize(1)));
    }

    @Test
    void testGetMaterialsWithValidKeyInApiCredentialsTableReturns200() throws Exception {
        mockMvc.perform(get("/api/v1/a2a/materials")
                        .header("X-API-Token", credentialsTableApiKey)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("AUTHENTIC")))
                .andExpect(jsonPath("$.articles", hasSize(1)));
    }

    @Test
    void testGetMaterialsWithValidKeyInApiKeysTableReturns200() throws Exception {
        mockMvc.perform(get("/api/v1/a2a/materials")
                        .header("Authorization", "Bearer " + apiKeysTableApiKey)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("AUTHENTIC")))
                .andExpect(jsonPath("$.podcasts", hasSize(1)));
    }

    @Test
    void testGetMaterialsWithInvalidApiKeyReturns401Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/a2a/materials")
                        .header("Authorization", "Bearer invalid-key-xyz999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.message", containsString("Unauthorized: Invalid or missing API key")));
    }

    @Test
    void testGetMaterialsWithoutApiKeyReturns401Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/a2a/materials")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)));
    }

    @Test
    void testGetMaterialsWithEmptyAuthorizationHeaderReturns401Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/a2a/materials")
                        .header("Authorization", "Bearer ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)));
    }

    @Test
    void testGetMaterialsWithBlankXApiTokenHeaderReturns401Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/a2a/materials")
                        .header("X-API-Token", "   ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)));
    }

    @Test
    void testGetMaterialsSecondaryEndpointPathWithValidKey() throws Exception {
        mockMvc.perform(get("/api/v1/materials")
                        .header("Authorization", "Bearer " + validApiKey)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("AUTHENTIC")));
    }

    @Test
    void testGetMaterialsSecondaryEndpointPathWithoutValidKeyReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/materials")
                        .header("Authorization", "Bearer bad-key-000")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)));
    }
}
