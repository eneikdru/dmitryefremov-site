package com.dmitryefremov.site.knowledgebase;

import com.dmitryefremov.site.knowledgebase.controller.KnowledgeBaseSearchController;
import com.dmitryefremov.site.knowledgebase.dto.*;
import com.dmitryefremov.site.knowledgebase.service.KnowledgeBaseSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KnowledgeBaseSearchController.class)
class KnowledgeBaseSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KnowledgeBaseSearchService searchService;

    @Test
    void testSearchEndpointSuccess() throws Exception {
        SearchResultItem item = new SearchResultItem(1L, "rasshirennyy-razum", "Title", SearchDomain.CANON, "Summary", "Snippet", "/canon/rasshirennyy-razum", 1.0, OffsetDateTime.now(), 2);
        SearchResponse response = new SearchResponse("разум", 1, List.of(item));

        when(searchService.search(eq("разум"), eq(List.of(SearchDomain.CANON, SearchDomain.PODCAST)), eq(20), eq(0)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/search")
                        .param("q", "разум")
                        .param("domains", "CANON,PODCAST")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query", is("разум")))
                .andExpect(jsonPath("$.totalResults", is(1)))
                .andExpect(jsonPath("$.items[0].slug", is("rasshirennyy-razum")));
    }

    @Test
    void testSearchEndpointBadRequestWhenQueryEmpty() throws Exception {
        mockMvc.perform(get("/api/v1/search")
                        .param("q", " ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Search query 'q' must not be empty")));
    }

    @Test
    void testListGraphNodesEndpoint() throws Exception {
        GraphNode node = new GraphNode(1L, "slug1", "Title 1", SearchDomain.CANON, "Summary", "/url", OffsetDateTime.now());
        GraphNodePage page = new GraphNodePage(List.of(node), 0, 10, 1L, 1);

        when(searchService.listGraphNodes(eq(SearchDomain.CANON), eq(0), eq(10))).thenReturn(page);

        mockMvc.perform(get("/api/v1/graph/nodes")
                        .param("domain", "CANON")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.content[0].slug", is("slug1")));
    }

    @Test
    void testGetGraphNodeBySlugFound() throws Exception {
        GraphNode node = new GraphNode(1L, "rasshirennyy-razum-i-vneshnyaya-pamyat", "Title", SearchDomain.CANON, "Summary", "/url", OffsetDateTime.now());
        GraphNodeDetail detail = new GraphNodeDetail(node, "Content", List.of(), List.of(), Map.of("llmsTxt", "/llms.txt"));

        when(searchService.getGraphNodeBySlug("rasshirennyy-razum-i-vneshnyaya-pamyat")).thenReturn(Optional.of(detail));

        mockMvc.perform(get("/api/v1/graph/nodes/rasshirennyy-razum-i-vneshnyaya-pamyat")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.node.slug", is("rasshirennyy-razum-i-vneshnyaya-pamyat")))
                .andExpect(jsonPath("$.node.domain", is("CANON")))
                .andExpect(jsonPath("$.machineReadableUrls.llmsTxt", is("/llms.txt")));
    }

    @Test
    void testGetGraphNodeBySlugNotFound() throws Exception {
        when(searchService.getGraphNodeBySlug("non-existent-slug-123")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/graph/nodes/non-existent-slug-123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void testListGraphLinksEndpoint() throws Exception {
        GraphLink link = new GraphLink(1L, "src", "Source", "tgt", "Target", "DISCUSSES", "Desc");
        GraphLinkPage page = new GraphLinkPage(List.of(link), 0, 20, 1L, 1);

        when(searchService.listGraphLinks(eq(null), eq(null), eq(null), eq(0), eq(20))).thenReturn(page);

        mockMvc.perform(get("/api/v1/graph/links")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].sourceSlug", is("src")));
    }

    @Test
    void testLlmsTxtEndpoint() throws Exception {
        when(searchService.generateLlmsTxt()).thenReturn("# Dmitry Efremov Index");

        mockMvc.perform(get("/api/v1/llms.txt"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(containsString("Dmitry Efremov Index")));
    }

    @Test
    void testAiAgentJsonEndpoint() throws Exception {
        AiAgentGraphRepresentation representation = new AiAgentGraphRepresentation("1.0.0", OffsetDateTime.now(), 5, 2, List.of(), List.of());
        when(searchService.generateAiAgentGraph()).thenReturn(representation);

        mockMvc.perform(get("/api/v1/ai-agent.json"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.version", is("1.0.0")))
                .andExpect(jsonPath("$.nodeCount", is(5)));
    }
}
