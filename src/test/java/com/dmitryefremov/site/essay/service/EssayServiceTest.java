package com.dmitryefremov.site.essay.service;

import com.dmitryefremov.site.essay.domain.PublishedEssay;
import com.dmitryefremov.site.essay.dto.EssayDto;
import com.dmitryefremov.site.essay.dto.EssayPageDto;
import com.dmitryefremov.site.essay.repository.PublishedEssayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EssayServiceTest {

    @Mock
    private PublishedEssayRepository publishedEssayRepository;

    private EssayService essayService;

    @BeforeEach
    void setUp() {
        essayService = new EssayService(publishedEssayRepository);
    }

    @Test
    void testGetEssaysPaginationSuccess() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 12, 10, 0, 0, 0, ZoneOffset.UTC);
        PublishedEssay essay = new PublishedEssay("test-essay", "Test Essay Title", "Summary text", "Full essay content", "Curator", publishedAt);

        PageImpl<PublishedEssay> page = new PageImpl<>(List.of(essay), PageRequest.of(0, 20), 1);
        when(publishedEssayRepository.findAllByOrderByPublishedAtDesc(any(Pageable.class))).thenReturn(page);

        EssayPageDto result = essayService.getEssays(0, 20);

        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(20);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

        EssayDto dto = result.getContent().get(0);
        assertThat(dto.getSlug()).isEqualTo("test-essay");
        assertThat(dto.getTitle()).isEqualTo("Test Essay Title");
        assertThat(dto.getSummary()).isEqualTo("Summary text");
        assertThat(dto.getContent()).isEqualTo("Full essay content");
        assertThat(dto.getPublishedAt()).isEqualTo(publishedAt);
    }

    @Test
    void testGetEssaysInvalidPageThrowsException() {
        assertThatThrownBy(() -> essayService.getEssays(-1, 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Page index must not be less than zero");
    }

    @Test
    void testGetEssaysInvalidSizeThrowsException() {
        assertThatThrownBy(() -> essayService.getEssays(0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Page size must be between 1 and 100");

        assertThatThrownBy(() -> essayService.getEssays(0, 101))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Page size must be between 1 and 100");
    }

    @Test
    void testGetEssayBySlugFound() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 12, 10, 0, 0, 0, ZoneOffset.UTC);
        PublishedEssay essay = new PublishedEssay("deontic-systems", "Deontic Systems", "Deontic logic essay summary", "Content of deontic systems", "Curator", publishedAt);

        when(publishedEssayRepository.findBySlug("deontic-systems")).thenReturn(Optional.of(essay));

        Optional<EssayDto> result = essayService.getEssayBySlug("deontic-systems");

        assertThat(result).isPresent();
        assertThat(result.get().getSlug()).isEqualTo("deontic-systems");
        assertThat(result.get().getTitle()).isEqualTo("Deontic Systems");
        assertThat(result.get().getSummary()).isEqualTo("Deontic logic essay summary");
        assertThat(result.get().getContent()).isEqualTo("Content of deontic systems");
    }

    @Test
    void testGetEssayBySlugNotFound() {
        when(publishedEssayRepository.findBySlug("non-existent")).thenReturn(Optional.empty());

        Optional<EssayDto> result = essayService.getEssayBySlug("non-existent");

        assertThat(result).isEmpty();
    }

    @Test
    void testCreateEssaySuccess() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 24, 12, 0, 0, 0, ZoneOffset.UTC);
        EssayDto input = new EssayDto(null, "new-essay", "New Essay", "Summary", "Markdown Content", publishedAt, null);

        PublishedEssay saved = new PublishedEssay("new-essay", "New Essay", "Summary", "Markdown Content", "Dmitry Efremov", publishedAt);
        saved.setId(100L);

        when(publishedEssayRepository.findBySlug("new-essay")).thenReturn(Optional.empty());
        when(publishedEssayRepository.save(any(PublishedEssay.class))).thenReturn(saved);

        EssayDto result = essayService.createEssay(input);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getSlug()).isEqualTo("new-essay");
        assertThat(result.getTitle()).isEqualTo("New Essay");
        assertThat(result.getSummary()).isEqualTo("Summary");
        assertThat(result.getContent()).isEqualTo("Markdown Content");
    }

    @Test
    void testCreateEssayDuplicateSlugThrowsException() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 24, 12, 0, 0, 0, ZoneOffset.UTC);
        EssayDto input = new EssayDto(null, "existing-slug", "Title", "Summary", "Content", publishedAt, null);

        PublishedEssay existing = new PublishedEssay("existing-slug", "Existing", "Summary", "Content", "Dmitry Efremov", publishedAt);
        when(publishedEssayRepository.findBySlug("existing-slug")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> essayService.createEssay(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Essay with slug 'existing-slug' already exists");
    }

    @Test
    void testCreateEssayMissingRequiredFieldsThrowsException() {
        EssayDto missingSlug = new EssayDto(null, "", "Title", "Summary", "Content", null, null);
        assertThatThrownBy(() -> essayService.createEssay(missingSlug))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Essay slug is required");

        EssayDto missingTitle = new EssayDto(null, "slug", "", "Summary", "Content", null, null);
        assertThatThrownBy(() -> essayService.createEssay(missingTitle))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Essay title is required");

        EssayDto missingContent = new EssayDto(null, "slug", "Title", "Summary", "", null, null);
        assertThatThrownBy(() -> essayService.createEssay(missingContent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Essay content is required");
    }
}
