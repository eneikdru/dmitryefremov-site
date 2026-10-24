package com.dmitryefremov.site.canon;

import com.dmitryefremov.site.canon.domain.*;
import com.dmitryefremov.site.canon.dto.CanonCrossReferenceDto;
import com.dmitryefremov.site.canon.dto.CanonItemDto;
import com.dmitryefremov.site.canon.dto.CanonPageDto;
import com.dmitryefremov.site.canon.repository.CanonCrossReferenceRepository;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
import com.dmitryefremov.site.canon.repository.ConceptMediaLinkRepository;
import com.dmitryefremov.site.canon.service.CanonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CanonServiceTest {

    @Mock
    private CanonItemRepository canonItemRepository;

    @Mock
    private CanonCrossReferenceRepository canonCrossReferenceRepository;

    @Mock
    private ConceptMediaLinkRepository conceptMediaLinkRepository;

    private Clock clock;
    private CanonService canonService;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2026-10-12T20:00:00Z"), ZoneId.of("UTC"));
        canonService = new CanonService(canonItemRepository, canonCrossReferenceRepository, conceptMediaLinkRepository, clock);
    }

    @Test
    void testGetCanonItemsFilteredByDomainAndType() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 12, 12, 0, 0, 0, ZoneOffset.UTC);
        CanonItem item = new CanonItem(
                "sovereign-ops",
                "Sovereign Digital Operations",
                CanonType.MANIFESTO,
                CanonDomain.PRAGMATIC_BUSINESS_STRATEGY,
                "Summary",
                "Content",
                publishedAt,
                clock
        );

        when(canonItemRepository.findByDomainAndTypeOrderByPublishedAtDesc(
                eq(CanonDomain.PRAGMATIC_BUSINESS_STRATEGY), eq(CanonType.MANIFESTO), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item), PageRequest.of(0, 20), 1));

        CanonPageDto result = canonService.getCanonItems(CanonDomain.PRAGMATIC_BUSINESS_STRATEGY, CanonType.MANIFESTO, 0, 20);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getSlug()).isEqualTo("sovereign-ops");
        assertThat(result.getContent().get(0).getDomain()).isEqualTo(CanonDomain.PRAGMATIC_BUSINESS_STRATEGY);
    }

    @Test
    void testCreateCanonItem() {
        CanonItemDto dto = new CanonItemDto();
        dto.setSlug("test-manifesto");
        dto.setTitle("Test Manifesto Title");
        dto.setType(CanonType.MANIFESTO);
        dto.setDomain(CanonDomain.PHILOSOPHY_OF_MIND);
        dto.setContent("# Content");

        when(canonItemRepository.findBySlug("test-manifesto")).thenReturn(Optional.empty());
        when(canonItemRepository.save(any(CanonItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CanonItemDto created = canonService.createCanonItem(dto);

        assertThat(created.getSlug()).isEqualTo("test-manifesto");
        assertThat(created.getDomain()).isEqualTo(CanonDomain.PHILOSOPHY_OF_MIND);
        assertThat(created.getType()).isEqualTo(CanonType.MANIFESTO);
    }

    @Test
    void testAddCrossReference() {
        OffsetDateTime now = OffsetDateTime.now(clock);
        CanonItem source = new CanonItem("source-item", "Source Item", CanonType.MANIFESTO, CanonDomain.AUTONOMOUS_ENGINEERING, "Summary", "Content", now, clock);
        ReflectionTestUtils.setField(source, "id", 101L);

        CanonItem target = new CanonItem("target-item", "Target Item", CanonType.GLOSSARY_TERM, CanonDomain.AUTONOMOUS_ENGINEERING, "Summary", "Content", now, clock);
        ReflectionTestUtils.setField(target, "id", 102L);

        when(canonItemRepository.findBySlug("source-item")).thenReturn(Optional.of(source));
        when(canonItemRepository.findBySlug("target-item")).thenReturn(Optional.of(target));
        when(canonCrossReferenceRepository.findBySourceIdAndTargetIdAndRelationshipType(eq(101L), eq(102L), eq(CanonRelationshipType.REFERENCES)))
                .thenReturn(Optional.empty());
        when(canonCrossReferenceRepository.save(any(CanonCrossReference.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CanonCrossReferenceDto xref = canonService.addCrossReference("source-item", "target-item", CanonRelationshipType.REFERENCES, "Deep link description");

        assertThat(xref.getSourceSlug()).isEqualTo("source-item");
        assertThat(xref.getTargetSlug()).isEqualTo("target-item");
        assertThat(xref.getRelationshipType()).isEqualTo(CanonRelationshipType.REFERENCES);
        assertThat(xref.getDescription()).isEqualTo("Deep link description");
    }

    @Test
    void testAddCrossReferenceToSelfThrowsException() {
        assertThatThrownBy(() -> canonService.addCrossReference("same-item", "same-item", CanonRelationshipType.REFERENCES, "Self ref"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Canon item cannot reference itself");
    }
}
