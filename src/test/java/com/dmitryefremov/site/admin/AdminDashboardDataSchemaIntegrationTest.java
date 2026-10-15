package com.dmitryefremov.site.admin;

import com.dmitryefremov.site.admin.domain.AdminUser;
import com.dmitryefremov.site.admin.domain.ConsentRecord;
import com.dmitryefremov.site.admin.domain.TelemetryLog;
import com.dmitryefremov.site.admin.repository.AdminUserRepository;
import com.dmitryefremov.site.admin.repository.ConsentRecordRepository;
import com.dmitryefremov.site.admin.repository.TelemetryLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AdminDashboardDataSchemaIntegrationTest {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private TelemetryLogRepository telemetryLogRepository;

    @Autowired
    private ConsentRecordRepository consentRecordRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFlywayMigrationCreatedTables() {
        Integer adminUsersTableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'ADMIN_USERS'",
                Integer.class
        );
        Integer telemetryLogsTableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'TELEMETRY_LOGS'",
                Integer.class
        );
        Integer consentRecordsTableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'CONSENT_RECORDS'",
                Integer.class
        );

        assertThat(adminUsersTableCount).isGreaterThan(0);
        assertThat(telemetryLogsTableCount).isGreaterThan(0);
        assertThat(consentRecordsTableCount).isGreaterThan(0);
    }

    @Test
    void testAdminUserSecureCredentialStorage() {
        String hashedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOP";
        AdminUser adminUser = new AdminUser("dmitry_admin", hashedPassword, "dmitry@efremov.com", "ADMIN");

        AdminUser saved = adminUserRepository.save(adminUser);
        adminUserRepository.flush();

        assertThat(saved.getId()).isNotNull();

        Optional<AdminUser> retrievedOptional = adminUserRepository.findByUsername("dmitry_admin");
        assertThat(retrievedOptional).isPresent();

        AdminUser retrieved = retrievedOptional.get();
        assertThat(retrieved.getUsername()).isEqualTo("dmitry_admin");
        assertThat(retrieved.getPasswordHash()).isEqualTo(hashedPassword);
        assertThat(retrieved.getEmail()).isEqualTo("dmitry@efremov.com");
        assertThat(retrieved.getRole()).isEqualTo("ADMIN");
        assertThat(retrieved.getCreatedAt()).isNotNull();
        assertThat(retrieved.getUpdatedAt()).isNotNull();
    }

    @Test
    void testTelemetryLogStorageAndRetrieval() {
        TelemetryLog log = new TelemetryLog("DWELL_TIME_RECORDED", "{\"dwell_ms\": 4500}", "PUBLIC_PORTAL");

        TelemetryLog saved = telemetryLogRepository.save(log);
        telemetryLogRepository.flush();

        assertThat(saved.getId()).isNotNull();

        List<TelemetryLog> retrievedLogs = telemetryLogRepository.findByEventType("DWELL_TIME_RECORDED");
        assertThat(retrievedLogs).hasSize(1);
        assertThat(retrievedLogs.get(0).getPayload()).contains("dwell_ms");
        assertThat(retrievedLogs.get(0).getSource()).isEqualTo("PUBLIC_PORTAL");
        assertThat(retrievedLogs.get(0).getCreatedAt()).isNotNull();
    }

    @Test
    void testConsentRecordStorageAndRetrieval() {
        ConsentRecord consent = new ConsentRecord("user_12345", "GDPR_ANALYTICS", true, "192.168.1.1");

        ConsentRecord saved = consentRecordRepository.save(consent);
        consentRecordRepository.flush();

        assertThat(saved.getId()).isNotNull();

        List<ConsentRecord> retrievedConsents = consentRecordRepository.findByUserId("user_12345");
        assertThat(retrievedConsents).hasSize(1);
        assertThat(retrievedConsents.get(0).getConsentType()).isEqualTo("GDPR_ANALYTICS");
        assertThat(retrievedConsents.get(0).getGranted()).isTrue();
        assertThat(retrievedConsents.get(0).getIpAddress()).isEqualTo("192.168.1.1");
        assertThat(retrievedConsents.get(0).getCreatedAt()).isNotNull();
    }
}
