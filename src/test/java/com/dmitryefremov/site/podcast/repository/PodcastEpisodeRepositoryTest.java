package com.dmitryefremov.site.podcast.repository;

import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.domain.PodcastTimecode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PodcastEpisodeRepositoryTest {

    @Autowired
    private PodcastEpisodeRepository podcastEpisodeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFlywayMigrationCreatedTables() {
        Integer episodeTableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'PODCAST_EPISODES'",
                Integer.class
        );
        Integer timecodeTableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'PODCAST_TIMECODES'",
                Integer.class
        );

        assertThat(episodeTableCount).isGreaterThan(0);
        assertThat(timecodeTableCount).isGreaterThan(0);
    }

    @Test
    void testInsertEpisodeWithMultipleTimecodes() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 9, 12, 0, 0, 0, ZoneOffset.UTC);

        PodcastEpisode episode = new PodcastEpisode(
                "systems-and-reality-ep1",
                "Системы и реальность: Выпуск 1",
                "Тезисы о деонтической логике и теории ограничений.",
                "Полное описание первенца подкаста.",
                "https://cdn.dmitryefremov.com/podcast/ep1.mp3",
                "https://youtube.com/watch?v=ep1",
                3600,
                publishedAt
        );

        PodcastTimecode timecode1 = new PodcastTimecode(0, "Введение и манифест", "Вводная часть о системах", 1);
        PodcastTimecode timecode2 = new PodcastTimecode(600, "Теория ограничений", "Анализ узких мест", 2);
        PodcastTimecode timecode3 = new PodcastTimecode(1800, "Деонтическая логика", "Формализация должного", 3);

        episode.addTimecode(timecode1);
        episode.addTimecode(timecode2);
        episode.addTimecode(timecode3);

        PodcastEpisode savedEpisode = podcastEpisodeRepository.save(episode);
        podcastEpisodeRepository.flush();

        assertThat(savedEpisode.getId()).isNotNull();

        Optional<PodcastEpisode> fetchedOptional = podcastEpisodeRepository.findBySlug("systems-and-reality-ep1");
        assertThat(fetchedOptional).isPresent();

        PodcastEpisode fetchedEpisode = fetchedOptional.get();
        assertThat(fetchedEpisode.getTitle()).isEqualTo("Системы и реальность: Выпуск 1");
        assertThat(fetchedEpisode.getThesis()).contains("деонтической логике");
        assertThat(fetchedEpisode.getTimecodes()).hasSize(3);
        assertThat(fetchedEpisode.getTimecodes())
                .extracting(PodcastTimecode::getTitle)
                .containsExactly("Введение и манифест", "Теория ограничений", "Деонтическая логика");
    }
}
