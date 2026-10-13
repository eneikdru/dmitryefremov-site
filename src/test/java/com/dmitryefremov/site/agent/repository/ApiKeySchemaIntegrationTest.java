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

        assertThat(indexNames).contains("IDX_API_KEYS_CLIENT_ID", "IDX_API_KEYS_KEY_PREFIX");
    }

    @Test
    void testInsertAndRetrieveApiKeyWithSecureHash() {
        int rowsInserted = jdbcTemplate.update(
                "INSERT INTO api_keys (client_id, key_prefix, key_hash, scopes) VALUES (?, ?, ?, ?)",
                "external-agent-001", "a2a_live_", "$2a$10$abcdefghijklmnopqrstuvwxyz123456789012345678901234567", "a2a:read,a2a:write"
        );

        assertThat(rowsInserted).isEqualTo(1);

        String fetchedPrefix = jdbcTemplate.queryForObject(
                "SELECT key_prefix FROM api_keys WHERE client_id = ?",
                String.class,
                "external-agent-001"
        );

        assertThat(fetchedPrefix).isEqualTo("a2a_live_");
    }

    @Test
    void testUniqueKeyHashConstraint() {
        jdbcTemplate.update(
                "INSERT INTO api_keys (client_id, key_prefix, key_hash) VALUES (?, ?, ?)",
                "agent-1", "a2a_live_", "hash_duplicate_test"
        );

        assertThatThrownBy(() -> {
            jdbcTemplate.update(
                    "INSERT INTO api_keys (client_id, key_prefix, key_hash) VALUES (?, ?, ?)",
                    "agent-2", "a2a_live_", "hash_duplicate_test"
            );
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
