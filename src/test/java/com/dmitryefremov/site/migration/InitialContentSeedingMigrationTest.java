package com.dmitryefremov.site.migration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InitialContentSeedingMigrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Verify that seeding migration populates realistic authentic materials, audio podcast, and demo API key")
    void verifyInitialContentSeeded() {
        // 1. Verify authentic materials exist in articles
        Integer articleCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM articles WHERE slug = ?",
                Integer.class,
                "high-end-scrollytelling-architecture"
        );
        assertThat(articleCount).isNotNull().isGreaterThanOrEqualTo(1);

        // 2. Verify audio podcast episode exists with valid audio URL
        Integer podcastCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM podcast_episodes WHERE slug = ? AND audio_url IS NOT NULL",
                Integer.class,
                "scrollytelling-and-a2a-podcast-ep5"
        );
        assertThat(podcastCount).isNotNull().isGreaterThanOrEqualTo(1);

        // 3. Verify demo API key exists in api_credentials
        Integer apiKeyCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM api_credentials WHERE client_id = ?",
                Integer.class,
                "demo-api-key-client"
        );
        assertThat(apiKeyCount).isNotNull().isGreaterThanOrEqualTo(1);
    }
}
