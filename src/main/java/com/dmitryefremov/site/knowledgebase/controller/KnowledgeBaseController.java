package com.dmitryefremov.site.knowledgebase.controller;

import com.dmitryefremov.site.knowledgebase.dto.AppliedModelConfigurationDto;
import com.dmitryefremov.site.knowledgebase.dto.AppliedModelCreateRequestDto;
import com.dmitryefremov.site.knowledgebase.dto.CategoryListDto;
import com.dmitryefremov.site.knowledgebase.dto.ConceptCreateRequestDto;
import com.dmitryefremov.site.knowledgebase.dto.ConceptDetailDto;
import com.dmitryefremov.site.knowledgebase.dto.ConceptItemDto;
import com.dmitryefremov.site.knowledgebase.dto.ConceptPageDto;
import com.dmitryefremov.site.knowledgebase.dto.ErrorResponseDto;
import com.dmitryefremov.site.knowledgebase.dto.TagListDto;
import com.dmitryefremov.site.knowledgebase.exception.KnowledgeBaseException;
import com.dmitryefremov.site.knowledgebase.exception.ResourceNotFoundException;
import com.dmitryefremov.site.knowledgebase.service.KnowledgeBaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/knowledge-base")
public class KnowledgeBaseController {

    private final KnowledgeBaseService service;

    public KnowledgeBaseController(KnowledgeBaseService service) {
        this.service = service;
    }

    @GetMapping("/concepts")
    public ResponseEntity<ConceptPageDto> getConcepts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        ConceptPageDto result = service.getConcepts(category, tag, q, page, size);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/concepts")
    public ResponseEntity<ConceptItemDto> createConcept(@Valid @RequestBody ConceptCreateRequestDto request) {
        ConceptItemDto created = service.createConcept(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/concepts/{slug}")
    public ResponseEntity<ConceptDetailDto> getConceptBySlug(@PathVariable String slug) {
        ConceptDetailDto detail = service.getConceptBySlug(slug);
        return ResponseEntity.ok(detail);
    }

    @GetMapping("/categories")
    public ResponseEntity<CategoryListDto> getConceptCategories() {
        CategoryListDto categories = service.getConceptCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/tags")
    public ResponseEntity<TagListDto> getConceptTags() {
        TagListDto tags = service.getConceptTags();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/concepts/{slug}/models")
    public ResponseEntity<List<AppliedModelConfigurationDto>> getAppliedModelsForConcept(@PathVariable String slug) {
        List<AppliedModelConfigurationDto> models = service.getAppliedModelsForConcept(slug);
        return ResponseEntity.ok(models);
    }

    @PostMapping("/concepts/{slug}/models")
    public ResponseEntity<AppliedModelConfigurationDto> createAppliedModelForConcept(
            @PathVariable String slug,
            @Valid @RequestBody AppliedModelCreateRequestDto request) {
        AppliedModelConfigurationDto created = service.createAppliedModelForConcept(slug, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleResourceNotFoundException(ResourceNotFoundException ex) {
        org.slf4j.LoggerFactory.getLogger(KnowledgeBaseController.class).error("Resource not found: {}", ex.getMessage(), ex);
        return new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), ex.getMessage(), OffsetDateTime.now());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleIllegalArgumentException(IllegalArgumentException ex) {
        org.slf4j.LoggerFactory.getLogger(KnowledgeBaseController.class).error("Validation error: {}", ex.getMessage(), ex);
        return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), OffsetDateTime.now());
    }

    @ExceptionHandler(KnowledgeBaseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleKnowledgeBaseException(KnowledgeBaseException ex) {
        org.slf4j.LoggerFactory.getLogger(KnowledgeBaseController.class).error("Internal knowledge base error: {}", ex.getMessage(), ex);
        return new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), OffsetDateTime.now());
    }
}
