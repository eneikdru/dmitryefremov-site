package com.dmitryefremov.site.agent;

import com.dmitryefremov.site.agent.domain.AgentApiToken;
import com.dmitryefremov.site.agent.dto.AgentMessageRequest;
import com.dmitryefremov.site.agent.dto.AgentOrderRequest;
import com.dmitryefremov.site.agent.repository.AgentApiTokenRepository;
import com.dmitryefremov.site.agent.repository.AgentMessageRepository;
import com.dmitryefremov.site.agent.repository.AgentOrderRepository;
import com.dmitryefremov.site.agent.repository.ContentAuditLogRepository;
import com.dmitryefremov.site.article.repository.ArticleRepository;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
import com.dmitryefremov.site.essay.repository.PublishedEssayRepository;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AgentSecurityAndE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AgentApiTokenRepository tokenRepository;

    @Autowired
    private ContentAuditLogRepository auditLogRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PodcastEpisodeRepository podcastEpisodeRepository;

    @Autowired
    private PublishedEssayRepository publishedEssayRepository;

    @Autowired
    @Qualifier("siteCanonItemRepository")
    private CanonItemRepository canonItemRepository;

    @Autowired
    private AgentOrderRepository orderRepository;

    @Autowired
    private AgentMessageRepository messageRepository;

    private AgentApiToken validAgentToken;
    private final String rawToken = "e2e-agent-token-999";

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
        tokenRepository.deleteAll();
        articleRepository.deleteAll();
        podcastEpisodeRepository.deleteAll();
        publishedEssayRepository.deleteAll();
        canonItemRepository.deleteAll();
        orderRepository.deleteAll();
        messageRepository.deleteAll();

        validAgentToken = new AgentApiToken("e2e-autonomous-agent", rawToken);
        tokenRepository.save(validAgentToken);
    }

    @Test
    void testSecurityBoundaryUnauthenticatedAccessDenied() throws Exception {
        // GET content without credentials must return 401
        mockMvc.perform(get("/api/v1/agent/content"))
                .andExpect(status().isUnauthorized());

        // PUT article without credentials must return 401
        mockMvc.perform(put("/api/v1/agent/content/articles/unauth-article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test\"}"))
                .andExpect(status().isUnauthorized());

        // PUT podcast without credentials must return 401
        mockMvc.perform(put("/api/v1/agent/content/podcasts/unauth-podcast")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test\"}"))
                .andExpect(status().isUnauthorized());

        // PUT essay without credentials must return 401
        mockMvc.perform(put("/api/v1/agent/content/essays/unauth-essay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test\"}"))
                .andExpect(status().isUnauthorized());

        // PUT canon without credentials must return 401
        mockMvc.perform(put("/api/v1/agent/content/canon/unauth-canon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSecurityBoundaryInvalidTokenAccessDenied() throws Exception {
        mockMvc.perform(get("/api/v1/agent/content")
                        .header("X-API-Token", "malicious-fake-token"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/v1/agent/content/articles/fake-token-article")
                        .header("Authorization", "Bearer malicious-fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSecurityBoundaryNonExistentOrCodePathTraversalDenied() throws Exception {
        // Attempts to access unowned/code resources or path traversal endpoints must be denied (404/400)
        mockMvc.perform(get("/api/v1/agent/code/src/main/App.java")
                        .header("X-API-Token", rawToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/agent/styles/main.css")
                        .header("X-API-Token", rawToken))
                .andExpect(status().isNotFound());

        // Path traversal attempt in slug
        mockMvc.perform(put("/api/v1/agent/content/articles/../../etc/passwd")
                        .header("X-API-Token", rawToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Path Traversal\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testE2EAutonomousUpdateFlow() throws Exception {
        // Step 1: Initial content fetch (Empty JSON response)
        mockMvc.perform(get("/api/v1/agent/content")
                        .header("X-API-Token", rawToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articles", hasSize(0)))
                .andExpect(jsonPath("$.podcasts", hasSize(0)))
                .andExpect(jsonPath("$.essays", hasSize(0)))
                .andExpect(jsonPath("$.canon", hasSize(0)));

        // Step 2: Autonomous Agent updates an Article via JSON
        String articleJson = """
                {
                    "title": "Autonomous AI Article",
                    "summary": "AI summary for autonomous update",
                    "content": "Full content written by AI agent"
                }
                """;

        mockMvc.perform(put("/api/v1/agent/content/articles/e2e-ai-article")
                        .header("X-API-Token", rawToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("e2e-ai-article")))
                .andExpect(jsonPath("$.title", is("Autonomous AI Article")));

        // Step 3: Autonomous Agent updates a Podcast Episode via Markdown Frontmatter
        String podcastMd = """
                ---
                title: Autonomous Podcast Episode 1
                thesis: AI Agents in Headless CMS
                audioUrl: https://cdn.example.com/audio/ep1.mp3
                durationSeconds: 1200
                ---
                Detailed podcast notes and transcript.
                """;

        mockMvc.perform(put("/api/v1/agent/content/podcasts/e2e-ai-podcast")
                        .header("Authorization", "Bearer " + rawToken)
                        .contentType("text/markdown")
                        .content(podcastMd))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("e2e-ai-podcast")))
                .andExpect(jsonPath("$.title", is("Autonomous Podcast Episode 1")))
                .andExpect(jsonPath("$.audioUrl", is("https://cdn.example.com/audio/ep1.mp3")));

        // Step 4: Autonomous Agent updates an Essay via JSON
        String essayJson = """
                {
                    "title": "Autonomous AI Essay",
                    "summary": "E2E AI essay summary",
                    "content": "Full essay content created by autonomous agent"
                }
                """;

        mockMvc.perform(put("/api/v1/agent/content/essays/e2e-ai-essay")
                        .header("X-API-Token", rawToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(essayJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("e2e-ai-essay")))
                .andExpect(jsonPath("$.title", is("Autonomous AI Essay")));

        // Step 5: Autonomous Agent updates a Canon concept via Markdown
        String canonMd = """
                ---
                title: E2E Canon Concept
                type: ONTOLOGY
                domain: PHILOSOPHY_OF_MIND
                summary: Extended mind ontology
                ---
                # Extended Mind Ontology Body
                """;

        mockMvc.perform(put("/api/v1/agent/content/canon/e2e-canon-concept")
                        .header("Authorization", "Bearer " + rawToken)
                        .contentType("text/markdown")
                        .content(canonMd))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("e2e-canon-concept")))
                .andExpect(jsonPath("$.title", is("E2E Canon Concept")));

        // Step 6: Verify content retrieval via RSS feed
        mockMvc.perform(get("/api/v1/agent/content")
                        .header("X-API-Token", rawToken)
                        .header("Accept", "application/rss+xml"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Autonomous AI Article")))
                .andExpect(content().string(containsString("Autonomous Podcast Episode 1")))
                .andExpect(content().string(containsString("Autonomous AI Essay")))
                .andExpect(content().string(containsString("E2E Canon Concept")));

        // Step 7: Agent submits service order and message
        AgentOrderRequest orderRequest = new AgentOrderRequest(
                "E2E Agent Client",
                "agent@autonomous.org",
                "CONTENT_UPDATE",
                "Automated content update workflow"
        );
        mockMvc.perform(post("/api/v1/agent/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated());

        AgentMessageRequest messageRequest = new AgentMessageRequest(
                "E2E Agent Client",
                "agent@autonomous.org",
                "E2E status verification message"
        );
        mockMvc.perform(post("/api/v1/agent/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageRequest)))
                .andExpect(status().isCreated());

        // Step 8: Verify Telemetry statistics reflect interaction activity
        mockMvc.perform(get("/api/v1/agent/telemetry"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders", is(1)))
                .andExpect(jsonPath("$.totalMessages", is(1)))
                .andExpect(jsonPath("$.totalInteractions", is(2)));

        // Step 9: Verify Database state and audit logs
        assertThat(articleRepository.findBySlug("e2e-ai-article")).isPresent();
        assertThat(podcastEpisodeRepository.findBySlug("e2e-ai-podcast")).isPresent();
        assertThat(publishedEssayRepository.findBySlug("e2e-ai-essay")).isPresent();
        assertThat(canonItemRepository.findBySlug("e2e-canon-concept")).isPresent();
        assertThat(auditLogRepository.findByAgentId(validAgentToken.getId())).hasSize(4);
    }
}
