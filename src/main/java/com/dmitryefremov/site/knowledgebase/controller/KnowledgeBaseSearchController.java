package com.dmitryefremov.site.knowledgebase.controller;

import com.dmitryefremov.site.knowledgebase.dto.*;
import com.dmitryefremov.site.knowledgebase.service.KnowledgeBaseSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class KnowledgeBaseSearchController {

    private final KnowledgeBaseSearchService searchService;

    public KnowledgeBaseSearchController(KnowledgeBaseSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/api/v1/search")
    public ResponseEntity<?> searchContent(
            @RequestParam("q") String query,
            @RequestParam(value = "domains", required = false) List<SearchDomain> domains,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset) {

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, "Search query 'q' must not be empty"));
        }

        if (limit < 1 || limit > 100 || offset < 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, "Invalid pagination parameters: limit must be between 1 and 100, offset must be >= 0"));
        }

        SearchResponse response = searchService.search(query, domains, limit, offset);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/graph/nodes")
    public ResponseEntity<?> listGraphNodes(
            @RequestParam(value = "domain", required = false) SearchDomain domain,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        if (page < 0 || size < 1 || size > 100) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, "Invalid pagination parameters: page must be >= 0, size must be between 1 and 100"));
        }

        GraphNodePage response = searchService.listGraphNodes(domain, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/graph/nodes/{slug}")
    public ResponseEntity<?> getGraphNodeBySlug(@PathVariable("slug") String slug) {
        return searchService.getGraphNodeBySlug(slug)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(404, "Graph node with slug '" + slug + "' not found")));
    }

    @GetMapping("/api/v1/graph/links")
    public ResponseEntity<?> listGraphLinks(
            @RequestParam(value = "sourceSlug", required = false) String sourceSlug,
            @RequestParam(value = "targetSlug", required = false) String targetSlug,
            @RequestParam(value = "relationshipType", required = false) String relationshipType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        if (page < 0 || size < 1 || size > 100) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, "Invalid pagination parameters: page must be >= 0, size must be between 1 and 100"));
        }

        GraphLinkPage response = searchService.listGraphLinks(sourceSlug, targetSlug, relationshipType, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/v1/llms.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getLlmsTxtIndex() {
        return ResponseEntity.ok(searchService.generateLlmsTxt());
    }

    @GetMapping(value = "/api/v1/ai-agent.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AiAgentGraphRepresentation> getAiAgentGraphJson() {
        return ResponseEntity.ok(searchService.generateAiAgentGraph());
    }
}
