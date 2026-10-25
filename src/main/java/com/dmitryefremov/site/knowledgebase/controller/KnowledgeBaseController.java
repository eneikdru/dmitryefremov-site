package com.dmitryefremov.site.knowledgebase.controller;

import com.dmitryefremov.site.knowledgebase.dto.*;
import com.dmitryefremov.site.knowledgebase.service.KnowledgeBaseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/knowledge-base")
public class KnowledgeBaseController {

    private final KnowledgeBaseService service;

    public KnowledgeBaseController(KnowledgeBaseService service) {
        this.service = service;
    }

    @GetMapping("/concepts")
    public ResponseEntity<ConceptPage> getConcepts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        ConceptPage conceptPage = service.getConcepts(category, tag, query, PageRequest.of(page, size));
        return ResponseEntity.ok(conceptPage);
    }

    @PostMapping("/concepts")
    public ResponseEntity<ConceptItem> createConcept(@RequestBody ConceptCreateRequest request) {
        if (request.getSlug() == null || request.getSlug().length() < 2 || request.getTitle() == null || request.getCategory() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid concept payload");
        }
        ConceptItem created = service.createConcept(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/concepts/{slug}")
    public ResponseEntity<ConceptDetail> getConceptBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(service.getConceptBySlug(slug));
    }

    @GetMapping("/categories")
    public ResponseEntity<CategoryList> getCategories() {
        return ResponseEntity.ok(service.getCategories());
    }

    @GetMapping("/tags")
    public ResponseEntity<TagList> getTags() {
        return ResponseEntity.ok(service.getTags());
    }

    @GetMapping("/concepts/{slug}/models")
    public ResponseEntity<List<AppliedModelConfigurationDto>> getAppliedModels(@PathVariable String slug) {
        return ResponseEntity.ok(service.getAppliedModels(slug));
    }

    @PostMapping("/concepts/{slug}/models")
    public ResponseEntity<AppliedModelConfigurationDto> createAppliedModel(
            @PathVariable String slug,
            @RequestBody AppliedModelCreateRequest request) {

        if (request.getModelName() == null || request.getConfigurationJson() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid model configuration payload");
        }

        AppliedModelConfigurationDto created = service.createAppliedModel(slug, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        ErrorResponse error = new ErrorResponse(ex.getStatusCode().value(), ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }
}
