package com.dmitryefremov.site.schedule;

import com.dmitryefremov.site.schedule.domain.PublicationSchedule;
import com.dmitryefremov.site.schedule.repository.PublicationScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PublicationScheduleSchemaIntegrationTest {

    @Autowired
    private PublicationScheduleRepository publicationScheduleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        publicationScheduleRepository.deleteAll();
    }

    @Test
    void testValidScheduleInsertAndRetrieval() {
        OffsetDateTime scheduledAt = OffsetDateTime.of(2026, 11, 1, 15, 0, 0, 0, ZoneOffset.UTC);
        PublicationSchedule schedule = new PublicationSchedule(
                null,
                "Test Topic Release",
                "TELEGRAM",
                "SCHEDULED",
                "Topic summary details",
                scheduledAt,
                null
        );

        PublicationSchedule saved = publicationScheduleRepository.saveAndFlush(schedule);

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertEquals("Test Topic Release", saved.getTitle());
        assertEquals("TELEGRAM", saved.getPlatform());
        assertEquals("SCHEDULED", saved.getStatus());
        assertEquals("Topic summary details", saved.getTopicSummary());
        assertEquals(scheduledAt, saved.getScheduledAt());
    }

    @Test
    void testInvalidPlatformRejectedByCheckConstraint() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            jdbcTemplate.execute(
                    "INSERT INTO publication_schedules (title, platform, status, topic_summary, scheduled_at, created_at) " +
                    "VALUES ('Invalid Platform Test', 'INVALID_PLATFORM', 'SCHEDULED', 'Summary', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
            );
        });
    }

    @Test
    void testInvalidStatusRejectedByCheckConstraint() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            jdbcTemplate.execute(
                    "INSERT INTO publication_schedules (title, platform, status, topic_summary, scheduled_at, created_at) " +
                    "VALUES ('Invalid Status Test', 'YOUTUBE', 'INVALID_STATUS', 'Summary', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
            );
        });
    }

    @Test
    void testValidCaseInsensitiveEnumCheckConstraint() {
        assertDoesNotThrow(() -> {
            jdbcTemplate.execute(
                    "INSERT INTO publication_schedules (title, platform, status, topic_summary, scheduled_at, created_at) " +
                    "VALUES ('Mixed Case Test', 'telegram', 'scheduled', 'Summary', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
            );
        });
    }
}
