package com.dmitryefremov.site.acceptance;

import com.dmitryefremov.site.agent.domain.AgentApiToken;
import com.dmitryefremov.site.agent.dto.CanonUpdateRequestDto;
import com.dmitryefremov.site.agent.repository.AgentApiTokenRepository;
import com.dmitryefremov.site.canon.domain.CanonDomain;
import com.dmitryefremov.site.canon.domain.CanonType;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class HeadlessIngestionFlowVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AgentApiTokenRepository tokenRepository;

    @Autowired
    @Qualifier("siteCanonItemRepository")
    private CanonItemRepository canonItemRepository;

    private final String validTokenString = "headless-mock-agent-token-12345";
    private final OffsetDateTime fixedPublishedAt = OffsetDateTime.of(2026, 10, 24, 12, 0, 0, 0, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
        canonItemRepository.deleteAll();

        AgentApiToken mockAgentToken = new AgentApiToken("mock-ai-ingestion-agent", validTokenString);
        tokenRepository.save(mockAgentToken);
    }

    @Test
    @DisplayName("Given an unauthenticated request when attempting to post or put content then 401 is returned")
    void testUnauthenticatedRequestDeniedWith401() throws Exception {
        CanonUpdateRequestDto dto = new CanonUpdateRequestDto(
                "Unauthenticated Concept",
                CanonType.ONTOLOGY,
                CanonDomain.PHILOSOPHY_OF_MIND,
                "Attempting unauthorized concept creation",
                "Full body content",
                fixedPublishedAt
        );

        mockMvc.perform(put("/api/v1/agent/content/canon/unauth-concept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.message", is("Invalid or missing API token")));
    }

    @Test
    @DisplayName("Given a mock AI agent posts/puts a valid new concept then subsequent GET returns it in the live feed")
    void testMockAiAgentIngestsConceptAndAppearsInLiveFeed() throws Exception {
        String slug = "extended-mind-concept";
        CanonUpdateRequestDto newConceptDto = new CanonUpdateRequestDto(
                "Extended Mind Concept",
                CanonType.ONTOLOGY,
                CanonDomain.PHILOSOPHY_OF_MIND,
                "A core concept in Andy Clark's extended mind theory.",
                "# Extended Mind Concept\n\nThe cognitive process extends into physical artifacts.",
                fixedPublishedAt
        );

        // 1. Authenticated Mock AI Agent POSTs / PUTs valid new concept
        mockMvc.perform(put("/api/v1/agent/content/canon/" + slug)
                        .header("X-API-Token", validTokenString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newConceptDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is(slug)))
                .andExpect(jsonPath("$.title", is("Extended Mind Concept")))
                .andExpect(jsonPath("$.type", is("ONTOLOGY")))
                .andExpect(jsonPath("$.domain", is("PHILOSOPHY_OF_MIND")));

        // 2. Subsequent GET request confirms the concept is immediately returned in the live canon feed by slug
        mockMvc.perform(get("/api/v1/canon/" + slug)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is(slug)))
                .andExpect(jsonPath("$.title", is("Extended Mind Concept")))
                .andExpect(jsonPath("$.summary", is("A core concept in Andy Clark's extended mind theory.")));

        // 3. Subsequent GET request to full list live data feed contains the ingested concept
        mockMvc.perform(get("/api/v1/canon")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].slug", is(slug)))
                .andExpect(jsonPath("$.content[0].title", is("Extended Mind Concept")));

        // 4. Subsequent GET request to agent content live feed also contains the concept
        mockMvc.perform(get("/api/v1/agent/content")
                        .header("X-API-Token", validTokenString)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canon[0].slug", is(slug)))
                .andExpect(jsonPath("$.canon[0].title", is("Extended Mind Concept")));
    }
}
