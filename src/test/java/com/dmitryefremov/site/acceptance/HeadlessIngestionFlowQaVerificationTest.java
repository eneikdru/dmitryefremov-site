package com.dmitryefremov.site.acceptance;

import com.dmitryefremov.site.agent.domain.AgentApiToken;
import com.dmitryefremov.site.agent.repository.AgentApiTokenRepository;
import com.dmitryefremov.site.agent.repository.ContentAuditLogRepository;
import com.dmitryefremov.site.article.repository.ArticleRepository;
import com.dmitryefremov.site.canon.repository.CanonCrossReferenceRepository;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HeadlessIngestionFlowQaVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CanonItemRepository canonItemRepository;

    @Autowired
    private CanonCrossReferenceRepository canonCrossReferenceRepository;

    @Autowired
    private AgentApiTokenRepository tokenRepository;

    @Autowired
    private ContentAuditLogRepository auditLogRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PodcastEpisodeRepository podcastEpisodeRepository;

    @Autowired
    private SparkRepository sparkRepository;

    private final String validAgentToken = "agent-headless-secret-token-123";

    @BeforeEach
    void setUp() {
        canonCrossReferenceRepository.deleteAll();
        canonItemRepository.deleteAll();
        auditLogRepository.deleteAll();
        tokenRepository.deleteAll();
        articleRepository.deleteAll();
        podcastEpisodeRepository.deleteAll();
        sparkRepository.deleteAll();

        AgentApiToken token = new AgentApiToken("qa-mock-ai-agent", validAgentToken);
        tokenRepository.save(token);
    }

    @Test
    @DisplayName("Given a mock AI agent POSTs a valid new concept, then a subsequent GET confirms it is immediately returned in live data feed")
    void testMockAgentPostConceptAndGetLiveFeedConfirmation() throws Exception {
        String conceptJson = """
                {
                    "slug": "agentic-ontology-concept",
                    "title": "Agentic Ontology Concept",
                    "summary": "Summary of agentic ontology concept for headless ingestion",
                    "content": "Detailed specification of autonomous agentic ontology.",
                    "domain": "AUTONOMOUS_ENGINEERING",
                    "type": "ONTOLOGY"
                }
                """;

        // Step 1: Mock AI agent POSTs valid concept
        mockMvc.perform(post("/api/v1/canon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conceptJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug", is("agentic-ontology-concept")))
                .andExpect(jsonPath("$.title", is("Agentic Ontology Concept")))
                .andExpect(jsonPath("$.domain", is("AUTONOMOUS_ENGINEERING")))
                .andExpect(jsonPath("$.type", is("ONTOLOGY")));

        // Step 2: Confirm concept is immediately returned by slug GET endpoint
        mockMvc.perform(get("/api/v1/canon/agentic-ontology-concept")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("agentic-ontology-concept")))
                .andExpect(jsonPath("$.title", is("Agentic Ontology Concept")))
                .andExpect(jsonPath("$.summary", is("Summary of agentic ontology concept for headless ingestion")));

        // Step 3: Confirm concept appears immediately in live canon feed GET endpoint
        mockMvc.perform(get("/api/v1/canon")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].slug", is("agentic-ontology-concept")));

        assertThat(canonItemRepository.findBySlug("agentic-ontology-concept")).isPresent();
    }

    @Test
    @DisplayName("Given an unauthenticated request attempting to POST/PUT to protected content ingestion, then a 401 is returned")
    void testUnauthenticatedPostOrPutReturns401() throws Exception {
        String articleJson = """
                {
                    "title": "Unauthenticated Article Title",
                    "summary": "Unauth Summary",
                    "content": "Unauth Content"
                }
                """;

        // PUT attempt on protected article endpoint without header
        mockMvc.perform(put("/api/v1/agent/content/articles/unauth-article")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.message", containsString("Invalid or missing API token")));

        // GET attempt on protected content feed without header
        mockMvc.perform(get("/api/v1/agent/content"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)));

        // PUT attempt with invalid token header
        mockMvc.perform(put("/api/v1/agent/content/articles/unauth-article")
                        .header("X-API-Token", "invalid-token-value")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)));
    }

    @Test
    @DisplayName("Given headless API content ingestion, when updated by agent, then content appears in live feeds")
    void testHeadlessAgentContentIngestionFlow() throws Exception {
        String articleJson = """
                {
                    "title": "Headless Ingested Article",
                    "summary": "Summary of article ingested via Headless API",
                    "content": "Full text content of article."
                }
                """;

        mockMvc.perform(put("/api/v1/agent/content/articles/headless-article-1")
                        .header("X-API-Token", validAgentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articleJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("headless-article-1")))
                .andExpect(jsonPath("$.title", is("Headless Ingested Article")));

        // Verify live agent content feed returns the ingested article
        mockMvc.perform(get("/api/v1/agent/content")
                        .header("X-API-Token", validAgentToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articles", hasSize(1)))
                .andExpect(jsonPath("$.articles[0].slug", is("headless-article-1")));

        // Verify public articles endpoint returns the ingested article
        mockMvc.perform(get("/api/v1/articles")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].slug", is("headless-article-1")));
    }

    @Test
    @DisplayName("Given Telegram webhook spark ingestion, when posted, then spark appears immediately in live feed")
    void testTelegramWebhookSparkIngestionFlow() throws Exception {
        String telegramWebhookPayload = """
                {
                  "update_id": 777888999,
                  "channel_post": {
                    "message_id": 8192,
                    "sender_chat": {
                      "id": -1001234567890,
                      "title": "Dmitry Efremov Channel",
                      "type": "channel"
                    },
                    "chat": {
                      "id": -1001234567890,
                      "title": "Dmitry Efremov Channel",
                      "type": "channel"
                    },
                    "date": 1760304000,
                    "text": "Automated headless ingestion test spark #a2a #publishing",
                    "author_signature": "Dmitry Efremov"
                  }
                }
                """;

        mockMvc.perform(post("/api/v1/telegram/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(telegramWebhookPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telegramMessageId").value(8192))
                .andExpect(jsonPath("$.domain").value("Agentic Economy (A2A)"));

        // Subsequent GET on live sparks feed confirms it is immediately returned
        mockMvc.perform(get("/api/v1/sparks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].telegramMessageId").value(8192))
                .andExpect(jsonPath("$.content[0].content").value(containsString("Automated headless ingestion test spark")));
    }
}
