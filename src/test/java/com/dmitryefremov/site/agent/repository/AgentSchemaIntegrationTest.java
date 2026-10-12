package com.dmitryefremov.site.agent.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class AgentSchemaIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFlywayMigrationCreatedAgentApiTokensTable() {
        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'AGENT_API_TOKENS'",
                Integer.class
        );

        assertThat(tableCount).isGreaterThan(0);
    }

    @Test
    void testFlywayMigrationCreatedContentAuditLogsTable() {
        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'CONTENT_AUDIT_LOGS'",
                Integer.class
        );

        assertThat(tableCount).isGreaterThan(0);
    }

    @Test
    void testReferentialIntegrityBetweenAuditLogsAndApiTokens() {
        // Insert agent
        jdbcTemplate.update(
                "INSERT INTO agent_api_tokens (agent_name, token_hash) VALUES (?, ?)",
                "test-agent", "hash123"
        );

        Long agentId = jdbcTemplate.queryForObject(
                "SELECT id FROM agent_api_tokens WHERE agent_name = ?",
                Long.class,
                "test-agent"
        );

        assertThat(agentId).isNotNull();

        // Insert audit log successfully
        int rowsAffected = jdbcTemplate.update(
                "INSERT INTO content_audit_logs (agent_id, content_type, content_id, action) VALUES (?, ?, ?, ?)",
                agentId, "ARTICLE", 1L, "CREATE"
        );

        assertThat(rowsAffected).isEqualTo(1);

        // Try to insert audit log with invalid agent_id
        assertThatThrownBy(() -> {
            jdbcTemplate.update(
                    "INSERT INTO content_audit_logs (agent_id, content_type, content_id, action) VALUES (?, ?, ?, ?)",
                    999999L, "ARTICLE", 1L, "CREATE"
            );
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
