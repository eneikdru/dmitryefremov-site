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

class KnowledgeGraphSearchContractTest {

    private static final String CONTRACT_FILE_PATH = "docs/contracts/knowledge-graph-search-api.yaml";

    @Test
    @DisplayName("Knowledge Graph and Search OpenAPI Contract file exists and is readable")
    void testContractFileExists() {
        File file = new File(CONTRACT_FILE_PATH);
        assertTrue(file.exists(), "API contract file docs/contracts/knowledge-graph-search-api.yaml must exist");
        assertTrue(file.isFile(), "API contract path must point to a file");
        assertTrue(file.canRead(), "API contract file must be readable");
    }

    @Test
    @DisplayName("Knowledge Graph and Search OpenAPI Contract parses as valid OpenAPI 3.0.3 YAML")
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

        assertTrue(paths.containsKey("/api/v1/search"), "Contract must define /api/v1/search endpoint");
        assertTrue(paths.containsKey("/api/v1/graph/nodes"), "Contract must define /api/v1/graph/nodes endpoint");
        assertTrue(paths.containsKey("/api/v1/graph/nodes/{slug}"), "Contract must define /api/v1/graph/nodes/{slug} endpoint");
        assertTrue(paths.containsKey("/api/v1/graph/links"), "Contract must define /api/v1/graph/links endpoint");
        assertTrue(paths.containsKey("/llms.txt"), "Contract must define /llms.txt endpoint");
        assertTrue(paths.containsKey("/ai-agent.json"), "Contract must define /ai-agent.json endpoint");

        // Validate /api/v1/search details
        Map<String, Object> searchEndpoint = (Map<String, Object>) paths.get("/api/v1/search");
        assertNotNull(searchEndpoint.get("get"), "GET method must be defined for /api/v1/search");
        Map<String, Object> searchGet = (Map<String, Object>) searchEndpoint.get("get");
        List<Map<String, Object>> searchParams = (List<Map<String, Object>>) searchGet.get("parameters");
        assertNotNull(searchParams, "Parameters must be defined for /api/v1/search");

        boolean hasQueryParam = searchParams.stream()
                .anyMatch(p -> "q".equals(p.get("name")) && Boolean.TRUE.equals(p.get("required")));
        assertTrue(hasQueryParam, "Search endpoint must require parameter 'q'");

        // Validate Components / Schemas
        Map<String, Object> components = (Map<String, Object>) root.get("components");
        assertNotNull(components, "Components section must exist");
        Map<String, Object> schemas = (Map<String, Object>) components.get("schemas");
        assertNotNull(schemas, "Schemas section must exist");

        assertTrue(schemas.containsKey("SearchDomain"), "Schema SearchDomain must exist");
        assertTrue(schemas.containsKey("SearchResultItem"), "Schema SearchResultItem must exist");
        assertTrue(schemas.containsKey("SearchResponse"), "Schema SearchResponse must exist");
        assertTrue(schemas.containsKey("GraphNode"), "Schema GraphNode must exist");
        assertTrue(schemas.containsKey("GraphLink"), "Schema GraphLink must exist");
        assertTrue(schemas.containsKey("GraphNodeDetail"), "Schema GraphNodeDetail must exist");
        assertTrue(schemas.containsKey("AiAgentGraphRepresentation"), "Schema AiAgentGraphRepresentation must exist");
        assertTrue(schemas.containsKey("ErrorResponse"), "Schema ErrorResponse must exist");
    }
}
