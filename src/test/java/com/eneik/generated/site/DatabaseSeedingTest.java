package com.eneik.generated.site;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DatabaseSeedingTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Verify publication_schedule table is created and populated with seed data")
    void verifyPublicationScheduleSeeding() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM publication_schedule", Integer.class);
        assertThat(count).isGreaterThanOrEqualTo(3);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT title, topic, platform, status FROM publication_schedule ORDER BY id ASC"
        );
        assertThat(rows).hasSizeGreaterThanOrEqualTo(3);

        assertThat(rows.get(0).get("platform")).isEqualTo("TELEGRAM");
        assertThat(rows.get(0).get("status")).isEqualTo("PUBLISHED");
        assertThat(rows.get(1).get("platform")).isEqualTo("YOUTUBE");
        assertThat(rows.get(1).get("status")).isEqualTo("IN_PRODUCTION");
        assertThat(rows.get(2).get("platform")).isEqualTo("ARTICLE");
        assertThat(rows.get(2).get("status")).isEqualTo("SCHEDULED");
    }

    @Test
    @DisplayName("Verify podcast_episodes table is created and populated with seed data")
    void verifyPodcastEpisodesSeeding() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM podcast_episodes", Integer.class);
        assertThat(count).isGreaterThanOrEqualTo(2);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT episode_number, title, audio_url, duration_seconds FROM podcast_episodes ORDER BY episode_number ASC"
        );

        assertThat(((Number) rows.get(0).get("episode_number")).intValue()).isEqualTo(1);
        assertThat((String) rows.get(0).get("title")).contains("Системы и реальность");
        assertThat(((Number) rows.get(1).get("episode_number")).intValue()).isEqualTo(2);
    }

    @Test
    @DisplayName("Verify articles table is created and populated with seed data")
    void verifyArticlesSeeding() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM articles", Integer.class);
        assertThat(count).isGreaterThanOrEqualTo(2);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT slug, title, reading_time_minutes, status FROM articles ORDER BY id ASC"
        );

        assertThat(rows.get(0).get("slug")).isEqualTo("deontic-logic-in-software-architecture");
        assertThat(rows.get(0).get("status")).isEqualTo("PUBLISHED");
        assertThat(rows.get(1).get("slug")).isEqualTo("theory-of-constraints-autonomous-systems");
    }
}
