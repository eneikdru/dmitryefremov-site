package com.dmitryefremov.site.knowledgebase.service;

import com.dmitryefremov.site.knowledgebase.domain.AppliedModelConfiguration;
import com.dmitryefremov.site.knowledgebase.domain.KnowledgeBaseConcept;
import com.dmitryefremov.site.knowledgebase.domain.KnowledgeBaseTag;
import com.dmitryefremov.site.knowledgebase.dto.*;
import com.dmitryefremov.site.knowledgebase.repository.AppliedModelConfigurationRepository;
import com.dmitryefremov.site.knowledgebase.repository.KnowledgeBaseConceptRepository;
import com.dmitryefremov.site.knowledgebase.repository.KnowledgeBaseTagRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class KnowledgeBaseService {

    private final KnowledgeBaseConceptRepository conceptRepository;
    private final KnowledgeBaseTagRepository tagRepository;
    private final AppliedModelConfigurationRepository appliedModelRepository;

    public KnowledgeBaseService(KnowledgeBaseConceptRepository conceptRepository,
                                KnowledgeBaseTagRepository tagRepository,
                                AppliedModelConfigurationRepository appliedModelRepository) {
        this.conceptRepository = conceptRepository;
        this.tagRepository = tagRepository;
        this.appliedModelRepository = appliedModelRepository;
    }

    @Transactional(readOnly = true)
    public ConceptPage getConcepts(String category, String tag, String q, Pageable pageable) {
        Specification<KnowledgeBaseConcept> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            if (q != null && !q.isBlank()) {
                String likePattern = "%" + q.toLowerCase() + "%";
                Predicate titleLike = cb.like(cb.lower(root.get("title")), likePattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), likePattern);
                Predicate contentLike = cb.like(cb.lower(root.get("content")), likePattern);
                predicates.add(cb.or(titleLike, descLike, contentLike));
            }

            if (tag != null && !tag.isBlank()) {
                Join<KnowledgeBaseConcept, KnowledgeBaseTag> tagsJoin = root.join("tags");
                predicates.add(cb.equal(tagsJoin.get("tagName"), tag));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<KnowledgeBaseConcept> page = conceptRepository.findAll(spec, pageable);

        ConceptPage conceptPage = new ConceptPage();
        conceptPage.setPage(page.getNumber());
        conceptPage.setSize(page.getSize());
        conceptPage.setTotalElements(page.getTotalElements());
        conceptPage.setTotalPages(page.getTotalPages());
        conceptPage.setContent(page.getContent().stream().map(this::mapToConceptItem).collect(Collectors.toList()));

        return conceptPage;
    }

    @Transactional
    public ConceptItem createConcept(ConceptCreateRequest request) {
        if (conceptRepository.existsBySlug(request.getSlug())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Concept with this slug already exists");
        }

        KnowledgeBaseConcept concept = new KnowledgeBaseConcept(
                request.getSlug(),
                request.getTitle(),
                request.getCategory(),
                request.getDescription(),
                request.getContent()
        );
        concept = conceptRepository.save(concept);

        if (request.getTags() != null && !request.getTags().isEmpty()) {
            for (String tagName : request.getTags()) {
                concept.addTag(new KnowledgeBaseTag(concept, tagName));
            }
            conceptRepository.save(concept);
        }

        return mapToConceptItem(concept);
    }

    @Transactional(readOnly = true)
    public ConceptDetail getConceptBySlug(String slug) {
        KnowledgeBaseConcept concept = conceptRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Concept not found"));

        List<KnowledgeBaseTag> tags = tagRepository.findByConceptId(concept.getId());
        List<AppliedModelConfiguration> models = appliedModelRepository.findByConceptId(concept.getId());

        ConceptDetail detail = new ConceptDetail();
        detail.setId(concept.getId());
        detail.setSlug(concept.getSlug());
        detail.setTitle(concept.getTitle());
        detail.setCategory(concept.getCategory());
        detail.setDescription(concept.getDescription());
        detail.setContent(concept.getContent());
        detail.setCreatedAt(concept.getCreatedAt());
        detail.setUpdatedAt(concept.getUpdatedAt());
        detail.setTags(tags.stream().map(KnowledgeBaseTag::getTagName).collect(Collectors.toList()));
        detail.setAppliedModels(models.stream().map(this::mapToAppliedModelDto).collect(Collectors.toList()));

        return detail;
    }

    @Transactional(readOnly = true)
    public CategoryList getCategories() {
        return new CategoryList(conceptRepository.findDistinctCategories());
    }

    @Transactional(readOnly = true)
    public TagList getTags() {
        return new TagList(tagRepository.findDistinctTagNames());
    }

    @Transactional(readOnly = true)
    public List<AppliedModelConfigurationDto> getAppliedModels(String slug) {
        KnowledgeBaseConcept concept = conceptRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Concept not found"));

        return appliedModelRepository.findByConceptId(concept.getId())
                .stream()
                .map(this::mapToAppliedModelDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppliedModelConfigurationDto createAppliedModel(String slug, AppliedModelCreateRequest request) {
        KnowledgeBaseConcept concept = conceptRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Concept not found"));

        AppliedModelConfiguration config = new AppliedModelConfiguration(
                concept,
                request.getModelName(),
                request.getVersion(),
                request.getConfigurationJson(),
                request.getIsActive()
        );

        config = appliedModelRepository.save(config);
        return mapToAppliedModelDto(config);
    }

    private ConceptItem mapToConceptItem(KnowledgeBaseConcept concept) {
        ConceptItem item = new ConceptItem();
        item.setId(concept.getId());
        item.setSlug(concept.getSlug());
        item.setTitle(concept.getTitle());
        item.setCategory(concept.getCategory());
        item.setDescription(concept.getDescription());
        item.setContent(concept.getContent());
        item.setCreatedAt(concept.getCreatedAt());
        item.setUpdatedAt(concept.getUpdatedAt());

        List<String> tags = concept.getTags()
                .stream()
                .map(KnowledgeBaseTag::getTagName)
                .collect(Collectors.toList());
        item.setTags(tags);

        return item;
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
