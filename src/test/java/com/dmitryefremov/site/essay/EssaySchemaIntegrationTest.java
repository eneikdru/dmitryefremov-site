package com.dmitryefremov.site.essay;

import com.dmitryefremov.site.essay.domain.Essay;
import com.dmitryefremov.site.essay.repository.EssayRepository;
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
class EssaySchemaIntegrationTest {

    @Autowired
    private EssayRepository essayRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFlywayMigrationCreatedEssaysTable() {
        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'ESSAYS'",
                Integer.class
        );

        assertThat(tableCount).isGreaterThan(0);
    }

    @Test
    void testFlywayMigrationCreatedIndexes() {
        List<String> indexNames = jdbcTemplate.queryForList(
                "SELECT UPPER(index_name) FROM information_schema.indexes WHERE UPPER(table_name) = 'ESSAYS'",
                String.class
        );

        assertThat(indexNames).contains("IDX_ESSAYS_SLUG", "IDX_ESSAYS_PUBLISHED_AT");
    }

    @Test
    void testSaveAndRetrieveEssayBySlug() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 12, 18, 0, 0, 0, ZoneOffset.UTC);

        String longContent = "# High-End Dark / Editorial Essay\n\n" + "Text content... ".repeat(100);

        Essay essay = new Essay(
                "editorial-essay-slug",
                "High-End Dark Editorial Essay",
                "Summary of the editorial essay",
                longContent,
                publishedAt
        );

        Essay saved = essayRepository.save(essay);
        essayRepository.flush();

        assertThat(saved.getId()).isNotNull();

        Optional<Essay> fetchedOptional = essayRepository.findBySlug("editorial-essay-slug");
        assertThat(fetchedOptional).isPresent();

        Essay fetched = fetchedOptional.get();
        assertThat(fetched.getTitle()).isEqualTo("High-End Dark Editorial Essay");
        assertThat(fetched.getSummary()).isEqualTo("Summary of the editorial essay");
        assertThat(fetched.getContent()).isEqualTo(longContent);
        assertThat(fetched.getPublishedAt()).isEqualTo(publishedAt);
        assertThat(fetched.getCreatedAt()).isNotNull();
    }

    @Test
    void testFindAllByOrderByPublishedAtDesc() {
        essayRepository.deleteAll();

        OffsetDateTime baseTime = OffsetDateTime.of(2026, 10, 12, 12, 0, 0, 0, ZoneOffset.UTC);

        Essay essay1 = new Essay("essay-1", "Essay 1", "Summary 1", "Content 1", baseTime.minusDays(2));
        Essay essay2 = new Essay("essay-2", "Essay 2", "Summary 2", "Content 2", baseTime);
        Essay essay3 = new Essay("essay-3", "Essay 3", "Summary 3", "Content 3", baseTime.minusDays(1));

        essayRepository.save(essay1);
        essayRepository.save(essay2);
        essayRepository.save(essay3);
        essayRepository.flush();

        Page<Essay> page = essayRepository.findAllByOrderByPublishedAtDesc(PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getContent().get(0).getSlug()).isEqualTo("essay-2");
        assertThat(page.getContent().get(1).getSlug()).isEqualTo("essay-3");
        assertThat(page.getContent().get(2).getSlug()).isEqualTo("essay-1");
    }
}
