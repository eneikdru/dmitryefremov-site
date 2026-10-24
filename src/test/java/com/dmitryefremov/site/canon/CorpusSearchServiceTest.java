package com.dmitryefremov.site.canon;

import com.dmitryefremov.site.article.domain.Article;
import com.dmitryefremov.site.article.repository.ArticleRepository;
import com.dmitryefremov.site.canon.domain.CanonDomain;
import com.dmitryefremov.site.canon.domain.CanonItem;
import com.dmitryefremov.site.canon.domain.CanonType;
import com.dmitryefremov.site.canon.dto.CorpusSearchDto;
import com.dmitryefremov.site.canon.dto.SearchResultItemDto;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
import com.dmitryefremov.site.canon.service.CorpusSearchService;
import com.dmitryefremov.site.essay.domain.Essay;
import com.dmitryefremov.site.essay.repository.EssayRepository;
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
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorpusSearchServiceTest {

    @Mock
    private CanonItemRepository canonItemRepository;

    @Mock
    private PodcastEpisodeRepository podcastEpisodeRepository;

    @Mock
    private EssayRepository essayRepository;

    @Mock
    private SparkRepository sparkRepository;

    @Mock
    private ArticleRepository articleRepository;

    private CorpusSearchService corpusSearchService;
    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2026-10-24T12:00:00Z"), ZoneId.of("UTC"));
        corpusSearchService = new CorpusSearchService(
                canonItemRepository,
                podcastEpisodeRepository,
                essayRepository,
                sparkRepository,
                articleRepository
        );
    }

    @Test
    void testSearchCorpusAcrossAllContentTypes() {
        OffsetDateTime now = OffsetDateTime.now(clock);

        CanonItem canonItem = new CanonItem("modal-logic", "Modal Logic Foundation", CanonType.ONTOLOGY, CanonDomain.AUTONOMOUS_ENGINEERING, "Summary modal logic", "Detailed content on modal logic", now, clock);
        when(canonItemRepository.findAll()).thenReturn(List.of(canonItem));

        PodcastEpisode episode = new PodcastEpisode("ruth-barcan", "Ruth Barcan Marcus Modal Logic", "Thesis modal logic", "Description on modal logic", "http://audio.mp3", "http://media.mp3", 1800, now);
        when(podcastEpisodeRepository.findAll()).thenReturn(List.of(episode));

        Essay essay = new Essay("essential-invariants", "Essential Invariants in Logic", "Summary invariants", "Essay content mentioning modal logic", now);
        when(essayRepository.findAll()).thenReturn(List.of(essay));

        Spark spark = new Spark(101L, 202L, "Quick note on modal logic", "Logic", "#logic", "Author", now);
        when(sparkRepository.findAll()).thenReturn(List.of(spark));

        Article article = new Article("deontic-architecture", "Deontic Logic Architecture", "Summary deontic", "Article on deontic systems", now);
        when(articleRepository.findAll()).thenReturn(List.of(article));

        CorpusSearchDto searchResult = corpusSearchService.searchCorpus("modal logic");

        assertThat(searchResult.getQuery()).isEqualTo("modal logic");
        assertThat(searchResult.getTotalResults()).isEqualTo(4);
        assertThat(searchResult.getResults())
                .extracting(SearchResultItemDto::getKind)
                .containsExactlyInAnyOrder("CANON", "PODCAST", "ESSAY", "SPARK");
    }

    @Test
    void testEmptySearchQueryReturnsEmptyResults() {
        CorpusSearchDto result = corpusSearchService.searchCorpus("   ");
        assertThat(result.getTotalResults()).isEqualTo(0);
        assertThat(result.getResults()).isEmpty();
    }
}
