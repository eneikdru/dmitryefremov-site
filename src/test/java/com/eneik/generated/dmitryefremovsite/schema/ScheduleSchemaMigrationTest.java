package com.eneik.generated.dmitryefremovsite.schema;

import com.eneik.generated.dmitryefremovsite.domain.Platform;
import com.eneik.generated.dmitryefremovsite.domain.PublicationSchedule;
import com.eneik.generated.dmitryefremovsite.domain.PublicationStatus;
import com.eneik.generated.dmitryefremovsite.repository.PublicationScheduleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.flyway.enabled=true",
    "spring.flyway.locations=classpath:db/migration",
    "spring.jpa.hibernate.ddl-auto=validate"
})
class ScheduleSchemaMigrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PublicationScheduleRepository scheduleRepository;

    @Test
    void testFlywayMigrationCreatesScheduleTableWithExpectedColumns() {
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
            "SELECT COLUMN_NAME, DATA_TYPE " +
            "FROM INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_NAME = 'PUBLICATION_SCHEDULE'"
        );

        assertThat(columns).isNotEmpty();

        List<String> columnNames = columns.stream()
            .map(col -> col.get("COLUMN_NAME").toString().toLowerCase())
            .toList();

        assertThat(columnNames).contains("id", "platform", "topic", "status", "scheduled_at", "created_at", "updated_at");
    }

    @Test
    void testInsertAndRetrieveScheduleRecordValidatesTimestampAndEnums() {
        OffsetDateTime scheduledAt = OffsetDateTime.of(2026, 11, 15, 10, 0, 0, 0, ZoneOffset.UTC);
        PublicationSchedule schedule = new PublicationSchedule(
            Platform.TELEGRAM,
            "Autonomous Systems and Thinking Logic",
            PublicationStatus.SCHEDULED,
            scheduledAt
        );

        PublicationSchedule saved = scheduleRepository.save(schedule);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPlatform()).isEqualTo(Platform.TELEGRAM);
        assertThat(saved.getTopic()).isEqualTo("Autonomous Systems and Thinking Logic");
        assertThat(saved.getStatus()).isEqualTo(PublicationStatus.SCHEDULED);
        assertThat(saved.getScheduledAt()).isEqualTo(scheduledAt);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        PublicationSchedule retrieved = scheduleRepository.findById(saved.getId()).orElseThrow();
        assertThat(retrieved.getPlatform()).isEqualTo(Platform.TELEGRAM);
        assertThat(retrieved.getStatus()).isEqualTo(PublicationStatus.SCHEDULED);
        assertThat(retrieved.getTopic()).isEqualTo("Autonomous Systems and Thinking Logic");
    }

    @Test
    void testFindByPlatformAndStatusQueries() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        scheduleRepository.save(new PublicationSchedule(Platform.YOUTUBE, "Video 1", PublicationStatus.IN_PRODUCTION, now));
        scheduleRepository.save(new PublicationSchedule(Platform.ARTICLE, "Article 1", PublicationStatus.PUBLISHED, now));

        List<PublicationSchedule> youtubeItems = scheduleRepository.findByPlatform(Platform.YOUTUBE);
        assertThat(youtubeItems).hasSize(1);
        assertThat(youtubeItems.get(0).getTopic()).isEqualTo("Video 1");

        List<PublicationSchedule> publishedItems = scheduleRepository.findByStatus(PublicationStatus.PUBLISHED);
        assertThat(publishedItems).hasSize(1);
        assertThat(publishedItems.get(0).getTopic()).isEqualTo("Article 1");
    }
}
