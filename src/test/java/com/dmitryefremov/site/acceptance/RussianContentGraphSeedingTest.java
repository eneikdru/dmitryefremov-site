package com.dmitryefremov.site.acceptance;

import com.dmitryefremov.site.canon.domain.CanonItem;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
import com.dmitryefremov.site.essay.domain.Essay;
import com.dmitryefremov.site.essay.repository.EssayRepository;
import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.repository.PodcastEpisodeRepository;
import com.dmitryefremov.site.spark.domain.Spark;
import com.dmitryefremov.site.spark.repository.SparkRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RussianContentGraphSeedingTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("siteCanonItemRepository")
    private CanonItemRepository canonItemRepository;

    @Autowired
    private EssayRepository essayRepository;

    @Autowired
    private PodcastEpisodeRepository podcastEpisodeRepository;

    @Autowired
    private SparkRepository sparkRepository;

    @Test
    @DisplayName("Verify authentic Russian canon nodes and graph cross-references exist")
    void testCanonNodesAndCrossReferencesSeeded() {
        Optional<CanonItem> canonItem = canonItemRepository.findBySlug("prikladnaya-konfiguraciya-modeli-avtonomii");
        assertThat(canonItem).isPresent();
        assertThat(canonItem.get().getTitle()).contains("Прикладная конфигурация модели автономии");
        assertThat(canonItem.get().getContent()).contains("Обязательная проверка");

        Integer xrefCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM canon_cross_references WHERE description LIKE ?",
                Integer.class,
                "%расширяет деонтическую автономию агентов%"
        );
        assertThat(xrefCount).isNotNull().isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Verify authentic Russian essays are seeded in essays and articles tables")
    void testRussianEssaysSeeded() {
        Optional<Essay> essay = essayRepository.findBySlug("vneshnyaya-rabochaya-pamyat-i-graf-ponyatiy");
        assertThat(essay).isPresent();
        assertThat(essay.get().getTitle()).contains("внешняя рабочая память");

        Integer articleCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM articles WHERE slug = ?",
                Integer.class,
                "vneshnyaya-rabochaya-pamyat-i-graf-ponyatiy"
        );
        assertThat(articleCount).isNotNull().isEqualTo(1);
    }

    @Test
    @DisplayName("Verify authentic Russian podcast episode with timecodes and quotes is seeded")
    void testRussianPodcastEpisodeSeeded() {
        Optional<PodcastEpisode> ep = podcastEpisodeRepository.findBySlug("vypusk-05-rasshirennyy-razum-i-kognitivnaya-arhitektura");
        assertThat(ep).isPresent();
        assertThat(ep.get().getTitle()).contains("Выпуск 05");
        assertThat(ep.get().getYoutubeUrl()).contains("youtube.com");
        assertThat(ep.get().getTimecodes()).hasSizeGreaterThanOrEqualTo(3);

        Integer quoteCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM podcast_quotes WHERE episode_id = ?",
                Integer.class,
                ep.get().getId()
        );
        assertThat(quoteCount).isNotNull().isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Verify authentic Russian Telegram sparks are seeded")
    void testRussianSparksSeeded() {
        Optional<Spark> spark = sparkRepository.findByTelegramMessageId(8001L);
        assertThat(spark).isPresent();
        assertThat(spark.get().getContent()).contains("Архитектура расширенного разума");
        assertThat(spark.get().getAuthor()).isEqualTo("Дмитрий Ефремов");
    }
}
