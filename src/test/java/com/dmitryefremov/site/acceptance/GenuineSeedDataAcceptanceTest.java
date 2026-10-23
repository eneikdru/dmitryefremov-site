package com.dmitryefremov.site.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GenuineSeedDataAcceptanceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Verify genuine manifestos, episode, and sparks are seeded correctly")
    void testGenuineSeedDataExists() {
        // 1. Verify Manifestos in canon_items
        Integer canonCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM canon_items WHERE slug IN ('gilbert-ryle-category-mistake', 'daniel-dennett-intentional-stance')",
                Integer.class
        );
        assertThat(canonCount).isNotNull().isEqualTo(2);

        // 2. Verify Episode in podcast_episodes
        Integer episodeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM podcast_episodes WHERE slug = 'episode-06-ghost-in-the-machine'",
                Integer.class
        );
        assertThat(episodeCount).isNotNull().isEqualTo(1);

        // Verify timecodes for the episode
        Integer timecodeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM podcast_timecodes pt JOIN podcast_episodes pe ON pt.episode_id = pe.id WHERE pe.slug = 'episode-06-ghost-in-the-machine'",
                Integer.class
        );
        assertThat(timecodeCount).isNotNull().isEqualTo(3);

        // 3. Verify Sparks
        Integer sparksCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sparks WHERE telegram_message_id IN (8001, 8002, 8003)",
                Integer.class
        );
        assertThat(sparksCount).isNotNull().isEqualTo(3);
    }
}
