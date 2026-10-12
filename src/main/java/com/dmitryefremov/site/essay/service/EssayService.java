package com.dmitryefremov.site.essay.service;

import com.dmitryefremov.site.essay.domain.Essay;
import com.dmitryefremov.site.essay.dto.EssayDto;
import com.dmitryefremov.site.essay.dto.EssayPageDto;
import com.dmitryefremov.site.essay.repository.EssayRepository;
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

    private final EssayRepository essayRepository;

    public EssayService(EssayRepository essayRepository) {
        this.essayRepository = essayRepository;
    }

    public EssayPageDto getEssays(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Essay> essayPage = essayRepository.findAllByOrderByPublishedAtDesc(pageable);

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
        return essayRepository.findBySlug(slug)
                .map(this::mapToDto);
    }

    public EssayDto mapToDto(Essay entity) {
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
