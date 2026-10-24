package com.dmitryefremov.site.canon;

import com.dmitryefremov.site.canon.domain.*;
import com.dmitryefremov.site.canon.repository.CanonCrossReferenceRepository;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
import com.dmitryefremov.site.canon.repository.CanonMediaLinkRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CanonSchemaIntegrationTest {

    @Autowired
    private CanonItemRepository canonItemRepository;

    @Autowired
    private CanonCrossReferenceRepository canonCrossReferenceRepository;

    @Autowired
    private CanonMediaLinkRepository canonMediaLinkRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFlywayMigrationCreatedCanonTables() {
        Integer itemsTable = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'CANON_ITEMS'",
                Integer.class
        );
        Integer xrefTable = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'CANON_CROSS_REFERENCES'",
                Integer.class
        );
        Integer mediaLinksTable = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE UPPER(table_name) = 'CANON_MEDIA_LINKS'",
                Integer.class
        );

        assertThat(itemsTable).isGreaterThan(0);
        assertThat(xrefTable).isGreaterThan(0);
        assertThat(mediaLinksTable).isGreaterThan(0);
    }

    @Test
    void testSeededCanonDataIsPresent() {
        Optional<CanonItem> manifesto = canonItemRepository.findBySlug("architecture-of-autonomous-systems");
        assertThat(manifesto).isPresent();
        assertThat(manifesto.get().getDomain()).isEqualTo(CanonDomain.AUTONOMOUS_ENGINEERING);
        assertThat(manifesto.get().getType()).isEqualTo(CanonType.MANIFESTO);

        List<CanonCrossReference> refs = canonCrossReferenceRepository.findBySourceId(manifesto.get().getId());
        assertThat(refs).isNotEmpty();
        assertThat(refs.get(0).getRelationshipType()).isEqualTo(CanonRelationshipType.REFERENCES);
        assertThat(refs.get(0).getTarget().getSlug()).isEqualTo("extended-mind-infrastructure");
    }

    @Test
    void testSaveAndRetrieveCanonItemWithDomainAndCrossReference() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 12, 12, 0, 0, 0, ZoneOffset.UTC);

        CanonItem item1 = new CanonItem(
                "agentic-economy-manifesto",
                "Agentic Economy Core Manifesto",
                CanonType.MANIFESTO,
                CanonDomain.AGENTIC_ECONOMY,
                "Summary of agentic economy",
                "# Agentic Economy Manifesto\n\nContent...",
                publishedAt
        );

        CanonItem item2 = new CanonItem(
                "quantized-agent-transactions",
                "Quantized Agent Transactions",
                CanonType.GLOSSARY_TERM,
                CanonDomain.AGENTIC_ECONOMY,
                "Glossary entry for transactions",
                "# Quantized Agent Transactions\n\nContent...",
                publishedAt
        );

        canonItemRepository.save(item1);
        canonItemRepository.save(item2);
        canonItemRepository.flush();

        CanonCrossReference xref = new CanonCrossReference(item1, item2, CanonRelationshipType.REFERENCES, "Defines transaction primitives");
        item1.addOutgoingReference(xref);

        canonCrossReferenceRepository.save(xref);
        canonItemRepository.flush();

        Optional<CanonItem> fetchedOpt = canonItemRepository.findBySlug("agentic-economy-manifesto");
        assertThat(fetchedOpt).isPresent();
        CanonItem fetched = fetchedOpt.get();
        assertThat(fetched.getDomain()).isEqualTo(CanonDomain.AGENTIC_ECONOMY);
        assertThat(fetched.getOutgoingReferences()).hasSize(1);
        assertThat(fetched.getOutgoingReferences().get(0).getTarget().getSlug()).isEqualTo("quantized-agent-transactions");
    }

    @Test
    void testSaveAndRetrieveCanonMediaLink() {
        OffsetDateTime publishedAt = OffsetDateTime.of(2026, 10, 12, 12, 0, 0, 0, ZoneOffset.UTC);

        CanonItem item = new CanonItem(
                "extended-mind-infrastructure-test",
                "Extended Mind Infrastructure Test",
                CanonType.GLOSSARY_TERM,
                CanonDomain.AUTONOMOUS_ENGINEERING,
                "Summary",
                "Content",
                publishedAt
        );

        canonItemRepository.save(item);
        canonItemRepository.flush();

        CanonMediaLink mediaLink = new CanonMediaLink(item, MediaType.PODCAST, 42L, "DISCUSSED_IN", "Episode 42 deep dive");
        item.addMediaLink(mediaLink);

        canonMediaLinkRepository.save(mediaLink);
        canonItemRepository.flush();

        Optional<CanonItem> fetchedOpt = canonItemRepository.findBySlug("extended-mind-infrastructure-test");
        assertThat(fetchedOpt).isPresent();
        CanonItem fetched = fetchedOpt.get();
        assertThat(fetched.getMediaLinks()).hasSize(1);
        assertThat(fetched.getMediaLinks().get(0).getMediaType()).isEqualTo(MediaType.PODCAST);
        assertThat(fetched.getMediaLinks().get(0).getMediaId()).isEqualTo(42L);
        assertThat(fetched.getMediaLinks().get(0).getRelationshipType()).isEqualTo("DISCUSSED_IN");

        List<CanonMediaLink> foundLinks = canonMediaLinkRepository.findByMediaTypeAndMediaId(MediaType.PODCAST, 42L);
        assertThat(foundLinks).hasSize(1);
        assertThat(foundLinks.get(0).getCanonItem().getSlug()).isEqualTo("extended-mind-infrastructure-test");
    }

    @Test
    void testFindByDomainAndType() {
        Page<CanonItem> page = canonItemRepository.findByDomainAndTypeOrderByPublishedAtDesc(
                CanonDomain.AUTONOMOUS_ENGINEERING, CanonType.MANIFESTO, PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getDomain()).isEqualTo(CanonDomain.AUTONOMOUS_ENGINEERING);
        assertThat(page.getContent().get(0).getType()).isEqualTo(CanonType.MANIFESTO);
    }
}
