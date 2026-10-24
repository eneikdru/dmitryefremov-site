package com.dmitryefremov.site.podcast.repository;

import com.dmitryefremov.site.podcast.domain.EpisodeTopic;
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
        Integer topicsTableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'EPISODE_TOPICS'",
                Integer.class
        );
        Integer episodesViewCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'EPISODES'",
                Integer.class
        );

        assertThat(episodeTableCount).isGreaterThan(0);
        assertThat(timecodeTableCount).isGreaterThan(0);
        assertThat(quoteTableCount).isGreaterThan(0);
        assertThat(topicsTableCount).isGreaterThan(0);
        assertThat(episodesViewCount).isGreaterThan(0);
    }

    @Test
    void testPodcastEpisodesTableAndEpisodesViewContainAudioUrlAndMetadataColumns() {
        Integer audioUrlColCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE UPPER(table_name) = 'PODCAST_EPISODES' AND UPPER(column_name) = 'AUDIO_URL'",
                Integer.class
        );
        Integer metadataColCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE UPPER(table_name) = 'PODCAST_EPISODES' AND UPPER(column_name) = 'METADATA'",
                Integer.class
        );
        Integer viewAudioUrlColCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE UPPER(table_name) = 'EPISODES' AND UPPER(column_name) = 'AUDIO_URL'",
                Integer.class
        );
        Integer viewMetadataColCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE UPPER(table_name) = 'EPISODES' AND UPPER(column_name) = 'METADATA'",
                Integer.class
        );

        assertThat(audioUrlColCount).isEqualTo(1);
        assertThat(metadataColCount).isEqualTo(1);
        assertThat(viewAudioUrlColCount).isEqualTo(1);
        assertThat(viewMetadataColCount).isEqualTo(1);
    }

    @Test
    void testInsertEpisodeWithAudioUrlAndMetadata() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 13, 14, 0, 0, 0, ZoneOffset.UTC);

        PodcastEpisode episode = new PodcastEpisode(
                "metadata-test-ep",
                "Метаданные подкаста",
                "Тезис о структурированных метаданных",
                "Подробное описание метаданных",
                "https://cdn.dmitryefremov.com/podcast/metadata-test.mp3",
                "https://dmitryefremov.com/podcasts/metadata-test",
                1800,
                publishedAt
        );
        episode.setMetadata("{\"bitrate\":\"320kbps\",\"format\":\"mp3\",\"file_size_bytes\":4321000}");

        PodcastEpisode saved = podcastEpisodeRepository.save(episode);
        podcastEpisodeRepository.flush();

        assertThat(saved.getId()).isNotNull();

        Optional<PodcastEpisode> fetched = podcastEpisodeRepository.findBySlug("metadata-test-ep");
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getAudioUrl()).isEqualTo("https://cdn.dmitryefremov.com/podcast/metadata-test.mp3");
        assertThat(fetched.get().getMetadata()).isEqualTo("{\"bitrate\":\"320kbps\",\"format\":\"mp3\",\"file_size_bytes\":4321000}");

        String viewMetadata = jdbcTemplate.queryForObject(
                "SELECT metadata FROM episodes WHERE slug = 'metadata-test-ep'",
                String.class
        );
        assertThat(viewMetadata).isEqualTo("{\"bitrate\":\"320kbps\",\"format\":\"mp3\",\"file_size_bytes\":4321000}");
    }

    @Test
    void testSeededPodcastEpisodesArePresentInRepository() {
        Optional<PodcastEpisode> ep1 = podcastEpisodeRepository.findBySlug("systems-and-reality-ep1");
        assertThat(ep1).isPresent();
        assertThat(ep1.get().getTitle()).contains("Системы и реальность #1");
        assertThat(ep1.get().getTimecodes()).hasSize(3);
        assertThat(ep1.get().getTopics()).hasSize(3);

        EpisodeTopic topic1 = ep1.get().getTopics().get(0);
        assertThat(topic1.getTopicName()).isEqualTo("Модальная логика и философия языка");
        assertThat(topic1.getStartTimeSeconds()).isEqualTo(0);
        assertThat(topic1.getEndTimeSeconds()).isEqualTo(900);

        Optional<PodcastEpisode> ep2 = podcastEpisodeRepository.findBySlug("systems-and-reality-ep2");
        assertThat(ep2).isPresent();
        assertThat(ep2.get().getTitle()).contains("Системы и реальность #2");
    }

    @Test
    void testInsertEpisodeWithTopicsReturnsTopicTimestamps() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 15, 10, 0, 0, 0, ZoneOffset.UTC);

        PodcastEpisode episode = new PodcastEpisode(
                "topics-test-ep",
                "Эпизод с темированием",
                "Тезис о навигации по темам",
                "Описание эпизода с темами",
                "https://cdn.dmitryefremov.com/podcast/topics-test.mp3",
                "https://dmitryefremov.com/podcasts/topics-test",
                3600,
                publishedAt
        );

        EpisodeTopic topic1 = new EpisodeTopic("Рут Баркан Маркус: Модальности", 0, 1200, "Формула Баркан и модальная логика", 1);
        EpisodeTopic topic2 = new EpisodeTopic("Пол Тагард: Когнитивистика", 1200, 2400, "Вычислительные модели и сопоставление гипотез", 2);
        EpisodeTopic topic3 = new EpisodeTopic("Сюзан Херли: Расширенное сознание", 2400, 3600, "Внешние когнитивные интерфейсы", 3);

        episode.addTopic(topic1);
        episode.addTopic(topic2);
        episode.addTopic(topic3);

        PodcastEpisode saved = podcastEpisodeRepository.save(episode);
        podcastEpisodeRepository.flush();

        assertThat(saved.getId()).isNotNull();

        Optional<PodcastEpisode> fetched = podcastEpisodeRepository.findBySlug("topics-test-ep");
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getTopics()).hasSize(3);

        EpisodeTopic fetchedTopic2 = fetched.get().getTopics().get(1);
        assertThat(fetchedTopic2.getTopicName()).isEqualTo("Пол Тагард: Когнитивистика");
        assertThat(fetchedTopic2.getStartTimeSeconds()).isEqualTo(1200);
        assertThat(fetchedTopic2.getEndTimeSeconds()).isEqualTo(2400);
        assertThat(fetchedTopic2.getDescription()).contains("Вычислительные модели");
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

        PodcastEpisode saved = podcastEpisodeRepository.save(episode);
        podcastEpisodeRepository.flush();

        assertThat(saved.getId()).isNotNull();

        Optional<PodcastEpisode> fetched = podcastEpisodeRepository.findBySlug("ruth-barcan-marcus-ep1");
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getConceptLinks()).contains("modal-logic");
        assertThat(fetched.get().getQuotes()).hasSize(1);
        assertThat(fetched.get().getQuotes().get(0).getQuoteText()).contains("Идентичность есть необходимое отношение");
        assertThat(fetched.get().getQuotes().get(0).getSpeaker()).isEqualTo("Рут Баркан Маркус");
    }

    @Test
    void testDraftPodcastEpisodesAreExcludedFromPublishedQueriesAndCounts() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 15, 12, 0, 0, 0, ZoneOffset.UTC);

        // Published episode
        PodcastEpisode published = new PodcastEpisode(
                "published-ep-1",
                "Published Episode 1",
                "Published thesis",
                "Published description",
                "https://cdn.dmitryefremov.com/podcast/pub1.mp3",
                "https://dmitryefremov.com/podcasts/pub1",
                1800,
                publishedAt
        );

        // Raw teleprompter draft episode (publishedAt = null)
        PodcastEpisode draft = new PodcastEpisode(
                "teleprompter-draft-ep-raw",
                "Raw Teleprompter Draft Episode",
                "Raw draft thesis",
                "Raw draft description /mnt/HC_Volume_106932004/raw",
                "https://cdn.dmitryefremov.com/podcast/draft.mp3",
                "https://dmitryefremov.com/podcasts/draft",
                1800,
                null
        );

        podcastEpisodeRepository.save(published);
        podcastEpisodeRepository.save(draft);
        podcastEpisodeRepository.flush();

        // 1. Verify findBySlugAndPublishedAtNotNull excludes draft
        Optional<PodcastEpisode> foundDraft = podcastEpisodeRepository.findBySlugAndPublishedAtNotNull("teleprompter-draft-ep-raw");
        assertThat(foundDraft).isEmpty();

        Optional<PodcastEpisode> foundPublished = podcastEpisodeRepository.findBySlugAndPublishedAtNotNull("published-ep-1");
        assertThat(foundPublished).isPresent();

        // 2. Verify findAllByPublishedAtNotNullOrderByPublishedAtDesc excludes draft
        org.springframework.data.domain.Page<PodcastEpisode> page = podcastEpisodeRepository.findAllByPublishedAtNotNullOrderByPublishedAtDesc(org.springframework.data.domain.PageRequest.of(0, 100));
        assertThat(page.getContent()).extracting(PodcastEpisode::getSlug).contains("published-ep-1").doesNotContain("teleprompter-draft-ep-raw");

        // 3. Verify countByPublishedAtNotNull reflects exact live count of published episodes
        long publishedCount = podcastEpisodeRepository.countByPublishedAtNotNull();
        long totalRawCount = podcastEpisodeRepository.count();
        assertThat(publishedCount).isLessThan(totalRawCount);
    }
}
