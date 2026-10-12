package com.dmitryefremov.site.ai.repository;

import com.dmitryefremov.site.ai.domain.AiDirectMessage;
import com.dmitryefremov.site.ai.domain.AiServiceOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AiRepositoryTest {

    @Autowired
    private AiServiceOrderRepository aiServiceOrderRepository;

    @Autowired
    private AiDirectMessageRepository aiDirectMessageRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFlywayMigrationCreatedAiTables() {
        Integer orderTableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'AI_SERVICE_ORDERS'",
                Integer.class
        );
        assertThat(orderTableCount).isGreaterThan(0);

        Integer messageTableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'AI_DIRECT_MESSAGES'",
                Integer.class
        );
        assertThat(messageTableCount).isGreaterThan(0);
    }

    @Test
    void testSaveAndRetrieveServiceOrderStoresUserDetailsTypeAndDatabaseGeneratedTimestamps() {
        AiServiceOrder order = new AiServiceOrder(
                "Alice AI User",
                "alice@example.com",
                "chatgpt-agent-001",
                "MVP",
                "Build MVP for logistics startup"
        );

        AiServiceOrder savedOrder = aiServiceOrderRepository.save(order);
        aiServiceOrderRepository.flush();

        assertThat(savedOrder.getId()).isNotNull();

        Optional<AiServiceOrder> retrieved = aiServiceOrderRepository.findById(savedOrder.getId());
        assertThat(retrieved).isPresent();

        AiServiceOrder fetched = retrieved.get();
        assertThat(fetched.getUserName()).isEqualTo("Alice AI User");
        assertThat(fetched.getUserEmail()).isEqualTo("alice@example.com");
        assertThat(fetched.getAgentIdentifier()).isEqualTo("chatgpt-agent-001");
        assertThat(fetched.getServiceType()).isEqualTo("MVP");
        assertThat(fetched.getRequirements()).isEqualTo("Build MVP for logistics startup");
        assertThat(fetched.getStatus()).isEqualTo("NEW");
        assertThat(fetched.getCreatedAt()).isNotNull();
        assertThat(fetched.getUpdatedAt()).isNotNull();
    }

    @Test
    void testSaveAndRetrieveDirectMessageStoresSenderDetailsContentAndClockSeededTimestamps() {
        Instant fixedInstant = Instant.parse("2026-10-12T12:00:00Z");
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);
        OffsetDateTime expectedTimestamp = OffsetDateTime.now(fixedClock);

        AiDirectMessage message = new AiDirectMessage(
                "Bob Assistant",
                "bob@example.com",
                "claude-agent-002",
                "Hello, I am reaching out regarding automated system integration.",
                fixedClock
        );

        AiDirectMessage savedMessage = aiDirectMessageRepository.save(message);
        aiDirectMessageRepository.flush();

        assertThat(savedMessage.getId()).isNotNull();

        Optional<AiDirectMessage> retrieved = aiDirectMessageRepository.findById(savedMessage.getId());
        assertThat(retrieved).isPresent();

        AiDirectMessage fetched = retrieved.get();
        assertThat(fetched.getSenderName()).isEqualTo("Bob Assistant");
        assertThat(fetched.getSenderEmail()).isEqualTo("bob@example.com");
        assertThat(fetched.getAgentIdentifier()).isEqualTo("claude-agent-002");
        assertThat(fetched.getContent()).isEqualTo("Hello, I am reaching out regarding automated system integration.");
        assertThat(fetched.getStatus()).isEqualTo("NEW");
        assertThat(fetched.getCreatedAt()).isNotNull();
        assertThat(fetched.getUpdatedAt()).isNotNull();
    }
}
