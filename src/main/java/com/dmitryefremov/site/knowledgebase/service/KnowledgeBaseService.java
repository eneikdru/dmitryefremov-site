package com.dmitryefremov.site.knowledgebase.service;

import com.dmitryefremov.site.knowledgebase.domain.AppliedModelConfiguration;
import com.dmitryefremov.site.knowledgebase.domain.Concept;
import com.dmitryefremov.site.knowledgebase.domain.ConceptTag;
import com.dmitryefremov.site.knowledgebase.dto.AppliedModelConfigurationDto;
import com.dmitryefremov.site.knowledgebase.dto.AppliedModelCreateRequestDto;
import com.dmitryefremov.site.knowledgebase.dto.CategoryListDto;
import com.dmitryefremov.site.knowledgebase.dto.ConceptCreateRequestDto;
import com.dmitryefremov.site.knowledgebase.dto.ConceptDetailDto;
import com.dmitryefremov.site.knowledgebase.dto.ConceptItemDto;
import com.dmitryefremov.site.knowledgebase.dto.ConceptPageDto;
import com.dmitryefremov.site.knowledgebase.dto.TagListDto;
import com.dmitryefremov.site.knowledgebase.exception.KnowledgeBaseException;
import com.dmitryefremov.site.knowledgebase.exception.ResourceNotFoundException;
import com.dmitryefremov.site.knowledgebase.repository.AppliedModelConfigurationRepository;
import com.dmitryefremov.site.knowledgebase.repository.ConceptRepository;
import com.dmitryefremov.site.knowledgebase.repository.ConceptTagRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnowledgeBaseService {

    private final ConceptRepository conceptRepository;
    private final ConceptTagRepository conceptTagRepository;
    private final AppliedModelConfigurationRepository appliedModelConfigurationRepository;

    public KnowledgeBaseService(ConceptRepository conceptRepository,
                                ConceptTagRepository conceptTagRepository,
                                AppliedModelConfigurationRepository appliedModelConfigurationRepository) {
        this.conceptRepository = conceptRepository;
        this.conceptTagRepository = conceptTagRepository;
        this.appliedModelConfigurationRepository = appliedModelConfigurationRepository;
    }

    @Transactional(readOnly = true)
    public ConceptPageDto getConcepts(String category, String tag, String q, int page, int size) {
        Specification<Concept> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (tag != null && !tag.isBlank()) {
                Join<Concept, ConceptTag> tagsJoin = root.join("tags", JoinType.INNER);
                predicates.add(cb.equal(tagsJoin.get("tagName"), tag));
            }
            if (q != null && !q.isBlank()) {
                String likePattern = "%" + q.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), likePattern),
                        cb.like(cb.lower(root.get("description")), likePattern),
                        cb.like(cb.lower(root.get("content")), likePattern)
                ));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<Concept> conceptPage = conceptRepository.findAll(spec, PageRequest.of(page, size));

        List<ConceptItemDto> content = conceptPage.getContent().stream()
                .map(this::mapToItemDto)
                .collect(Collectors.toList());

        ConceptPageDto dto = new ConceptPageDto();
        dto.setContent(content);
        dto.setPage(conceptPage.getNumber());
        dto.setSize(conceptPage.getSize());
        dto.setTotalElements(conceptPage.getTotalElements());
        dto.setTotalPages(conceptPage.getTotalPages());
        return dto;
    }

    @Transactional
    public ConceptItemDto createConcept(ConceptCreateRequestDto request) {
        if (conceptRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Concept with slug '" + request.getSlug() + "' already exists");
        }

        Concept concept = new Concept();
        concept.setSlug(request.getSlug());
        concept.setTitle(request.getTitle());
        concept.setCategory(request.getCategory());
        concept.setDescription(request.getDescription());
        concept.setContent(request.getContent());

        if (request.getTags() != null) {
            for (String tagName : request.getTags()) {
                ConceptTag conceptTag = new ConceptTag();
                conceptTag.setTagName(tagName);
                conceptTag.setConcept(concept);
                concept.getTags().add(conceptTag);
            }
        }

        concept = conceptRepository.save(concept);
        return mapToItemDto(concept);
    }

    @Transactional(readOnly = true)
    public ConceptDetailDto getConceptBySlug(String slug) {
        Concept concept = conceptRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Concept not found: " + slug));
        return mapToDetailDto(concept);
    }

    @Transactional(readOnly = true)
    public CategoryListDto getConceptCategories() {
        List<String> categories = conceptRepository.findDistinctCategories();
        return new CategoryListDto(categories);
    }

    @Transactional(readOnly = true)
    public TagListDto getConceptTags() {
        List<String> tags = conceptTagRepository.findDistinctTagNames();
        return new TagListDto(tags);
    }

    @Transactional(readOnly = true)
    public List<AppliedModelConfigurationDto> getAppliedModelsForConcept(String slug) {
        Concept concept = conceptRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Concept not found: " + slug));
        return concept.getAppliedModels().stream()
                .map(this::mapToAppliedModelDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppliedModelConfigurationDto createAppliedModelForConcept(String slug, AppliedModelCreateRequestDto request) {
        Concept concept = conceptRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Concept not found: " + slug));

        AppliedModelConfiguration config = new AppliedModelConfiguration();
        config.setConcept(concept);
        config.setModelName(request.getModelName());
        config.setVersion(request.getVersion());
        config.setConfigurationJson(request.getConfigurationJson());
        if (request.getIsActive() != null) {
            config.setIsActive(request.getIsActive());
        }

        config = appliedModelConfigurationRepository.save(config);
        concept.getAppliedModels().add(config);

        return mapToAppliedModelDto(config);
    }

    private ConceptItemDto mapToItemDto(Concept concept) {
        ConceptItemDto dto = new ConceptItemDto();
        dto.setId(concept.getId());
        dto.setSlug(concept.getSlug());
        dto.setTitle(concept.getTitle());
        dto.setCategory(concept.getCategory());
        dto.setDescription(concept.getDescription());
        dto.setContent(concept.getContent());
        dto.setTags(concept.getTags().stream().map(ConceptTag::getTagName).collect(Collectors.toList()));
        dto.setCreatedAt(concept.getCreatedAt());
        dto.setUpdatedAt(concept.getUpdatedAt());
        return dto;
    }

    private ConceptDetailDto mapToDetailDto(Concept concept) {
        ConceptDetailDto dto = new ConceptDetailDto();
        dto.setId(concept.getId());
        dto.setSlug(concept.getSlug());
        dto.setTitle(concept.getTitle());
        dto.setCategory(concept.getCategory());
        dto.setDescription(concept.getDescription());
        dto.setContent(concept.getContent());
        dto.setTags(concept.getTags().stream().map(ConceptTag::getTagName).collect(Collectors.toList()));
        dto.setAppliedModels(concept.getAppliedModels().stream().map(this::mapToAppliedModelDto).collect(Collectors.toList()));
        dto.setCreatedAt(concept.getCreatedAt());
        dto.setUpdatedAt(concept.getUpdatedAt());
        return dto;
    }

    private AppliedModelConfigurationDto mapToAppliedModelDto(AppliedModelConfiguration config) {
        AppliedModelConfigurationDto dto = new AppliedModelConfigurationDto();
        dto.setId(config.getId());
        if (config.getConcept() != null) {
            dto.setConceptId(config.getConcept().getId());
            dto.setConceptSlug(config.getConcept().getSlug());
        }
        dto.setModelName(config.getModelName());
        dto.setVersion(config.getVersion());
        dto.setConfigurationJson(config.getConfigurationJson());
        dto.setIsActive(config.getIsActive());
        dto.setCreatedAt(config.getCreatedAt());
        dto.setUpdatedAt(config.getUpdatedAt());
        return dto;
    }
}
