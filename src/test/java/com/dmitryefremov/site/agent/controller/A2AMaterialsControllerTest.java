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

    private final String validApiKey = "valid-a2a-key-123456";

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        articleRepository.deleteAll();
        podcastEpisodeRepository.deleteAll();

        AgentApiToken token = new AgentApiToken("a2a-test-agent", validApiKey);
        tokenRepository.save(token);

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
    void testGetMaterialsSecondaryEndpointPath() throws Exception {
        mockMvc.perform(get("/api/v1/materials")
                        .header("Authorization", "Bearer " + validApiKey)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("AUTHENTIC")));
    }
}
