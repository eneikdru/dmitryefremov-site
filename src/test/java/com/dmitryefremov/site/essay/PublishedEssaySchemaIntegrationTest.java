package com.dmitryefremov.site.essay;

import com.dmitryefremov.site.essay.domain.Essay;
import com.dmitryefremov.site.essay.domain.PublishedEssay;
import com.dmitryefremov.site.essay.repository.EssayRepository;
import com.dmitryefremov.site.essay.repository.PublishedEssayRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PublishedEssaySchemaIntegrationTest {

    @Autowired
    private PublishedEssayRepository publishedEssayRepository;

    @Autowired
    private EssayRepository essayRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFlywayMigrationCreatedPublishedEssaysTable() {
        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'PUBLISHED_ESSAYS'",
                Integer.class
        );

        assertThat(tableCount).isGreaterThan(0);
    }

    @Test
    void testFlywayMigrationCreatedIndexes() {
        List<String> indexNames = jdbcTemplate.queryForList(
                "SELECT UPPER(index_name) FROM information_schema.indexes WHERE UPPER(table_name) = 'PUBLISHED_ESSAYS'",
                String.class
        );

        assertThat(indexNames).contains("IDX_PUBLISHED_ESSAYS_SLUG", "IDX_PUBLISHED_ESSAYS_PUBLISHED_AT");
    }

    @Test
    void testPublishedEssaySaveAndRetrieve() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        PublishedEssay essay = new PublishedEssay(
                "curated-essay-slug",
                "Curated Essay Title",
                "Summary of curated content",
                "Full curated content for teleprompter isolation.",
                "Dmitry Efremov",
                now
        );

        PublishedEssay saved = publishedEssayRepository.save(essay);
        publishedEssayRepository.flush();

        assertThat(saved.getId()).isNotNull();

        Optional<PublishedEssay> found = publishedEssayRepository.findBySlug("curated-essay-slug");
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Curated Essay Title");
        assertThat(found.get().getCuratedBy()).isEqualTo("Dmitry Efremov");
    }

    @Test
    void testIsolationBetweenPublishedEssaysAndDraftEssays() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        // Draft essay in internal essays table
        Essay draftEssay = new Essay(
                "teleprompter-draft-1",
                "Internal Teleprompter Script Draft",
                "Draft Summary",
                "Internal teleprompter draft notes and raw script...",
                now
        );
        essayRepository.save(draftEssay);

        // Curated essay in published_essays table
        PublishedEssay curatedEssay = new PublishedEssay(
                "curated-final-1",
                "Final Curated Published Essay",
                "Public Summary",
                "Polished public essay content for readers.",
                "Dmitry Efremov",
                now
        );
        publishedEssayRepository.save(curatedEssay);

        essayRepository.flush();
        publishedEssayRepository.flush();

        // Querying published_essays must NOT contain internal draft essays
        Optional<PublishedEssay> curatedByDraftSlug = publishedEssayRepository.findBySlug("teleprompter-draft-1");
        assertThat(curatedByDraftSlug).isEmpty();

        // Querying essays must NOT contain curated essays
        Optional<Essay> draftByCuratedSlug = essayRepository.findBySlug("curated-final-1");
        assertThat(draftByCuratedSlug).isEmpty();

        // Total count in published_essays table is isolated
        List<PublishedEssay> publishedList = publishedEssayRepository.findAll();
        assertThat(publishedList).extracting(PublishedEssay::getSlug).contains("curated-final-1").doesNotContain("teleprompter-draft-1");
    }
}
