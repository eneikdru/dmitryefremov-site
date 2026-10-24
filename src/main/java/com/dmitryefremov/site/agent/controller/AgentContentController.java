package com.dmitryefremov.site.agent.controller;

import com.dmitryefremov.site.agent.domain.AgentApiToken;
import com.dmitryefremov.site.agent.dto.ArticleUpdateRequestDto;
import com.dmitryefremov.site.agent.dto.ConceptUpdateRequestDto;
import com.dmitryefremov.site.agent.dto.EssayUpdateRequestDto;
import com.dmitryefremov.site.agent.dto.PodcastUpdateRequestDto;
import com.dmitryefremov.site.agent.service.AgentContentService;
import com.dmitryefremov.site.article.dto.ArticleDto;
import com.dmitryefremov.site.canon.dto.CanonItemDto;
import com.dmitryefremov.site.essay.dto.EssayDto;
import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import com.dmitryefremov.site.podcast.dto.PodcastEpisodeDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/agent/content")
public class AgentContentController {

    private final AgentContentService agentContentService;

    public AgentContentController(AgentContentService agentContentService) {
        this.agentContentService = agentContentService;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/rss+xml"})
    public ResponseEntity<?> getContent(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-API-Token", required = false) String apiTokenHeader,
            @RequestHeader(value = "Accept", required = false) String acceptHeader) {

        String token = apiTokenHeader != null ? apiTokenHeader : authHeader;
        Optional<AgentApiToken> agentOpt = agentContentService.authenticateToken(token);
        if (agentOpt.isEmpty()) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid or missing API token");
        }

        if (acceptHeader != null && (acceptHeader.contains("application/xml") || acceptHeader.contains("application/rss+xml") || acceptHeader.contains("text/xml"))) {
            String rssXml = agentContentService.getAllContentAsRss();
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(rssXml);
        } else {
            Map<String, Object> contentMap = agentContentService.getAllContentAsJson();
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(contentMap);
        }
    }

    @PutMapping(value = "/articles/{slug}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateArticleJson(
            @PathVariable String slug,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-API-Token", required = false) String apiTokenHeader,
            @RequestBody ArticleUpdateRequestDto dto) {

        String token = apiTokenHeader != null ? apiTokenHeader : authHeader;
        Optional<AgentApiToken> agentOpt = agentContentService.authenticateToken(token);
        if (agentOpt.isEmpty()) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid or missing API token");
        }

        try {
            ArticleDto updated = agentContentService.updateArticleJson(slug, dto, agentOpt.get());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping(value = "/articles/{slug}", consumes = {"text/markdown", "text/plain"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateArticleMarkdown(
            @PathVariable String slug,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-API-Token", required = false) String apiTokenHeader,
            @RequestBody String markdown) {

        String token = apiTokenHeader != null ? apiTokenHeader : authHeader;
        Optional<AgentApiToken> agentOpt = agentContentService.authenticateToken(token);
        if (agentOpt.isEmpty()) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid or missing API token");
        }

        try {
            ArticleDto updated = agentContentService.updateArticleMarkdown(slug, markdown, agentOpt.get());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping(value = "/podcasts/{slug}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePodcastJson(
            @PathVariable String slug,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-API-Token", required = false) String apiTokenHeader,
            @RequestBody PodcastUpdateRequestDto dto) {

        String token = apiTokenHeader != null ? apiTokenHeader : authHeader;
        Optional<AgentApiToken> agentOpt = agentContentService.authenticateToken(token);
        if (agentOpt.isEmpty()) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid or missing API token");
        }

        try {
            PodcastEpisodeDto updated = agentContentService.updatePodcastJson(slug, dto, agentOpt.get());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping(value = "/podcasts/{slug}", consumes = {"text/markdown", "text/plain"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePodcastMarkdown(
            @PathVariable String slug,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-API-Token", required = false) String apiTokenHeader,
            @RequestBody String markdown) {

        String token = apiTokenHeader != null ? apiTokenHeader : authHeader;
        Optional<AgentApiToken> agentOpt = agentContentService.authenticateToken(token);
        if (agentOpt.isEmpty()) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid or missing API token");
        }

        try {
            PodcastEpisodeDto updated = agentContentService.updatePodcastMarkdown(slug, markdown, agentOpt.get());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping(value = "/essays/{slug}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateEssayJson(
            @PathVariable String slug,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-API-Token", required = false) String apiTokenHeader,
            @RequestBody EssayUpdateRequestDto dto) {

        String token = apiTokenHeader != null ? apiTokenHeader : authHeader;
        Optional<AgentApiToken> agentOpt = agentContentService.authenticateToken(token);
        if (agentOpt.isEmpty()) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid or missing API token");
        }

        try {
            EssayDto updated = agentContentService.updateEssayJson(slug, dto, agentOpt.get());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping(value = "/essays/{slug}", consumes = {"text/markdown", "text/plain"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateEssayMarkdown(
            @PathVariable String slug,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-API-Token", required = false) String apiTokenHeader,
            @RequestBody String markdown) {

        String token = apiTokenHeader != null ? apiTokenHeader : authHeader;
        Optional<AgentApiToken> agentOpt = agentContentService.authenticateToken(token);
        if (agentOpt.isEmpty()) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid or missing API token");
        }

        try {
            EssayDto updated = agentContentService.updateEssayMarkdown(slug, markdown, agentOpt.get());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping(value = {"/concepts/{slug}", "/canon/{slug}"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateConceptJson(
            @PathVariable String slug,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-API-Token", required = false) String apiTokenHeader,
            @RequestBody ConceptUpdateRequestDto dto) {

        String token = apiTokenHeader != null ? apiTokenHeader : authHeader;
        Optional<AgentApiToken> agentOpt = agentContentService.authenticateToken(token);
        if (agentOpt.isEmpty()) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid or missing API token");
        }

        try {
            CanonItemDto updated = agentContentService.updateConceptJson(slug, dto, agentOpt.get());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping(value = {"/concepts/{slug}", "/canon/{slug}"}, consumes = {"text/markdown", "text/plain"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateConceptMarkdown(
            @PathVariable String slug,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-API-Token", required = false) String apiTokenHeader,
            @RequestBody String markdown) {

        String token = apiTokenHeader != null ? apiTokenHeader : authHeader;
        Optional<AgentApiToken> agentOpt = agentContentService.authenticateToken(token);
        if (agentOpt.isEmpty()) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid or missing API token");
        }

        try {
            CanonItemDto updated = agentContentService.updateConceptMarkdown(slug, markdown, agentOpt.get());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponseDto error = new ErrorResponseDto(status.value(), message, OffsetDateTime.now());
        return ResponseEntity.status(status).body(error);
    }
}
