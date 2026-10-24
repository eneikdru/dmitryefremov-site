package com.dmitryefremov.site.canon.service;

import com.dmitryefremov.site.canon.domain.*;
import com.dmitryefremov.site.canon.dto.CanonCrossReferenceDto;
import com.dmitryefremov.site.canon.dto.CanonItemDto;
import com.dmitryefremov.site.canon.dto.CanonPageDto;
import com.dmitryefremov.site.canon.dto.ConceptMediaLinkDto;
import com.dmitryefremov.site.canon.repository.CanonCrossReferenceRepository;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
import com.dmitryefremov.site.canon.repository.ConceptMediaLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("siteCanonService")
@Transactional(readOnly = true)
public class CanonService {

    private final CanonItemRepository canonItemRepository;
    private final CanonCrossReferenceRepository canonCrossReferenceRepository;
    private final ConceptMediaLinkRepository conceptMediaLinkRepository;
    private final Clock clock;

    public CanonService(@Qualifier("siteCanonItemRepository") CanonItemRepository canonItemRepository,
                        @Qualifier("siteCanonCrossReferenceRepository") CanonCrossReferenceRepository canonCrossReferenceRepository,
                        @Qualifier("siteConceptMediaLinkRepository") ConceptMediaLinkRepository conceptMediaLinkRepository,
                        @Autowired(required = false) Clock clock) {
        this.canonItemRepository = canonItemRepository;
        this.canonCrossReferenceRepository = canonCrossReferenceRepository;
        this.conceptMediaLinkRepository = conceptMediaLinkRepository;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    public CanonPageDto getCanonItems(CanonDomain domain, CanonType type, int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<CanonItem> canonPage;

        if (domain != null && type != null) {
            canonPage = canonItemRepository.findByDomainAndTypeOrderByPublishedAtDesc(domain, type, pageable);
        } else if (domain != null) {
            canonPage = canonItemRepository.findByDomainOrderByPublishedAtDesc(domain, pageable);
        } else if (type != null) {
            canonPage = canonItemRepository.findByTypeOrderByPublishedAtDesc(type, pageable);
        } else {
            canonPage = canonItemRepository.findAllByOrderByPublishedAtDesc(pageable);
        }

        List<CanonItemDto> dtos = canonPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new CanonPageDto(
                dtos,
                canonPage.getNumber(),
                canonPage.getSize(),
                canonPage.getTotalElements(),
                canonPage.getTotalPages()
        );
    }

    public Optional<CanonItemDto> getCanonItemBySlug(String slug) {
        return canonItemRepository.findBySlug(slug)
                .map(this::mapToDtoWithReferences);
    }

    public List<ConceptMediaLinkDto> getMediaLinksForCanonItem(String slug) {
        return conceptMediaLinkRepository.findByCanonItemSlug(slug).stream()
                .map(this::mapToMediaLinkDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CanonItemDto createCanonItem(CanonItemDto dto) {
        if (dto.getSlug() == null || dto.getSlug().isBlank()) {
            throw new IllegalArgumentException("Slug must not be empty");
        }
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title must not be empty");
        }
        if (dto.getType() == null) {
            throw new IllegalArgumentException("Canon type must not be null");
        }
        if (dto.getDomain() == null) {
            throw new IllegalArgumentException("Canon domain must not be null");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new IllegalArgumentException("Content must not be empty");
        }

        if (canonItemRepository.findBySlug(dto.getSlug()).isPresent()) {
            throw new IllegalArgumentException("Canon item with slug '" + dto.getSlug() + "' already exists");
        }

        OffsetDateTime now = OffsetDateTime.now(clock);
        OffsetDateTime publishedAt = dto.getPublishedAt() != null ? dto.getPublishedAt() : now;

        CanonItem item = new CanonItem(
                dto.getSlug(),
                dto.getTitle(),
                dto.getType(),
                dto.getDomain(),
                dto.getSummary(),
                dto.getContent(),
                publishedAt,
                clock
        );

        CanonItem saved = canonItemRepository.save(item);
        return mapToDto(saved);
    }

    @Transactional
    public CanonCrossReferenceDto addCrossReference(String sourceSlug, String targetSlug, CanonRelationshipType relationshipType, String description) {
        if (sourceSlug != null && sourceSlug.equalsIgnoreCase(targetSlug)) {
            throw new IllegalArgumentException("Canon item cannot reference itself");
        }

        CanonItem source = canonItemRepository.findBySlug(sourceSlug)
                .orElseThrow(() -> new IllegalArgumentException("Source canon item with slug '" + sourceSlug + "' not found"));
        CanonItem target = canonItemRepository.findBySlug(targetSlug)
                .orElseThrow(() -> new IllegalArgumentException("Target canon item with slug '" + targetSlug + "' not found"));

        if (source.getId() != null && target.getId() != null && Objects.equals(source.getId(), target.getId())) {
            throw new IllegalArgumentException("Canon item cannot reference itself");
        }

        CanonRelationshipType relType = relationshipType != null ? relationshipType : CanonRelationshipType.REFERENCES;

        Optional<CanonCrossReference> existing = Optional.empty();
        if (source.getId() != null && target.getId() != null) {
            existing = canonCrossReferenceRepository
                    .findBySourceIdAndTargetIdAndRelationshipType(source.getId(), target.getId(), relType);
        }

        if (existing.isPresent()) {
            return mapToRefDto(existing.get());
        }

        CanonCrossReference xref = new CanonCrossReference(source, target, relType, description, clock);
        source.addOutgoingReference(xref);

        CanonCrossReference saved = canonCrossReferenceRepository.save(xref);
        return mapToRefDto(saved);
    }

    public CanonItemDto mapToDto(CanonItem entity) {
        List<ConceptMediaLinkDto> links = getMediaLinksForCanonItem(entity.getSlug());
        return new CanonItemDto(
                entity.getId(),
                entity.getSlug(),
                entity.getTitle(),
                entity.getType(),
                entity.getDomain(),
                entity.getSummary(),
                entity.getContent(),
                entity.getPublishedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                List.of(),
                List.of(),
                links
        );
    }

    public CanonItemDto mapToDtoWithReferences(CanonItem entity) {
        List<CanonCrossReferenceDto> outgoing = entity.getOutgoingReferences().stream()
                .map(this::mapToRefDto)
                .collect(Collectors.toList());

        List<CanonCrossReferenceDto> incoming = entity.getIncomingReferences().stream()
                .map(this::mapToRefDto)
                .collect(Collectors.toList());

        List<ConceptMediaLinkDto> links = getMediaLinksForCanonItem(entity.getSlug());

        return new CanonItemDto(
                entity.getId(),
                entity.getSlug(),
                entity.getTitle(),
                entity.getType(),
                entity.getDomain(),
                entity.getSummary(),
                entity.getContent(),
                entity.getPublishedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                outgoing,
                incoming,
                links
        );
    }

    public CanonCrossReferenceDto mapToRefDto(CanonCrossReference ref) {
        return new CanonCrossReferenceDto(
                ref.getId(),
                ref.getSource() != null ? ref.getSource().getSlug() : null,
                ref.getSource() != null ? ref.getSource().getTitle() : null,
                ref.getTarget() != null ? ref.getTarget().getSlug() : null,
                ref.getTarget() != null ? ref.getTarget().getTitle() : null,
                ref.getRelationshipType(),
                ref.getDescription(),
                ref.getCreatedAt()
        );
    }

    public ConceptMediaLinkDto mapToMediaLinkDto(ConceptMediaLink link) {
        return new ConceptMediaLinkDto(
                link.getId(),
                link.getCanonItem() != null ? link.getCanonItem().getId() : null,
                link.getCanonItem() != null ? link.getCanonItem().getSlug() : null,
                link.getCanonItem() != null ? link.getCanonItem().getTitle() : null,
                link.getMediaType(),
                link.getPodcastEpisode() != null ? link.getPodcastEpisode().getId() : null,
                link.getPodcastEpisode() != null ? link.getPodcastEpisode().getSlug() : null,
                link.getPodcastEpisode() != null ? link.getPodcastEpisode().getTitle() : null,
                link.getEssay() != null ? link.getEssay().getId() : null,
                link.getEssay() != null ? link.getEssay().getSlug() : null,
                link.getEssay() != null ? link.getEssay().getTitle() : null,
                link.getSpark() != null ? link.getSpark().getId() : null,
                link.getSpark() != null ? link.getSpark().getContent() : null,
                link.getArticle() != null ? link.getArticle().getId() : null,
                link.getArticle() != null ? link.getArticle().getSlug() : null,
                link.getArticle() != null ? link.getArticle().getTitle() : null,
                link.getRelationshipType(),
                link.getDescription(),
                link.getCreatedAt()
        );
    }
}
