package com.dmitryefremov.site.schedule.repository;

import com.dmitryefremov.site.schedule.domain.PublicationPlatform;
import com.dmitryefremov.site.schedule.domain.PublicationSchedule;
import com.dmitryefremov.site.schedule.domain.PublicationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class PublicationScheduleRepositoryTest {

    @Autowired
    private PublicationScheduleRepository publicationScheduleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFlywayMigrationCreatedTable() {
        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'PUBLICATION_SCHEDULES'",
                Integer.class
        );
        assertThat(tableCount).isGreaterThan(0);
    }

    @Test
    void testInsertAndRetrieveScheduleItem() {
        OffsetDateTime scheduledAt = OffsetDateTime.of(2026, 11, 1, 10, 0, 0, 0, ZoneOffset.UTC);

        PublicationSchedule schedule = new PublicationSchedule(
                PublicationPlatform.TELEGRAM,
                "Деонтическая логика в проектировании систем",
                PublicationStatus.SCHEDULED,
                scheduledAt
        );

        PublicationSchedule saved = publicationScheduleRepository.save(schedule);
        publicationScheduleRepository.flush();

        assertThat(saved.getId()).isNotNull();

        Optional<PublicationSchedule> fetched = publicationScheduleRepository.findById(saved.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getPlatform()).isEqualTo(PublicationPlatform.TELEGRAM);
        assertThat(fetched.get().getTopic()).isEqualTo("Деонтическая логика в проектировании систем");
        assertThat(fetched.get().getStatus()).isEqualTo(PublicationStatus.SCHEDULED);
        assertThat(fetched.get().getScheduledAt()).isEqualTo(scheduledAt);
        assertThat(fetched.get().getCreatedAt()).isNotNull();
        assertThat(fetched.get().getUpdatedAt()).isNotNull();
    }

    @Test
    void testValidationOfEnumsAndTimestamps() {
        OffsetDateTime scheduledAt = OffsetDateTime.of(2026, 11, 15, 14, 30, 0, 0, ZoneOffset.UTC);

        assertThatThrownBy(() -> new PublicationSchedule(null, "Topic", PublicationStatus.SCHEDULED, scheduledAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Platform must not be null");

        assertThatThrownBy(() -> new PublicationSchedule(PublicationPlatform.YOUTUBE, "", PublicationStatus.SCHEDULED, scheduledAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Topic must not be null or blank");

        assertThatThrownBy(() -> new PublicationSchedule(PublicationPlatform.YOUTUBE, "Topic", null, scheduledAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Status must not be null");

        assertThatThrownBy(() -> new PublicationSchedule(PublicationPlatform.YOUTUBE, "Topic", PublicationStatus.SCHEDULED, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Scheduled timestamp must not be null");

        assertThat(PublicationPlatform.parse("telegram")).isEqualTo(PublicationPlatform.TELEGRAM);
        assertThat(PublicationStatus.parse("in_production")).isEqualTo(PublicationStatus.IN_PRODUCTION);

        assertThatThrownBy(() -> PublicationPlatform.parse("INVALID_PLATFORM"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown publication platform");

        assertThatThrownBy(() -> PublicationStatus.parse("UNKNOWN_STATUS"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown publication status");
    }

    @Test
    void testFilterByPlatformAndStatus() {
        OffsetDateTime t1 = OffsetDateTime.of(2026, 11, 1, 10, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime t2 = OffsetDateTime.of(2026, 11, 2, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime t3 = OffsetDateTime.of(2026, 11, 3, 15, 0, 0, 0, ZoneOffset.UTC);

        publicationScheduleRepository.save(new PublicationSchedule(PublicationPlatform.TELEGRAM, "Telegram Topic 1", PublicationStatus.SCHEDULED, t1));
        publicationScheduleRepository.save(new PublicationSchedule(PublicationPlatform.YOUTUBE, "YouTube Topic 1", PublicationStatus.IN_PRODUCTION, t2));
        publicationScheduleRepository.save(new PublicationSchedule(PublicationPlatform.TELEGRAM, "Telegram Topic 2", PublicationStatus.PUBLISHED, t3));
        publicationScheduleRepository.flush();

        List<PublicationSchedule> telegramItems = publicationScheduleRepository.findByPlatform(PublicationPlatform.TELEGRAM);
        assertThat(telegramItems).hasSize(2);

        List<PublicationSchedule> inProductionItems = publicationScheduleRepository.findByStatus(PublicationStatus.IN_PRODUCTION);
        assertThat(inProductionItems).hasSize(1);
        assertThat(inProductionItems.get(0).getTopic()).isEqualTo("YouTube Topic 1");

        List<PublicationSchedule> filtered = publicationScheduleRepository.findByPlatformAndStatus(PublicationPlatform.TELEGRAM, PublicationStatus.SCHEDULED);
        assertThat(filtered).hasSize(1);
        assertThat(filtered.get(0).getTopic()).isEqualTo("Telegram Topic 1");
    }
}
