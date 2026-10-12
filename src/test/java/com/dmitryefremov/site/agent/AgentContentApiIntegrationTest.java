package com.dmitryefremov.site.agent;

import com.dmitryefremov.site.agent.domain.AgentApiToken;
import com.dmitryefremov.site.agent.repository.AgentApiTokenRepository;
import com.dmitryefremov.site.agent.repository.ContentAuditLogRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AgentContentApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgentApiTokenRepository tokenRepository;

    @Autowired
    private ContentAuditLogRepository auditLogRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PodcastEpisodeRepository podcastEpisodeRepository;

    private AgentApiToken validAgentToken;
    private final String rawToken = "secret-agent-token-123";

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        tokenRepository.deleteAll();
        articleRepository.deleteAll();
        podcastEpisodeRepository.deleteAll();

        validAgentToken = new AgentApiToken("autonomous-agent-1", rawToken);
        tokenRepository.save(validAgentToken);
    }

    @Test
    void testGetContentWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/agent/content")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.message", containsString("Invalid or missing API token")));
    }

    @Test
    void testGetContentWithInvalidTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/agent/content")
                        .header("X-API-Token", "invalid-token-xyz")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)));
    }

    @Test
    void testGetContentAsJsonSuccess() throws Exception {
        Article article = new Article("test-slug-1", "Test Title", "Test Summary", "Test Content", OffsetDateTime.now());
        articleRepository.save(article);

        PodcastEpisode episode = new PodcastEpisode("podcast-slug-1", "Podcast Title", "Thesis", "Desc", "http://audio.mp3", "http://media.link", 1800, OffsetDateTime.now());
        podcastEpisodeRepository.save(episode);

        mockMvc.perform(get("/api/v1/agent/content")
                        .header("X-API-Token", rawToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articles", hasSize(1)))
                .andExpect(jsonPath("$.articles[0].slug", is("test-slug-1")))
                .andExpect(jsonPath("$.podcasts", hasSize(1)))
                .andExpect(jsonPath("$.podcasts[0].slug", is("podcast-slug-1")));
    }

    @Test
    void testGetContentAsRssSuccess() throws Exception {
        Article article = new Article("rss-article-1", "RSS Article Title", "Summary", "Content", OffsetDateTime.now());
        articleRepository.save(article);

        mockMvc.perform(get("/api/v1/agent/content")
                        .header("X-API-Token", rawToken)
                        .header("Accept", "application/rss+xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
                .andExpect(content().string(containsString("<rss version=\"2.0\"")))
                .andExpect(content().string(containsString("RSS Article Title")));
    }

    @Test
    void testUpdateArticleJsonCreateAndAuditLog() throws Exception {
        String jsonPayload = """
                {
                    "title": "Autonomous Article Title",
                    "summary": "Summary of autonomous article",
                    "content": "# Autonomous Article Content"
                }
                """;

        mockMvc.perform(put("/api/v1/agent/content/articles/new-autonomous-article")
                        .header("X-API-Token", rawToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("new-autonomous-article")))
                .andExpect(jsonPath("$.title", is("Autonomous Article Title")))
                .andExpect(jsonPath("$.summary", is("Summary of autonomous article")));

        assertThat(articleRepository.findBySlug("new-autonomous-article")).isPresent();
        assertThat(auditLogRepository.findByAgentId(validAgentToken.getId())).hasSize(1);
    }

    @Test
    void testUpdateArticleMarkdownWithFrontmatter() throws Exception {
        String markdownPayload = """
                ---
                title: Markdown Frontmatter Title
                summary: Markdown Frontmatter Summary
                ---
                # Body Header

                Body content goes here.
                """;

        mockMvc.perform(put("/api/v1/agent/content/articles/md-frontmatter-article")
                        .header("Authorization", "Bearer " + rawToken)
                        .contentType("text/markdown")
                        .content(markdownPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("md-frontmatter-article")))
                .andExpect(jsonPath("$.title", is("Markdown Frontmatter Title")))
                .andExpect(jsonPath("$.summary", is("Markdown Frontmatter Summary")))
                .andExpect(jsonPath("$.content", containsString("Body content goes here.")));

        assertThat(articleRepository.findBySlug("md-frontmatter-article")).isPresent();
    }

    @Test
    void testUpdatePodcastJsonCreateAndAuditLog() throws Exception {
        String jsonPayload = """
                {
                    "title": "Autonomous Podcast Episode",
                    "thesis": "Thesis statement",
                    "description": "Full description of episode",
                    "audioUrl": "https://example.com/episode1.mp3",
                    "durationSeconds": 3600
                }
                """;

        mockMvc.perform(put("/api/v1/agent/content/podcasts/ep-101")
                        .header("X-API-Token", rawToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("ep-101")))
                .andExpect(jsonPath("$.title", is("Autonomous Podcast Episode")))
                .andExpect(jsonPath("$.audioUrl", is("https://example.com/episode1.mp3")));

        assertThat(podcastEpisodeRepository.findBySlug("ep-101")).isPresent();
        assertThat(auditLogRepository.findByAgentId(validAgentToken.getId())).hasSize(1);
    }

    @Test
    void testUpdatePodcastMarkdownWithFrontmatter() throws Exception {
        String markdownPayload = """
                ---
                title: Podcast MD Title
                thesis: Podcast MD Thesis
                audioUrl: https://media.org/audio.mp3
                durationSeconds: 2400
                ---
                This is the podcast description text.
                """;

        mockMvc.perform(put("/api/v1/agent/content/podcasts/md-podcast-1")
                        .header("X-API-Token", rawToken)
                        .contentType("text/markdown")
                        .content(markdownPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("md-podcast-1")))
                .andExpect(jsonPath("$.title", is("Podcast MD Title")))
                .andExpect(jsonPath("$.thesis", is("Podcast MD Thesis")))
                .andExpect(jsonPath("$.audioUrl", is("https://media.org/audio.mp3")));

        assertThat(podcastEpisodeRepository.findBySlug("md-podcast-1")).isPresent();
    }

    @Test
    void testUpdatePodcastJsonWithQuotesAndConceptLinks() throws Exception {
        String jsonPayload = """
                {
                    "title": "Andy Clark & Extended Mind",
                    "thesis": "The mind extends into external tools and media.",
                    "description": "Deep dive into Andy Clark, Susan Hurley, and extended mind theory.",
                    "audioUrl": "https://example.com/extended-mind.mp3",
                    "youtubeUrl": "https://youtube.com/watch?v=extmind",
                    "conceptLinks": "https://dmitryefremov.com/ontology/extended-mind",
                    "durationSeconds": 3600,
                    "timecodes": [
                        {
                            "timeOffsetSeconds": 0,
                            "title": "Introduction to Extended Mind",
                            "displayOrder": 1
                        }
                    ],
                    "quotes": [
                        {
                            "quoteText": "Where does the mind stop and the rest of the world begin?",
                            "speaker": "Andy Clark",
                            "timeOffsetSeconds": 120,
                            "conceptLinks": "https://dmitryefremov.com/ontology/extended-mind",
                            "displayOrder": 1
                        }
                    ]
                }
                """;

        mockMvc.perform(put("/api/v1/agent/content/podcasts/andy-clark-extended-mind")
                        .header("X-API-Token", rawToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("andy-clark-extended-mind")))
                .andExpect(jsonPath("$.title", is("Andy Clark & Extended Mind")))
                .andExpect(jsonPath("$.conceptLinks", is("https://dmitryefremov.com/ontology/extended-mind")))
                .andExpect(jsonPath("$.timecodes", hasSize(1)))
                .andExpect(jsonPath("$.quotes", hasSize(1)))
                .andExpect(jsonPath("$.quotes[0].speaker", is("Andy Clark")))
                .andExpect(jsonPath("$.quotes[0].quoteText", containsString("Where does the mind stop")));

        assertThat(podcastEpisodeRepository.findBySlug("andy-clark-extended-mind")).isPresent();
    }

    @Test
    void testUpdateArticleInvalidPayloadReturnsBadRequest() throws Exception {
        String markdownPayload = "   ";

        mockMvc.perform(put("/api/v1/agent/content/articles/invalid-article")
                        .header("X-API-Token", rawToken)
                        .contentType("text/markdown")
                        .content(markdownPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Markdown payload cannot be empty")));
    }
}
