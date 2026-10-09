package com.dmitryefremov.site;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DataSeedingIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testSeededArticlesExist() {
        Integer articleCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM articles",
                Integer.class
        );
        assertThat(articleCount).isGreaterThanOrEqualTo(3);

        List<Map<String, Object>> articles = jdbcTemplate.queryForList(
                "SELECT slug, title, summary FROM articles ORDER BY id"
        );
        assertThat(articles).extracting(m -> m.get("slug"))
                .contains("systems-logic", "theory-of-constraints", "autonomous-processes");
    }

    @Test
    void testSeededPodcastEpisodesAndTimecodesExist() {
        Integer episodeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM podcast_episodes",
                Integer.class
        );
        assertThat(episodeCount).isGreaterThanOrEqualTo(2);

        Integer timecodeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM podcast_timecodes",
                Integer.class
        );
        assertThat(timecodeCount).isGreaterThanOrEqualTo(6);

        List<Map<String, Object>> episodes = jdbcTemplate.queryForList(
                "SELECT slug, title FROM podcast_episodes ORDER BY id"
        );
        assertThat(episodes).extracting(m -> m.get("slug"))
                .contains("ep-01-systems-reality", "ep-02-autonomous-processes");
    }

    @Test
    void testSeededPublicationScheduleExists() {
        Integer scheduleCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM publication_schedule",
                Integer.class
        );
        assertThat(scheduleCount).isGreaterThanOrEqualTo(3);

        List<Map<String, Object>> scheduleItems = jdbcTemplate.queryForList(
                "SELECT topic, platform, status FROM publication_schedule ORDER BY id"
        );
        assertThat(scheduleItems).extracting(m -> m.get("platform"))
                .contains("TELEGRAM", "YOUTUBE", "ARTICLE");

        assertThat(scheduleItems).extracting(m -> m.get("status"))
                .contains("PUBLISHED", "IN_PRODUCTION", "SCHEDULED");
    }
}
