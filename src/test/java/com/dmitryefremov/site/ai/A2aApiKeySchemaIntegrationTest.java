package com.dmitryefremov.site.ai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class A2aApiKeySchemaIntegrationTest {

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
    void testFlywayMigrationCreatedIndexesOnApiKeysTable() {
        List<String> indexNames = jdbcTemplate.queryForList(
                "SELECT UPPER(index_name) FROM information_schema.indexes WHERE UPPER(table_name) = 'API_KEYS'",
                String.class
        );

        assertThat(indexNames).contains("IDX_API_KEYS_KEY_HASH", "IDX_API_KEYS_OWNER_IDENTIFIER", "IDX_API_KEYS_IS_ACTIVE");
    }

    @Test
    void testInsertAndQueryApiKeyRecord() {
        String insertSql = "INSERT INTO api_keys (key_name, key_hash, key_prefix, scopes, owner_identifier, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                insertSql,
                "agent-alpha-key",
                "sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                "ak_live_",
                "read:data,write:orders",
                "external-agent-01",
                true
        );

        Map<String, Object> record = jdbcTemplate.queryForMap(
                "SELECT * FROM api_keys WHERE key_hash = ?",
                "sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        );

        assertThat(record.get("KEY_NAME")).isEqualTo("agent-alpha-key");
        assertThat(record.get("KEY_PREFIX")).isEqualTo("ak_live_");
        assertThat(record.get("SCOPES")).isEqualTo("read:data,write:orders");
        assertThat(record.get("OWNER_IDENTIFIER")).isEqualTo("external-agent-01");
        assertThat(record.get("IS_ACTIVE")).isEqualTo(Boolean.TRUE);
        assertThat(record.get("CREATED_AT")).isNotNull();
    }

    @Test
    void testUniqueConstraintOnKeyHash() {
        String insertSql = "INSERT INTO api_keys (key_name, key_hash, owner_identifier) VALUES (?, ?, ?)";

        jdbcTemplate.update(insertSql, "key1", "hash_duplicate", "owner1");

        assertThrows(Exception.class, () -> jdbcTemplate.update(insertSql, "key2", "hash_duplicate", "owner2"));
    }
}
