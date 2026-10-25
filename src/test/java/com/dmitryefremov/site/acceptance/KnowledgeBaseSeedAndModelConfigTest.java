package com.dmitryefremov.site.acceptance;

import com.dmitryefremov.site.canon.domain.CanonItem;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class KnowledgeBaseSeedAndModelConfigTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("siteCanonItemRepository")
    private CanonItemRepository canonItemRepository;

    @Test
    @DisplayName("Verify real-world author concepts and applied model configuration are seeded")
    void testAuthorConceptsAndModelConfigSeeded() {
        Optional<CanonItem> modelConfigOpt = canonItemRepository.findBySlug("prikladnaya-konfiguraciya-modeli-avtonomii");
        assertThat(modelConfigOpt).isPresent();

        CanonItem modelConfig = modelConfigOpt.get();
        assertThat(modelConfig.getType().name()).isEqualTo("MODEL_CONFIG");
        assertThat(modelConfig.getDomain().name()).isEqualTo("AUTONOMOUS_ENGINEERING");
        assertThat(modelConfig.getTitle()).contains("Прикладная конфигурация модели автономии");
        assertThat(modelConfig.getContent()).contains("dmitrii.eneik.com");
        assertThat(modelConfig.getContent()).contains("O(A)");

        Optional<CanonItem> memoryConceptOpt = canonItemRepository.findBySlug("pamyat-i-graf-znaniy-dmitrii-eneik");
        assertThat(memoryConceptOpt).isPresent();
        assertThat(memoryConceptOpt.get().getType().name()).isEqualTo("MANIFESTO");
        assertThat(memoryConceptOpt.get().getDomain().name()).isEqualTo("PHILOSOPHY_OF_MIND");

        Optional<CanonItem> a2aConceptOpt = canonItemRepository.findBySlug("a2a-protokoly-i-mashinochitaemyy-kontekst");
        assertThat(a2aConceptOpt).isPresent();
        assertThat(a2aConceptOpt.get().getType().name()).isEqualTo("GLOSSARY_TERM");
        assertThat(a2aConceptOpt.get().getDomain().name()).isEqualTo("AGENTIC_ECONOMY");
    }

    @Test
    @DisplayName("Verify cross-references link applied model config to author concepts without fake placeholders")
    void testCrossReferencesAndConceptMediaLinksExist() {
        Integer xrefCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM canon_cross_references WHERE description LIKE ?",
                Integer.class,
                "%Прикладная конфигурация модели%"
        );
        assertThat(xrefCount).isNotNull().isGreaterThanOrEqualTo(2);

        Integer mediaLinkCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM concept_media_links WHERE description LIKE ?",
                Integer.class,
                "%прикладной конфигурации модели%"
        );
        assertThat(mediaLinkCount).isNotNull().isGreaterThanOrEqualTo(1);
    }
}
