package com.dmitryefremov.site.agent.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class A2AApiKeysSchemaIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFlywayMigrationCreatedApiKeysTable() {
        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'API_KEYS'",
                Integer.class
        );

        assertThat(tableCount).isGreaterThan(0);
    }

    @Test
    void testInsertAndRetrieveApiKeyWithSecureHash() {
        String keyIdentity = "agent-a2a-test-identity";
        String secureHash = "$2a$10$e8R6S8k.K/y1dM2K1N8u3eOaK0gO6aH6Z/0v.5M2lO5aG/1R2S3T4";

        int rows = jdbcTemplate.update(
                "INSERT INTO api_keys (key_identity, key_hash, scopes, status, rate_limit_rpm) VALUES (?, ?, ?, ?, ?)",
                keyIdentity, secureHash, "SCOPE_CONTENT_READ,SCOPE_CONTENT_WRITE", "ACTIVE", 120
        );

        assertThat(rows).isEqualTo(1);

        Map<String, Object> result = jdbcTemplate.queryForMap(
                "SELECT key_identity, key_hash, scopes, status, rate_limit_rpm FROM api_keys WHERE key_identity = ?",
                keyIdentity
        );

        assertThat(result.get("KEY_IDENTITY")).isEqualTo(keyIdentity);
        assertThat(result.get("KEY_HASH")).isEqualTo(secureHash);
        assertThat(result.get("SCOPES")).isEqualTo("SCOPE_CONTENT_READ,SCOPE_CONTENT_WRITE");
        assertThat(result.get("STATUS")).isEqualTo("ACTIVE");
        assertThat(((Number) result.get("RATE_LIMIT_RPM")).intValue()).isEqualTo(120);
    }

    @Test
    void testUniqueKeyIdentityConstraint() {
        String keyIdentity = "agent-duplicate-test";
        String secureHash = "hash_value_123";

        jdbcTemplate.update(
                "INSERT INTO api_keys (key_identity, key_hash) VALUES (?, ?)",
                keyIdentity, secureHash
        );

        assertThatThrownBy(() -> jdbcTemplate.update(
                "INSERT INTO api_keys (key_identity, key_hash) VALUES (?, ?)",
                keyIdentity, "different_hash"
        )).isInstanceOf(DataIntegrityViolationException.class);
    }
}
