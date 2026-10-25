#!/usr/bin/env python3
"""
Validation script for Knowledge Base API OpenAPI contract (docs/contracts/knowledge-base-api.yaml).
Verifies that:
1. File exists and is valid YAML.
2. OpenAPI version is 3.0.x.
3. Required endpoints (/api/v1/knowledge-base/concepts, /api/v1/knowledge-base/concepts/{slug},
   /api/v1/knowledge-base/categories, /api/v1/knowledge-base/tags,
   /api/v1/knowledge-base/concepts/{slug}/models) are defined.
4. GET /api/v1/knowledge-base/concepts defines parameters 'category', 'tag', 'q', 'page', and 'size'.
5. Schemas for ConceptItem, ConceptDetail, ConceptCreateRequest, ConceptPage,
   AppliedModelConfiguration, AppliedModelCreateRequest, CategoryList, TagList, and ErrorResponse are present.
"""

import sys
from pathlib import Path
import yaml

def main():
    contract_path = Path("docs/contracts/knowledge-base-api.yaml")
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
        "/api/v1/knowledge-base/concepts",
        "/api/v1/knowledge-base/concepts/{slug}",
        "/api/v1/knowledge-base/categories",
        "/api/v1/knowledge-base/tags",
        "/api/v1/knowledge-base/concepts/{slug}/models"
    ]
    for req_path in required_paths:
        assert req_path in paths, f"Missing {req_path} endpoint"

    assert "get" in paths["/api/v1/knowledge-base/concepts"], "Missing GET on /api/v1/knowledge-base/concepts"
    assert "post" in paths["/api/v1/knowledge-base/concepts"], "Missing POST on /api/v1/knowledge-base/concepts"
    assert "get" in paths["/api/v1/knowledge-base/concepts/{slug}"], "Missing GET on /api/v1/knowledge-base/concepts/{slug}"

    # 3. Check parameters for concept fetching / searching / filtering
    get_concepts = paths["/api/v1/knowledge-base/concepts"]["get"]
    params = get_concepts.get("parameters", [])
    param_names = [p.get("name") for p in params]
    for expected_param in ["category", "tag", "q", "page", "size"]:
        assert expected_param in param_names, f"Missing parameter '{expected_param}' on GET /api/v1/knowledge-base/concepts"

    # 4. Check required schemas
    components = spec.get("components", {})
    schemas = components.get("schemas", {})

    required_schemas = [
        "ConceptItem",
        "ConceptDetail",
        "ConceptCreateRequest",
        "ConceptPage",
        "AppliedModelConfiguration",
        "AppliedModelCreateRequest",
        "CategoryList",
        "TagList",
        "ErrorResponse"
    ]
    for req_schema in required_schemas:
        assert req_schema in schemas, f"Missing schema definition for {req_schema}"

    # 5. Check ConceptItem properties
    concept_props = schemas["ConceptItem"].get("properties", {})
    for prop in ["id", "slug", "title", "category", "description", "content", "tags"]:
        assert prop in concept_props, f"ConceptItem missing '{prop}' property"

    print("SUCCESS: Knowledge Base API OpenAPI contract is valid and meets all acceptance criteria.")

if __name__ == "__main__":
    main()
