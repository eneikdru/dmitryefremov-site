#!/usr/bin/env python3
"""
Validation script for Article API OpenAPI contract (docs/contracts/article-api.yaml).
Verifies that:
1. File exists and is valid YAML.
2. OpenAPI version is 3.0.x.
3. Required endpoints (/api/v1/articles and /api/v1/articles/{slug}) are defined.
4. Schemas for Article, ArticlePage, and ErrorResponse are present.
5. Article schema includes 'content' (raw Markdown) and 'publishedAt'.
"""

import sys
from pathlib import Path
import yaml

def main():
    contract_path = Path("docs/contracts/article-api.yaml")
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
    assert "/api/v1/articles" in paths, "Missing /api/v1/articles endpoint"
    assert "get" in paths["/api/v1/articles"], "Missing GET operation on /api/v1/articles"

    assert "/api/v1/articles/{slug}" in paths, "Missing /api/v1/articles/{slug} endpoint"
    assert "get" in paths["/api/v1/articles/{slug}"], "Missing GET operation on /api/v1/articles/{slug}"

    # 3. Check schemas
    components = spec.get("components", {})
    schemas = components.get("schemas", {})

    for required_schema in ["Article", "ArticlePage", "ErrorResponse"]:
        assert required_schema in schemas, f"Missing schema definition for {required_schema}"

    # 4. Check Article schema properties
    article_schema = schemas["Article"]
    props = article_schema.get("properties", {})
    assert "content" in props, "Article schema missing 'content' property for raw Markdown body"
    assert "publishedAt" in props, "Article schema missing 'publishedAt' property"
    assert "slug" in props, "Article schema missing 'slug' property"
    assert "title" in props, "Article schema missing 'title' property"

    print("SUCCESS: Article API OpenAPI contract is valid and meets all acceptance criteria.")

if __name__ == "__main__":
    main()
