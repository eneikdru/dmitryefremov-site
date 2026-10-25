package com.dmitryefremov.site.contract;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KnowledgeBaseApiContractTest {

    private static final String CONTRACT_FILE_PATH = "docs/contracts/knowledge-base-api.yaml";

    @Test
    @DisplayName("Knowledge Base OpenAPI Contract file exists and is readable")
    void testContractFileExists() {
        File file = new File(CONTRACT_FILE_PATH);
        assertTrue(file.exists(), "API contract file " + CONTRACT_FILE_PATH + " must exist");
        assertTrue(file.isFile(), "API contract path must point to a file");
        assertTrue(file.canRead(), "API contract file must be readable");
    }

    @Test
    @DisplayName("Knowledge Base OpenAPI Contract parses as valid OpenAPI 3.0.3 YAML")
    @SuppressWarnings("unchecked")
    void testContractStructureAndPaths() throws Exception {
        File file = new File(CONTRACT_FILE_PATH);
        Yaml yaml = new Yaml();
        Map<String, Object> root;
        try (InputStream is = new FileInputStream(file)) {
            root = yaml.load(is);
        }

        assertNotNull(root, "Parsed OpenAPI contract YAML must not be null");

        // Validate OpenAPI version and Info
        assertEquals("3.0.3", root.get("openapi"), "OpenAPI version must be 3.0.3");
        Map<String, Object> info = (Map<String, Object>) root.get("info");
        assertNotNull(info, "Info block must exist");
        assertNotNull(info.get("title"), "Info title must exist");
        assertNotNull(info.get("version"), "Info version must exist");

        // Validate Paths
        Map<String, Object> paths = (Map<String, Object>) root.get("paths");
        assertNotNull(paths, "Paths section must exist");

        assertTrue(paths.containsKey("/api/v1/knowledge-base/concepts"), "Contract must define /api/v1/knowledge-base/concepts");
        assertTrue(paths.containsKey("/api/v1/knowledge-base/concepts/{slug}"), "Contract must define /api/v1/knowledge-base/concepts/{slug}");
        assertTrue(paths.containsKey("/api/v1/knowledge-base/categories"), "Contract must define /api/v1/knowledge-base/categories");
        assertTrue(paths.containsKey("/api/v1/knowledge-base/tags"), "Contract must define /api/v1/knowledge-base/tags");
        assertTrue(paths.containsKey("/api/v1/knowledge-base/concepts/{slug}/models"), "Contract must define /api/v1/knowledge-base/concepts/{slug}/models");

        // Validate query parameters on GET /api/v1/knowledge-base/concepts
        Map<String, Object> conceptsEndpoint = (Map<String, Object>) paths.get("/api/v1/knowledge-base/concepts");
        assertNotNull(conceptsEndpoint.get("get"), "GET method must be defined for /api/v1/knowledge-base/concepts");
        Map<String, Object> conceptsGet = (Map<String, Object>) conceptsEndpoint.get("get");
        List<Map<String, Object>> params = (List<Map<String, Object>>) conceptsGet.get("parameters");
        assertNotNull(params, "Parameters must be defined for concepts endpoint");

        List<String> paramNames = params.stream().map(p -> (String) p.get("name")).toList();
        assertTrue(paramNames.contains("category"), "Must support 'category' filter parameter");
        assertTrue(paramNames.contains("tag"), "Must support 'tag' filter parameter");
        assertTrue(paramNames.contains("q"), "Must support 'q' search parameter");

        // Validate Components / Schemas
        Map<String, Object> components = (Map<String, Object>) root.get("components");
        assertNotNull(components, "Components section must exist");
        Map<String, Object> schemas = (Map<String, Object>) components.get("schemas");
        assertNotNull(schemas, "Schemas section must exist");

        assertTrue(schemas.containsKey("ConceptItem"), "Schema ConceptItem must exist");
        assertTrue(schemas.containsKey("ConceptDetail"), "Schema ConceptDetail must exist");
        assertTrue(schemas.containsKey("ConceptCreateRequest"), "Schema ConceptCreateRequest must exist");
        assertTrue(schemas.containsKey("ConceptPage"), "Schema ConceptPage must exist");
        assertTrue(schemas.containsKey("AppliedModelConfiguration"), "Schema AppliedModelConfiguration must exist");
        assertTrue(schemas.containsKey("AppliedModelCreateRequest"), "Schema AppliedModelCreateRequest must exist");
        assertTrue(schemas.containsKey("CategoryList"), "Schema CategoryList must exist");
        assertTrue(schemas.containsKey("TagList"), "Schema TagList must exist");
        assertTrue(schemas.containsKey("ErrorResponse"), "Schema ErrorResponse must exist");
    }
}
