package com.dmitryefremov.site.agent.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.Timestamp;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AiOrdersAndMessagesSchemaIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testServiceOrderPersistence() {
        jdbcTemplate.update(
                "INSERT INTO ai_service_orders (user_details, service_type) VALUES (?, ?)",
                "user@example.com", "audit"
        );

        String userDetails = jdbcTemplate.queryForObject(
                "SELECT user_details FROM ai_service_orders WHERE service_type = ?",
                String.class,
                "audit"
        );

        assertThat(userDetails).isEqualTo("user@example.com");
    }

    @Test
    void testDirectMessagePersistence() {
        jdbcTemplate.update(
                "INSERT INTO ai_direct_messages (sender_details, content) VALUES (?, ?)",
                "sender@example.com", "Hello from AI"
        );

        String content = jdbcTemplate.queryForObject(
                "SELECT content FROM ai_direct_messages WHERE sender_details = ?",
                String.class,
                "sender@example.com"
        );

        assertThat(content).isEqualTo("Hello from AI");
    }
}
