package com.dmitryefremov.site.intake.repository;

import com.dmitryefremov.site.intake.domain.ClientIntake;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ClientIntakeRepositoryTest {

    @Autowired
    private ClientIntakeRepository clientIntakeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFlywayMigrationCreatedClientIntakesTable() {
        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'CLIENT_INTAKES'",
                Integer.class
        );

        assertThat(tableCount).isGreaterThan(0);
    }

    @Test
    void testSaveAndRetrieveClientIntakeEncryptedAtRestAndDecryptedByEntity() {
        Instant fixedInstant = Instant.parse("2026-10-12T20:00:00Z");
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

        String secretPayload = "{\"idea\":\"Build AI Turnkey Platform\",\"wishlist\":[\"Secure intake\",\"Encryption at rest\"]}";

        ClientIntake intake = new ClientIntake(
                "Acme Corp Client",
                "client@acme.com",
                "Acme Corp",
                secretPayload,
                fixedClock
        );

        ClientIntake savedIntake = clientIntakeRepository.save(intake);
        clientIntakeRepository.flush();

        assertThat(savedIntake.getId()).isNotNull();

        // 1. Verify encrypted at rest via raw JDBC SQL query
        String rawDbPayload = jdbcTemplate.queryForObject(
                "SELECT wishlist_payload FROM client_intakes WHERE id = ?",
                String.class,
                savedIntake.getId()
        );

        assertThat(rawDbPayload).isNotNull();
        assertThat(rawDbPayload).isNotEqualTo(secretPayload);
        assertThat(rawDbPayload).doesNotContain("Build AI Turnkey Platform");
        assertThat(rawDbPayload).doesNotContain("Secure intake");

        // 2. Verify JPA entity decrypts transparently on retrieval
        Optional<ClientIntake> retrieved = clientIntakeRepository.findById(savedIntake.getId());
        assertThat(retrieved).isPresent();

        ClientIntake fetched = retrieved.get();
        assertThat(fetched.getClientName()).isEqualTo("Acme Corp Client");
        assertThat(fetched.getClientEmail()).isEqualTo("client@acme.com");
        assertThat(fetched.getCompanyName()).isEqualTo("Acme Corp");
        assertThat(fetched.getWishlistPayload()).isEqualTo(secretPayload);
        assertThat(fetched.getStatus()).isEqualTo("NEW");
        assertThat(fetched.getCreatedAt()).isNotNull();
        assertThat(fetched.getUpdatedAt()).isNotNull();
    }
}
