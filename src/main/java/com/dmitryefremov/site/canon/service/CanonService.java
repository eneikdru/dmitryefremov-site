package com.dmitryefremov.site.canon.service;

import com.dmitryefremov.site.canon.domain.*;
import com.dmitryefremov.site.canon.dto.CanonCrossReferenceDto;
import com.dmitryefremov.site.canon.dto.CanonItemDto;
import com.dmitryefremov.site.canon.dto.CanonPageDto;
import com.dmitryefremov.site.canon.repository.CanonCrossReferenceRepository;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
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
    private final Clock clock;

    public CanonService(@Qualifier("siteCanonItemRepository") CanonItemRepository canonItemRepository,
                        @Qualifier("siteCanonCrossReferenceRepository") CanonCrossReferenceRepository canonCrossReferenceRepository,
                        @Autowired(required = false) Clock clock) {
        this.canonItemRepository = canonItemRepository;
        this.canonCrossReferenceRepository = canonCrossReferenceRepository;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    public CanonPageDto getCanonItems(CanonDomain domain, CanonType type, int page, int size) {
        return getCanonItems(domain, type, null, page, size);
    }

    public CanonPageDto getCanonItems(CanonDomain domain, CanonType type, String query, int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<CanonItem> canonPage;

        if (query != null && !query.isBlank()) {
            canonPage = canonItemRepository.searchByQuery(query.trim(), pageable);
        } else if (domain != null && type != null) {
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
                List.of()
        );
    }

    public CanonItemDto mapToDtoWithReferences(CanonItem entity) {
        List<CanonCrossReferenceDto> outgoing = entity.getOutgoingReferences().stream()
                .map(this::mapToRefDto)
                .collect(Collectors.toList());

        List<CanonCrossReferenceDto> incoming = entity.getIncomingReferences().stream()
                .map(this::mapToRefDto)
                .collect(Collectors.toList());

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
                incoming
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
}
