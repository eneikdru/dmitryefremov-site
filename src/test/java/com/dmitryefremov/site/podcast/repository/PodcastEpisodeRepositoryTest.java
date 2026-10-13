package com.dmitryefremov.site.podcast.repository;

import com.dmitryefremov.site.podcast.domain.PodcastEpisode;
import com.dmitryefremov.site.podcast.domain.PodcastQuote;
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
    void testFlywayMigrationCreatedTablesAndEpisodesView() {
        Integer episodeTableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'PODCAST_EPISODES'",
                Integer.class
        );
        Integer timecodeTableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'PODCAST_TIMECODES'",
                Integer.class
        );
        Integer quoteTableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'PODCAST_QUOTES'",
                Integer.class
        );
        Integer episodesViewCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'EPISODES'",
                Integer.class
        );

        assertThat(episodeTableCount).isGreaterThan(0);
        assertThat(timecodeTableCount).isGreaterThan(0);
        assertThat(quoteTableCount).isGreaterThan(0);
        assertThat(episodesViewCount).isGreaterThan(0);

        Integer audioUrlColumn = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE UPPER(table_name) = 'PODCAST_EPISODES' AND UPPER(column_name) = 'AUDIO_URL'",
                Integer.class
        );
        Integer metadataColumn = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE UPPER(table_name) = 'PODCAST_EPISODES' AND UPPER(column_name) = 'METADATA'",
                Integer.class
        );

        assertThat(audioUrlColumn).isEqualTo(1);
        assertThat(metadataColumn).isEqualTo(1);
    }

    @Test
    void testSeededPodcastEpisodesArePresentInRepository() {
        Optional<PodcastEpisode> ep1 = podcastEpisodeRepository.findBySlug("systems-and-reality-ep1");
        assertThat(ep1).isPresent();
        assertThat(ep1.get().getTitle()).contains("Системы и реальность #1");
        assertThat(ep1.get().getTimecodes()).hasSize(3);

        Optional<PodcastEpisode> ep2 = podcastEpisodeRepository.findBySlug("systems-and-reality-ep2");
        assertThat(ep2).isPresent();
        assertThat(ep2.get().getTitle()).contains("Системы и реальность #2");
    }

    @Test
    void testInsertEpisodeWithMultipleTimecodesAndPlatforms() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 9, 12, 0, 0, 0, ZoneOffset.UTC);

        PodcastEpisode episode = new PodcastEpisode(
                "systems-and-reality-ep3",
                "Системы и реальность: Выпуск 3",
                "Тезисы о деонтической логике и теории ограничений.",
                "Полное описание третьего выпуска.",
                "https://cdn.dmitryefremov.com/podcast/ep3.mp3",
                "https://youtube.com/watch?v=ep3",
                "https://youtube.com/watch?v=ep3",
                "https://t.me/efremov_mvp/100",
                "https://open.spotify.com/episode/ep3",
                "https://podcasts.apple.com/podcast/ep3",
                "YOUTUBE,TELEGRAM,SPOTIFY,APPLE",
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

        Optional<PodcastEpisode> fetchedOptional = podcastEpisodeRepository.findBySlug("systems-and-reality-ep3");
        assertThat(fetchedOptional).isPresent();

        PodcastEpisode fetchedEpisode = fetchedOptional.get();
        assertThat(fetchedEpisode.getTitle()).isEqualTo("Системы и реальность: Выпуск 3");
        assertThat(fetchedEpisode.getThesis()).contains("деонтической логике");
        assertThat(fetchedEpisode.getYoutubeUrl()).isEqualTo("https://youtube.com/watch?v=ep3");
        assertThat(fetchedEpisode.getTelegramUrl()).isEqualTo("https://t.me/efremov_mvp/100");
        assertThat(fetchedEpisode.getPlatforms()).isEqualTo("YOUTUBE,TELEGRAM,SPOTIFY,APPLE");
        assertThat(fetchedEpisode.getTimecodes()).hasSize(3);
        assertThat(fetchedEpisode.getTimecodes())
                .extracting(PodcastTimecode::getTitle)
                .containsExactly("Введение и манифест", "Теория ограничений", "Деонтическая логика");

        // Verify episodes view query
        Integer viewCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM episodes WHERE slug = 'systems-and-reality-ep3'",
                Integer.class
        );
        assertThat(viewCount).isEqualTo(1);
    }

    @Test
    void testInsertEpisodeWithQuotesAndConceptLinks() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 12, 10, 0, 0, 0, ZoneOffset.UTC);

        PodcastEpisode episode = new PodcastEpisode(
                "ruth-barcan-marcus-ep1",
                "Философия сознания: Рут Баркан Маркус",
                "Необходимая модальность и прямое наименование.",
                "Разбор философского наследия Рут Баркан Маркус.",
                "https://cdn.dmitryefremov.com/podcast/marcus.mp3",
                "https://youtube.com/watch?v=marcus",
                "https://youtube.com/watch?v=marcus",
                "https://t.me/efremov_mvp/101",
                "https://open.spotify.com/episode/marcus",
                "https://podcasts.apple.com/podcast/marcus",
                "YOUTUBE,TELEGRAM,SPOTIFY,APPLE",
                2700,
                publishedAt
        );
        episode.setConceptLinks("https://dmitryefremov.com/ontology/modal-logic,https://dmitryefremov.com/ontology/direct-reference");

        PodcastTimecode tc = new PodcastTimecode(300, "Формула Баркан", "Обсуждение модальной логики", 1);
        episode.addTimecode(tc);

        PodcastQuote quote = new PodcastQuote(
                "Идентичность есть необходимое отношение между объектом и им самим.",
                "Рут Баркан Маркус",
                360,
                "https://dmitryefremov.com/ontology/modal-logic",
                1
        );
        episode.addQuote(quote);

        episode.setMetadata("{\"bitrate\": \"320kbps\", \"format\": \"mp3\"}");

        PodcastEpisode saved = podcastEpisodeRepository.save(episode);
        podcastEpisodeRepository.flush();

        assertThat(saved.getId()).isNotNull();

        Optional<PodcastEpisode> fetched = podcastEpisodeRepository.findBySlug("ruth-barcan-marcus-ep1");
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getAudioUrl()).isEqualTo("https://cdn.dmitryefremov.com/podcast/marcus.mp3");
        assertThat(fetched.get().getMetadata()).isEqualTo("{\"bitrate\": \"320kbps\", \"format\": \"mp3\"}");
        assertThat(fetched.get().getConceptLinks()).contains("modal-logic");
        assertThat(fetched.get().getQuotes()).hasSize(1);
        assertThat(fetched.get().getQuotes().get(0).getQuoteText()).contains("Идентичность есть необходимое отношение");
        assertThat(fetched.get().getQuotes().get(0).getSpeaker()).isEqualTo("Рут Баркан Маркус");

        String viewMetadata = jdbcTemplate.queryForObject(
                "SELECT metadata FROM episodes WHERE slug = 'ruth-barcan-marcus-ep1'",
                String.class
        );
        assertThat(viewMetadata).isEqualTo("{\"bitrate\": \"320kbps\", \"format\": \"mp3\"}");
    }
}
