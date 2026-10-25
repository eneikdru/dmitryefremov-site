package com.dmitryefremov.site.knowledgebase.service;

import com.dmitryefremov.site.canon.domain.CanonDomain;
import com.dmitryefremov.site.canon.domain.CanonItem;
import com.dmitryefremov.site.canon.domain.CanonType;
import com.dmitryefremov.site.canon.repository.CanonCrossReferenceRepository;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
import com.dmitryefremov.site.essay.domain.PublishedEssay;
import com.dmitryefremov.site.essay.repository.PublishedEssayRepository;
import com.dmitryefremov.site.knowledgebase.domain.AppliedModelConfiguration;
import com.dmitryefremov.site.knowledgebase.domain.KnowledgeBaseConcept;
import com.dmitryefremov.site.knowledgebase.dto.*;
import com.dmitryefremov.site.knowledgebase.repository.AppliedModelConfigurationRepository;
import com.dmitryefremov.site.knowledgebase.repository.KnowledgeBaseConceptRepository;
import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import com.dmitryefremov.site.spark.domain.Spark;
import com.dmitryefremov.site.spark.repository.SparkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KnowledgeBaseSearchServiceTest {

    @Mock
    private KnowledgeBaseConceptRepository conceptRepository;
    @Mock
    private AppliedModelConfigurationRepository modelConfigurationRepository;
    @Mock
    private CanonItemRepository canonItemRepository;
    @Mock
    private CanonCrossReferenceRepository canonCrossReferenceRepository;
    @Mock
    private PodcastEpisodeRepository podcastEpisodeRepository;
    @Mock
    private PublishedEssayRepository essayRepository;
    @Mock
    private SparkRepository sparkRepository;

    private Clock fixedClock;
    private KnowledgeBaseSearchService searchService;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-10-25T10:00:00Z"), ZoneOffset.UTC);
        searchService = new KnowledgeBaseSearchService(
                conceptRepository,
                modelConfigurationRepository,
                canonItemRepository,
                canonCrossReferenceRepository,
                podcastEpisodeRepository,
                essayRepository,
                sparkRepository,
                fixedClock
        );
    }

    @Test
    void testSearchWithEmptyQueryReturnsEmpty() {
        SearchResponse response = searchService.search("  ", null, 20, 0);
        assertThat(response.getItems()).isEmpty();
        assertThat(response.getTotalResults()).isEqualTo(0);
    }

    @Test
    void testSearchMatchesMultiDomain() {
        CanonItem canon = new CanonItem("modal-logic", "Modal Logic System", CanonType.MANIFESTO, CanonDomain.AUTONOMOUS_ENGINEERING, "Summary", "Modal logic content", OffsetDateTime.now(fixedClock));
        when(canonItemRepository.findAll()).thenReturn(List.of(canon));

        PodcastEpisode ep = new PodcastEpisode("ep1", "Modal Episode", "Thesis", "Modal description", "audio.mp3", "media", "yt", "tg", "sp", "ap", "platforms", 3600, OffsetDateTime.now(fixedClock));
        ep.setConceptLinks("modal-logic");
        when(podcastEpisodeRepository.findAll()).thenReturn(List.of(ep));

        when(conceptRepository.searchByQuery(any())).thenReturn(Collections.emptyList());

        SearchResponse response = searchService.search("Modal", List.of(SearchDomain.CANON, SearchDomain.PODCAST), 20, 0);

        assertThat(response.getTotalResults()).isEqualTo(2);
        assertThat(response.getItems()).extracting(SearchResultItem::getSlug)
                .containsExactlyInAnyOrder("modal-logic", "ep1");
    }

    @Test
    void testGetGraphNodeBySlugWithModelConfiguration() {
        KnowledgeBaseConcept kb = new KnowledgeBaseConcept("deontic-agent", "Deontic Agent", "AUTONOMOUS", "Deontic agent concept", "Core content", fixedClock);
        when(canonItemRepository.findBySlug("deontic-agent")).thenReturn(Optional.empty());
        when(conceptRepository.findBySlug("deontic-agent")).thenReturn(Optional.of(kb));

        AppliedModelConfiguration config = new AppliedModelConfiguration(kb, "DeonticRules", "1.0.0", "{\"rule\":\"O(A)\"}", true, fixedClock);
        when(modelConfigurationRepository.findByConceptIdAndActiveTrue(kb.getId())).thenReturn(List.of(config));

        Optional<GraphNodeDetail> detailOpt = searchService.getGraphNodeBySlug("deontic-agent");

        assertThat(detailOpt).isPresent();
        GraphNodeDetail detail = detailOpt.get();
        assertThat(detail.getNode().getTitle()).isEqualTo("Deontic Agent");
        assertThat(detail.getContent()).contains("Applied Model Configurations").contains("DeonticRules");
    }
}
