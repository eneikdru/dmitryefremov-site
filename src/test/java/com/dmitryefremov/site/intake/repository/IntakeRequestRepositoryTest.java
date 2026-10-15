package com.dmitryefremov.site.intake.repository;

import com.dmitryefremov.site.intake.domain.IntakeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class IntakeRequestRepositoryTest {

    @Autowired
    private IntakeRequestRepository intakeRequestRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFlywayMigrationCreatedIntakeRequestsTable() {
        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'INTAKE_REQUESTS'",
                Integer.class
        );

        assertThat(tableCount).isGreaterThan(0);
    }

    @Test
    void testSaveAndRetrieveIntakeRequestEncryptedAtRestAndDecryptedByEntity() {
        Instant fixedInstant = Instant.parse("2026-10-15T12:00:00Z");
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

        String secretAuditPayload = "{\"auditTarget\":\"/admin/telemetry\",\"confidentialScope\":\"internal_pipeline_v1\"}";

        IntakeRequest request = new IntakeRequest(
                "Confidential Client",
                "audit-client@partner.org",
                "Partner Org",
                secretAuditPayload,
                fixedClock
        );

        IntakeRequest savedRequest = intakeRequestRepository.save(request);
        intakeRequestRepository.flush();

        assertThat(savedRequest.getId()).isNotNull();

        // 1. Verify encrypted at rest via raw JDBC SQL query
        String rawDbPayload = jdbcTemplate.queryForObject(
                "SELECT audit_payload FROM intake_requests WHERE id = ?",
                String.class,
                savedRequest.getId()
        );

        assertThat(rawDbPayload).isNotNull();
        assertThat(rawDbPayload).isNotEqualTo(secretAuditPayload);
        assertThat(rawDbPayload).doesNotContain("/admin/telemetry");
        assertThat(rawDbPayload).doesNotContain("internal_pipeline_v1");

        // 2. Verify JPA entity decrypts transparently on retrieval
        Optional<IntakeRequest> retrieved = intakeRequestRepository.findById(savedRequest.getId());
        assertThat(retrieved).isPresent();

        IntakeRequest fetched = retrieved.get();
        assertThat(fetched.getClientName()).isEqualTo("Confidential Client");
        assertThat(fetched.getClientEmail()).isEqualTo("audit-client@partner.org");
        assertThat(fetched.getCompanyName()).isEqualTo("Partner Org");
        assertThat(fetched.getAuditPayload()).isEqualTo(secretAuditPayload);
        assertThat(fetched.getStatus()).isEqualTo("NEW");
        assertThat(fetched.getCreatedAt()).isNotNull();
        assertThat(fetched.getUpdatedAt()).isNotNull();
    }

    @Test
    void testFindByClientEmailAndDeletion() {
        Instant fixedInstant = Instant.parse("2026-10-15T12:30:00Z");
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

        IntakeRequest request = new IntakeRequest(
                "Test Client",
                "user@example.com",
                "Example Inc",
                "{\"secret\":\"data\"}",
                fixedClock
        );

        intakeRequestRepository.save(request);
        intakeRequestRepository.flush();

        List<IntakeRequest> found = intakeRequestRepository.findByClientEmail("user@example.com");
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getClientName()).isEqualTo("Test Client");

        long deleted = intakeRequestRepository.deleteByClientEmail("user@example.com");
        assertThat(deleted).isEqualTo(1L);

        List<IntakeRequest> afterDeletion = intakeRequestRepository.findByClientEmail("user@example.com");
        assertThat(afterDeletion).isEmpty();
    }
}
