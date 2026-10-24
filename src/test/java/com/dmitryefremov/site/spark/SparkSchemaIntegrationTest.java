package com.dmitryefremov.site.spark;

import com.dmitryefremov.site.spark.domain.Spark;
import com.dmitryefremov.site.spark.repository.SparkRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SparkSchemaIntegrationTest {

    @Autowired
    private SparkRepository sparkRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFlywayMigrationCreatedSparksTable() {
        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'SPARKS'",
                Integer.class
        );

        assertThat(tableCount).isGreaterThan(0);
    }

    @Test
    void testFlywayMigrationCreatedIndexes() {
        List<String> indexNames = jdbcTemplate.queryForList(
                "SELECT UPPER(index_name) FROM information_schema.indexes WHERE UPPER(table_name) = 'SPARKS'",
                String.class
        );

        assertThat(indexNames).contains("IDX_SPARKS_TELEGRAM_MESSAGE_ID", "IDX_SPARKS_PUBLISHED_AT", "IDX_SPARKS_DOMAIN");
    }

    @Test
    void testSaveAndRetrieveSparkByTelegramMessageId() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 12, 20, 0, 0, 0, ZoneOffset.UTC);

        Spark spark = new Spark(
                8001L,
                -100123456789L,
                "Autonomous engineering insight on deterministic state machines #engineering #architecture",
                "Autonomous Engineering",
                "engineering,architecture",
                "@efremov_mvp",
                publishedAt
        );

        Spark saved = sparkRepository.save(spark);
        sparkRepository.flush();

        assertThat(saved.getId()).isNotNull();

        Optional<Spark> fetchedOptional = sparkRepository.findByTelegramMessageId(8001L);
        assertThat(fetchedOptional).isPresent();

        Spark fetched = fetchedOptional.get();
        assertThat(fetched.getContent()).contains("Autonomous engineering insight");
        assertThat(fetched.getDomain()).isEqualTo("Autonomous Engineering");
        assertThat(fetched.getTags()).isEqualTo("engineering,architecture");
        assertThat(fetched.getAuthor()).isEqualTo("@efremov_mvp");
        assertThat(fetched.getPublishedAt()).isEqualTo(publishedAt);
        assertThat(fetched.getCreatedAt()).isNotNull();
    }

    @Test
    void testFindAllByOrderByPublishedAtDescAndDomainFilter() {
        long initialTotal = sparkRepository.count();

        OffsetDateTime baseTime = OffsetDateTime.of(2026, 10, 12, 12, 0, 0, 0, ZoneOffset.UTC);

        Spark spark1 = new Spark(8101L, 1L, "Content 1 #a2a", "Agentic Economy (A2A)", "a2a", "@author", baseTime.minusDays(2));
        Spark spark2 = new Spark(8102L, 1L, "Content 2 #engineering", "Autonomous Engineering", "engineering", "@author", baseTime);
        Spark spark3 = new Spark(8103L, 1L, "Content 3 #a2a", "Agentic Economy (A2A)", "a2a", "@author", baseTime.minusDays(1));

        sparkRepository.save(spark1);
        sparkRepository.save(spark2);
        sparkRepository.save(spark3);
        sparkRepository.flush();

        Page<Spark> pageAll = sparkRepository.findAllByOrderByPublishedAtDesc(PageRequest.of(0, 50));
        assertThat(pageAll.getContent()).hasSize((int) (initialTotal + 3));
        assertThat(pageAll.getContent().stream().anyMatch(s -> Long.valueOf(8102L).equals(s.getTelegramMessageId()))).isTrue();

        Page<Spark> pageDomain = sparkRepository.findByDomainOrderByPublishedAtDesc("Agentic Economy (A2A)", PageRequest.of(0, 50));
        assertThat(pageDomain.getContent().stream().filter(s -> s.getTelegramMessageId() != null && s.getTelegramMessageId() >= 8101L && s.getTelegramMessageId() <= 8103L)).hasSize(2);
    }
}
