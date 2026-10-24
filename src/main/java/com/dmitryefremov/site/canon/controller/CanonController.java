package com.dmitryefremov.site.canon.controller;

import com.dmitryefremov.site.canon.domain.CanonDomain;
import com.dmitryefremov.site.canon.domain.CanonRelationshipType;
import com.dmitryefremov.site.canon.domain.CanonType;
import com.dmitryefremov.site.canon.dto.*;
import com.dmitryefremov.site.canon.service.CanonService;
import com.dmitryefremov.site.canon.service.CorpusSearchService;
import com.dmitryefremov.site.podcast.dto.ErrorResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@RestController("siteCanonController")
@RequestMapping("/api/v1/canon")
public class CanonController {

    private final CanonService canonService;
    private final CorpusSearchService corpusSearchService;
    private final Clock clock;

    public CanonController(@Qualifier("siteCanonService") CanonService canonService,
                           @Qualifier("siteCorpusSearchService") CorpusSearchService corpusSearchService,
                           @Autowired(required = false) Clock clock) {
        this.canonService = canonService;
        this.corpusSearchService = corpusSearchService;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    @GetMapping
    public ResponseEntity<CanonPageDto> listCanonItems(
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        CanonDomain canonDomain = domain != null && !domain.isBlank() ? CanonDomain.fromString(domain) : null;
        CanonType canonType = type != null && !type.isBlank() ? CanonType.fromString(type) : null;

        CanonPageDto result = canonService.getCanonItems(canonDomain, canonType, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<CorpusSearchDto> searchCorpus(@RequestParam String q) {
        CorpusSearchDto searchResult = corpusSearchService.searchCorpus(q);
        return ResponseEntity.ok(searchResult);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getCanonItemBySlug(@PathVariable String slug) {
        return canonService.getCanonItemBySlug(slug)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    ErrorResponseDto error = new ErrorResponseDto(
                            HttpStatus.NOT_FOUND.value(),
                            "Canon item with slug '" + slug + "' was not found",
                            OffsetDateTime.now(clock)
                    );
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
    }

    @GetMapping("/{slug}/links")
    public ResponseEntity<List<ConceptMediaLinkDto>> getCanonItemMediaLinks(@PathVariable String slug) {
        List<ConceptMediaLinkDto> links = canonService.getMediaLinksForCanonItem(slug);
        return ResponseEntity.ok(links);
    }

    @PostMapping
    public ResponseEntity<CanonItemDto> createCanonItem(@RequestBody CanonItemDto dto) {
        CanonItemDto created = canonService.createCanonItem(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{sourceSlug}/references/{targetSlug}")
    public ResponseEntity<CanonCrossReferenceDto> addCrossReference(
            @PathVariable String sourceSlug,
            @PathVariable String targetSlug,
            @RequestParam(required = false) String relationshipType,
            @RequestParam(required = false) String description) {

        CanonRelationshipType relType = relationshipType != null && !relationshipType.isBlank()
                ? CanonRelationshipType.fromString(relationshipType)
                : CanonRelationshipType.REFERENCES;

        CanonCrossReferenceDto xref = canonService.addCrossReference(sourceSlug, targetSlug, relType, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(xref);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                OffsetDateTime.now(clock)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
