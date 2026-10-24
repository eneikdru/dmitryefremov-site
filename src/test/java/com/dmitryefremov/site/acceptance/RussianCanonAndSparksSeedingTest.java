package com.dmitryefremov.site.acceptance;

import com.dmitryefremov.site.canon.domain.CanonDomain;
import com.dmitryefremov.site.canon.domain.CanonItem;
import com.dmitryefremov.site.canon.domain.CanonType;
import com.dmitryefremov.site.canon.repository.CanonCrossReferenceRepository;
import com.dmitryefremov.site.canon.repository.CanonItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RussianCanonAndSparksSeedingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CanonItemRepository canonItemRepository;

    @Autowired
    private CanonCrossReferenceRepository canonCrossReferenceRepository;

    @Test
    @DisplayName("Verify authentic Russian Canon items are seeded and accessible via API")
    void testRussianCanonItemsSeeded() throws Exception {
        Optional<CanonItem> deontika = canonItemRepository.findBySlug("deontika-avtonomnyh-sistem");
        assertThat(deontika).isPresent();
        assertThat(deontika.get().getTitle()).contains("Деонтика");
        assertThat(deontika.get().getDomain()).isEqualTo(CanonDomain.AUTONOMOUS_ENGINEERING);
        assertThat(deontika.get().getType()).isEqualTo(CanonType.MANIFESTO);

        Optional<CanonItem> extendedMind = canonItemRepository.findBySlug("rasshirennyy-razum-i-vneshnyaya-pamyat");
        assertThat(extendedMind).isPresent();
        assertThat(extendedMind.get().getDomain()).isEqualTo(CanonDomain.PHILOSOPHY_OF_MIND);

        mockMvc.perform(get("/api/v1/canon/deontika-avtonomnyh-sistem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("deontika-avtonomnyh-sistem"))
                .andExpect(jsonPath("$.title").value(containsString("Деонтика")));
    }

    @Test
    @DisplayName("Verify Russian Canon cross-references are established in graph")
    void testRussianCanonCrossReferencesSeeded() {
        Optional<CanonItem> deontika = canonItemRepository.findBySlug("deontika-avtonomnyh-sistem");
        assertThat(deontika).isPresent();

        var refs = canonCrossReferenceRepository.findBySourceId(deontika.get().getId());
        assertThat(refs).hasSize(1);
        assertThat(refs.get(0).getTarget().getSlug()).isEqualTo("rasshirennyy-razum-i-vneshnyaya-pamyat");
        assertThat(refs.get(0).getRelationshipType().name()).isEqualTo("EXTENDS");
    }

    @Test
    @DisplayName("Verify authentic Russian Sparks are seeded in database")
    void testRussianSparksSeeded() {
        Integer sparkCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sparks WHERE author = '@efremov_mvp'",
                Integer.class
        );
        assertThat(sparkCount).isGreaterThanOrEqualTo(3);

        String firstSparkContent = jdbcTemplate.queryForObject(
                "SELECT content FROM sparks WHERE telegram_message_id = 9001",
                String.class
        );
        assertThat(firstSparkContent).contains("Сайт как внешняя рабочая память");
    }
}
