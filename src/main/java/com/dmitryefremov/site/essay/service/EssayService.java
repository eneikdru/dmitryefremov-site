package com.dmitryefremov.site.essay.service;

import com.dmitryefremov.site.essay.domain.PublishedEssay;
import com.dmitryefremov.site.essay.dto.EssayDto;
import com.dmitryefremov.site.essay.dto.EssayPageDto;
import com.dmitryefremov.site.essay.repository.PublishedEssayRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("siteEssayService")
@Transactional(readOnly = true)
public class EssayService {

    private final PublishedEssayRepository publishedEssayRepository;

    public EssayService(PublishedEssayRepository publishedEssayRepository) {
        this.publishedEssayRepository = publishedEssayRepository;
    }

    public EssayPageDto getEssays(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<PublishedEssay> essayPage = publishedEssayRepository.findAllByOrderByPublishedAtDesc(pageable);

        List<EssayDto> dtos = essayPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new EssayPageDto(
                dtos,
                essayPage.getNumber(),
                essayPage.getSize(),
                essayPage.getTotalElements(),
                essayPage.getTotalPages()
        );
    }

    public Optional<EssayDto> getEssayBySlug(String slug) {
        return publishedEssayRepository.findBySlug(slug)
                .map(this::mapToDto);
    }

    @Transactional
    public EssayDto createEssay(EssayDto dto) {
        if (dto.getSlug() == null || dto.getSlug().isBlank()) {
            throw new IllegalArgumentException("Essay slug is required");
        }
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Essay title is required");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new IllegalArgumentException("Essay content is required");
        }
        if (publishedEssayRepository.findBySlug(dto.getSlug()).isPresent()) {
            throw new IllegalArgumentException("Essay with slug '" + dto.getSlug() + "' already exists");
        }

        PublishedEssay entity = new PublishedEssay(
                dto.getSlug(),
                dto.getTitle(),
                dto.getSummary(),
                dto.getContent(),
                "Dmitry Efremov",
                dto.getPublishedAt() != null ? dto.getPublishedAt() : java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC)
        );

        PublishedEssay saved = publishedEssayRepository.save(entity);
        return mapToDto(saved);
    }

    public EssayDto mapToDto(PublishedEssay entity) {
        return new EssayDto(
                entity.getId(),
                entity.getSlug(),
                entity.getTitle(),
                entity.getSummary(),
                entity.getContent(),
                entity.getPublishedAt(),
                entity.getCreatedAt()
        );
    }
}
