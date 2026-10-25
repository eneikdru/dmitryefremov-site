package com.dmitryefremov.site.contact;

import com.dmitryefremov.site.contact.domain.ContactMessage;
import com.dmitryefremov.site.contact.repository.ContactMessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ContactMessageSchemaIntegrationTest {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testContactMessagesTableAndIndexesCreated() {
        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'CONTACT_MESSAGES'",
                Integer.class
        );
        assertThat(tableCount).isGreaterThan(0);

        List<String> indexes = jdbcTemplate.queryForList(
                "SELECT UPPER(index_name) FROM information_schema.indexes WHERE UPPER(table_name) = 'CONTACT_MESSAGES'",
                String.class
        );

        assertThat(indexes).contains(
                "IDX_CONTACT_MESSAGES_CREATED_AT",
                "IDX_CONTACT_MESSAGES_SENDER_EMAIL",
                "IDX_CONTACT_MESSAGES_STATUS"
        );
    }

    @Test
    void testPersistenceAndQueryingBySenderEmailAndStatus() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-10-24T12:00:00Z"), ZoneOffset.UTC);

        ContactMessage msg1 = new ContactMessage(
                "Alice Smith",
                "alice@example.com",
                "Inquiry about services",
                "Hello, I would like to discuss a project.",
                fixedClock
        );

        ContactMessage msg2 = new ContactMessage(
                "Bob Jones",
                "bob@example.com",
                "Collaboration Request",
                "Hi, I am interested in partnership opportunities.",
                fixedClock
        );

        contactMessageRepository.saveAll(List.of(msg1, msg2));
        contactMessageRepository.flush();

        List<ContactMessage> foundByEmail = contactMessageRepository.findBySenderEmail("alice@example.com");
        assertThat(foundByEmail).hasSize(1);
        assertThat(foundByEmail.get(0).getSenderName()).isEqualTo("Alice Smith");
        assertThat(foundByEmail.get(0).getSubject()).isEqualTo("Inquiry about services");
        assertThat(foundByEmail.get(0).getContent()).isEqualTo("Hello, I would like to discuss a project.");
        assertThat(foundByEmail.get(0).getStatus()).isEqualTo("NEW");
        assertThat(foundByEmail.get(0).getCreatedAt()).isEqualTo(OffsetDateTime.now(fixedClock));

        List<ContactMessage> foundByStatus = contactMessageRepository.findByStatus("NEW");
        assertThat(foundByStatus).hasSize(2);
    }
}
