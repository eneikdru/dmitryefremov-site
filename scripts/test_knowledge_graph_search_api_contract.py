#!/usr/bin/env python3
"""
Validation script for Knowledge Graph and Instant Search API OpenAPI contract (docs/contracts/knowledge-graph-search-api.yaml).
Verifies that:
1. File exists and is valid YAML.
2. OpenAPI version is 3.0.x.
3. Required endpoints (/api/v1/search, /api/v1/graph, /api/v1/graph/nodes/{nodeId}, /api/v1/graph/nodes/{nodeId}/neighbors, /api/v1/nodes/{slug}/machine-readable) are defined.
4. GET operation on /api/v1/search requires parameter 'q' and defines structured multi-domain response schemas.
5. All required DTO schemas (SearchResponse, SearchResultGroup, SearchResultItem, GraphData, GraphNode, GraphEdge, GraphNodeDetail, MachineReadableNode, ErrorResponse) are present and properly structured.
"""

import sys
from pathlib import Path
import yaml

def main():
    contract_path = Path("docs/contracts/knowledge-graph-search-api.yaml")
    if not contract_path.exists():
        print(f"Error: {contract_path} does not exist.")
        sys.exit(1)

    with open(contract_path, "r", encoding="utf-8") as f:
        spec = yaml.safe_load(f)

    # 1. Check OpenAPI version
    openapi_ver = spec.get("openapi", "")
    assert openapi_ver.startswith("3.0"), f"Expected OpenAPI 3.0.x, got {openapi_ver}"

    # 2. Check endpoints
    paths = spec.get("paths", {})
    required_paths = [
        "/api/v1/search",
        "/api/v1/graph",
        "/api/v1/graph/nodes/{nodeId}",
        "/api/v1/graph/nodes/{nodeId}/neighbors",
        "/api/v1/nodes/{slug}/machine-readable"
    ]
    for path in required_paths:
        assert path in paths, f"Missing endpoint: {path}"
        assert "get" in paths[path], f"Missing GET operation on {path}"

    # 3. Check search parameters
    search_get = paths["/api/v1/search"]["get"]
    params = search_get.get("parameters", [])
    param_names = [p.get("name") for p in params]
    assert "q" in param_names, "Missing required query parameter 'q' on /api/v1/search"

    # Verify 'q' parameter is required
    q_param = next(p for p in params if p.get("name") == "q")
    assert q_param.get("required") is True, "Query parameter 'q' must be marked required"

    # 4. Check required schemas
    components = spec.get("components", {})
    schemas = components.get("schemas", {})

    required_schemas = [
        "SearchResultType",
        "DomainType",
        "RelationshipType",
        "SearchResultItem",
        "SearchResultGroup",
        "SearchResponse",
        "GraphNode",
        "GraphEdge",
        "GraphData",
        "GraphNodeDetail",
        "GraphNeighborsResponse",
        "MachineReadableNode",
        "ErrorResponse"
    ]

    for schema in required_schemas:
        assert schema in schemas, f"Missing schema definition for {schema}"

    # 5. Check SearchResultItem schema properties
    item_props = schemas["SearchResultItem"].get("properties", {})
    required_item_props = ["id", "entityId", "type", "domain", "slug", "title", "summary", "score", "permalink"]
    for prop in required_item_props:
        assert prop in item_props, f"SearchResultItem schema missing property '{prop}'"

    # 6. Check SearchResponse schema properties
    search_response_props = schemas["SearchResponse"].get("properties", {})
    required_search_resp_props = ["query", "totalHits", "groups", "results"]
    for prop in required_search_resp_props:
        assert prop in search_response_props, f"SearchResponse schema missing property '{prop}'"

    # 7. Check MachineReadableNode schema properties
    mr_props = schemas["MachineReadableNode"].get("properties", {})
    required_mr_props = ["slug", "title", "type", "domain", "contentMarkdown", "permalink"]
    for prop in required_mr_props:
        assert prop in mr_props, f"MachineReadableNode schema missing property '{prop}'"

    print("SUCCESS: Knowledge Graph and Search API OpenAPI contract is valid and meets all acceptance criteria.")

if __name__ == "__main__":
    main()
