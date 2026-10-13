package com.dmitryefremov.site.agent.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class ApiKeySchemaIntegrationTest {

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
    void testFlywayMigrationCreatedIndexes() {
        List<String> indexNames = jdbcTemplate.queryForList(
                "SELECT UPPER(index_name) FROM information_schema.indexes WHERE UPPER(table_name) = 'API_KEYS'",
                String.class
        );

        assertThat(indexNames).contains("IDX_API_KEYS_IDENTITY", "IDX_API_KEYS_STATUS");
    }

    @Test
    void testInsertAndRetrieveApiKeyWithSecureHash() {
        int rowsInserted = jdbcTemplate.update(
                "INSERT INTO api_keys (key_identity, key_hash, scopes, status) VALUES (?, ?, ?, ?)",
                "external-agent-001", "$2a$10$abcdefghijklmnopqrstuvwxyz123456789012345678901234567", "SCOPE_CONTENT_READ", "ACTIVE"
        );

        assertThat(rowsInserted).isEqualTo(1);

        String fetchedIdentity = jdbcTemplate.queryForObject(
                "SELECT key_identity FROM api_keys WHERE key_identity = ?",
                String.class,
                "external-agent-001"
        );

        assertThat(fetchedIdentity).isEqualTo("external-agent-001");
    }

    @Test
    void testUniqueKeyIdentityConstraint() {
        jdbcTemplate.update(
                "INSERT INTO api_keys (key_identity, key_hash, scopes, status) VALUES (?, ?, ?, ?)",
                "agent-1", "hash_duplicate_test_1", "SCOPE_CONTENT_READ", "ACTIVE"
        );

        assertThatThrownBy(() -> {
            jdbcTemplate.update(
                    "INSERT INTO api_keys (key_identity, key_hash, scopes, status) VALUES (?, ?, ?, ?)",
                    "agent-1", "hash_duplicate_test_2", "SCOPE_CONTENT_READ", "ACTIVE"
            );
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
