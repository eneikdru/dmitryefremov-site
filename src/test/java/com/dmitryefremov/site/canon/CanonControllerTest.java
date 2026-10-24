package com.dmitryefremov.site.canon;

import com.dmitryefremov.site.canon.controller.CanonController;
import com.dmitryefremov.site.canon.domain.CanonDomain;
import com.dmitryefremov.site.canon.domain.CanonRelationshipType;
import com.dmitryefremov.site.canon.domain.CanonType;
import com.dmitryefremov.site.canon.dto.*;
import com.dmitryefremov.site.canon.service.CanonService;
import com.dmitryefremov.site.canon.service.CorpusSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CanonController.class)
class CanonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "siteCanonService")
    private CanonService canonService;

    @MockBean(name = "siteCorpusSearchService")
    private CorpusSearchService corpusSearchService;

    @Test
    void testListCanonItemsFilteredByDomainReturns200() throws Exception {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 12, 10, 0, 0, 0, ZoneOffset.UTC);
        CanonItemDto dto = new CanonItemDto(
                1L,
                "architecture-of-autonomous-systems",
                "The Architecture of Autonomous Systems",
                CanonType.MANIFESTO,
                CanonDomain.AUTONOMOUS_ENGINEERING,
                "Summary",
                "# Content",
                publishedAt,
                publishedAt,
                null,
                List.of(),
                List.of()
        );

        CanonPageDto pageDto = new CanonPageDto(List.of(dto), 0, 20, 1L, 1);

        when(canonService.getCanonItems(eq(CanonDomain.AUTONOMOUS_ENGINEERING), eq(null), eq(0), eq(20)))
                .thenReturn(pageDto);

        mockMvc.perform(get("/api/v1/canon")
                        .param("domain", "AUTONOMOUS_ENGINEERING")
                        .param("page", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].slug", is("architecture-of-autonomous-systems")))
                .andExpect(jsonPath("$.content[0].domain", is("AUTONOMOUS_ENGINEERING")))
                .andExpect(jsonPath("$.content[0].type", is("MANIFESTO")));
    }

    @Test
    void testGetCanonItemBySlugReturns200WithCrossReferences() throws Exception {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 12, 10, 0, 0, 0, ZoneOffset.UTC);

        CanonCrossReferenceDto ref = new CanonCrossReferenceDto(
                1L,
                "architecture-of-autonomous-systems",
                "The Architecture of Autonomous Systems",
                "extended-mind-infrastructure",
                "Extended Mind Infrastructure",
                CanonRelationshipType.REFERENCES,
                "State persistence link",
                publishedAt
        );

        CanonItemDto dto = new CanonItemDto(
                1L,
                "architecture-of-autonomous-systems",
                "The Architecture of Autonomous Systems",
                CanonType.MANIFESTO,
                CanonDomain.AUTONOMOUS_ENGINEERING,
                "Summary",
                "# Content",
                publishedAt,
                publishedAt,
                null,
                List.of(ref),
                List.of()
        );

        when(canonService.getCanonItemBySlug("architecture-of-autonomous-systems"))
                .thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/v1/canon/architecture-of-autonomous-systems")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug", is("architecture-of-autonomous-systems")))
                .andExpect(jsonPath("$.domain", is("AUTONOMOUS_ENGINEERING")))
                .andExpect(jsonPath("$.outgoingReferences", hasSize(1)))
                .andExpect(jsonPath("$.outgoingReferences[0].targetSlug", is("extended-mind-infrastructure")))
                .andExpect(jsonPath("$.outgoingReferences[0].relationshipType", is("REFERENCES")));
    }

    @Test
    void testGetCanonItemMediaLinksReturns200() throws Exception {
        OffsetDateTime createdAt = OffsetDateTime.of(2026, 10, 24, 12, 0, 0, 0, ZoneOffset.UTC);

        ConceptMediaLinkDto link = new ConceptMediaLinkDto(
                1L, 10L, "canon-slug", "Canon Title", "PODCAST",
                100L, "podcast-slug", "Podcast Episode Title",
                null, null, null,
                null, null,
                null, null, null,
                "EXPLAINS", "Link description", createdAt
        );

        when(canonService.getMediaLinksForCanonItem("canon-slug")).thenReturn(List.of(link));

        mockMvc.perform(get("/api/v1/canon/canon-slug/links")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].canonItemSlug", is("canon-slug")))
                .andExpect(jsonPath("$[0].mediaType", is("PODCAST")))
                .andExpect(jsonPath("$[0].podcastEpisodeSlug", is("podcast-slug")));
    }

    @Test
    void testSearchCorpusEndpointReturns200() throws Exception {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 24, 12, 0, 0, 0, ZoneOffset.UTC);

        SearchResultItemDto item = new SearchResultItemDto(
                "canon-modal-logic", "Modal Logic", "modal-logic", "CANON",
                "Summary", "Snippet text", "/canon/modal-logic", 10.0, publishedAt
        );

        CorpusSearchDto searchDto = new CorpusSearchDto("modal", 1, List.of(item));

        when(corpusSearchService.searchCorpus("modal")).thenReturn(searchDto);

        mockMvc.perform(get("/api/v1/canon/search")
                        .param("q", "modal")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.query", is("modal")))
                .andExpect(jsonPath("$.totalResults", is(1)))
                .andExpect(jsonPath("$.results[0].slug", is("modal-logic")))
                .andExpect(jsonPath("$.results[0].kind", is("CANON")));
    }

    @Test
    void testGetCanonItemBySlugNotFoundReturns404() throws Exception {
        when(canonService.getCanonItemBySlug("unknown-slug"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/canon/unknown-slug")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Canon item with slug 'unknown-slug' was not found")));
    }

    @Test
    void testAddCrossReferenceReturns201() throws Exception {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 12, 10, 0, 0, 0, ZoneOffset.UTC);
        CanonCrossReferenceDto xref = new CanonCrossReferenceDto(
                10L,
                "source-slug",
                "Source Title",
                "target-slug",
                "Target Title",
                CanonRelationshipType.EXTENDS,
                "Extends definition",
                publishedAt
        );

        when(canonService.addCrossReference(eq("source-slug"), eq("target-slug"), eq(CanonRelationshipType.EXTENDS), eq("Extends definition")))
                .thenReturn(xref);

        mockMvc.perform(post("/api/v1/canon/source-slug/references/target-slug")
                        .param("relationshipType", "EXTENDS")
                        .param("description", "Extends definition")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sourceSlug", is("source-slug")))
                .andExpect(jsonPath("$.targetSlug", is("target-slug")))
                .andExpect(jsonPath("$.relationshipType", is("EXTENDS")));
    }
}
