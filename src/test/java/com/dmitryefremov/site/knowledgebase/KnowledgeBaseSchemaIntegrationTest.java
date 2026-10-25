package com.dmitryefremov.site.knowledgebase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class KnowledgeBaseSchemaIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testTablesCreated() {
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT UPPER(table_name) FROM information_schema.tables WHERE UPPER(table_name) IN ('KNOWLEDGE_BASE_CONCEPTS', 'KNOWLEDGE_BASE_TAGS', 'APPLIED_MODEL_CONFIGURATIONS')",
                String.class
        );

        assertThat(tables).contains(
                "KNOWLEDGE_BASE_CONCEPTS",
                "KNOWLEDGE_BASE_TAGS",
                "APPLIED_MODEL_CONFIGURATIONS"
        );
    }

    @Test
    void testIndexesCreated() {
        List<String> indexes = jdbcTemplate.queryForList(
                "SELECT UPPER(index_name) FROM information_schema.indexes WHERE UPPER(table_name) IN ('KNOWLEDGE_BASE_CONCEPTS', 'KNOWLEDGE_BASE_TAGS', 'APPLIED_MODEL_CONFIGURATIONS')",
                String.class
        );

        assertThat(indexes).contains(
                "IDX_KB_CONCEPTS_SLUG",
                "IDX_KB_CONCEPTS_CATEGORY",
                "IDX_KB_TAGS_TAG_NAME",
                "IDX_KB_TAGS_CONCEPT_ID",
                "IDX_APPLIED_MODEL_CONCEPT_ID",
                "IDX_APPLIED_MODEL_NAME"
        );
    }

    @Test
    void testInsertAndQueryKnowledgeBaseData() {
        // Insert concept
        jdbcTemplate.update(
                "INSERT INTO knowledge_base_concepts (slug, title, category, description, content) VALUES (?, ?, ?, ?, ?)",
                "modal-logic-invariant", "Modal Logic Invariant", "PHILOSOPHY", "Description of modal logic invariant", "Content details"
        );

        Long conceptId = jdbcTemplate.queryForObject(
                "SELECT id FROM knowledge_base_concepts WHERE slug = ?",
                Long.class,
                "modal-logic-invariant"
        );
        assertThat(conceptId).isNotNull();

        // Insert tag
        jdbcTemplate.update(
                "INSERT INTO knowledge_base_tags (concept_id, tag_name) VALUES (?, ?)",
                conceptId, "modal-logic"
        );

        String tag = jdbcTemplate.queryForObject(
                "SELECT tag_name FROM knowledge_base_tags WHERE concept_id = ?",
                String.class,
                conceptId
        );
        assertThat(tag).isEqualTo("modal-logic");

        // Insert applied model configuration
        jdbcTemplate.update(
                "INSERT INTO applied_model_configurations (concept_id, model_name, version, configuration_json) VALUES (?, ?, ?, ?)",
                conceptId, "BarcanFormulaModel", "1.0.0", "{\"parameters\": {\"modal_strictness\": 0.95}}"
        );

        String modelName = jdbcTemplate.queryForObject(
                "SELECT model_name FROM applied_model_configurations WHERE concept_id = ?",
                String.class,
                conceptId
        );
        assertThat(modelName).isEqualTo("BarcanFormulaModel");
    }
}
