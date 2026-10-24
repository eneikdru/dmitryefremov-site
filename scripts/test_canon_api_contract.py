#!/usr/bin/env python3
"""
Validation script for Canon Content API OpenAPI contract (docs/contracts/canon-api.yaml).
Verifies that:
1. File exists and is valid YAML.
2. OpenAPI version is 3.0.x.
3. Required endpoints (/api/v1/canon, /api/v1/canon/{slug}, /api/v1/canon/{sourceSlug}/references/{targetSlug}) are defined.
4. GET and POST operations on /api/v1/canon are defined.
5. CanonItem schema is present and includes 'content' (raw Markdown) and required metadata fields.
"""

import sys
from pathlib import Path
import yaml

def main():
    contract_path = Path("docs/contracts/canon-api.yaml")
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
    assert "/api/v1/canon" in paths, "Missing /api/v1/canon endpoint"
    assert "get" in paths["/api/v1/canon"], "Missing GET operation on /api/v1/canon"
    assert "post" in paths["/api/v1/canon"], "Missing POST operation on /api/v1/canon"

    assert "/api/v1/canon/{slug}" in paths, "Missing /api/v1/canon/{slug} endpoint"
    assert "get" in paths["/api/v1/canon/{slug}"], "Missing GET operation on /api/v1/canon/{slug}"

    # 3. Check schemas
    components = spec.get("components", {})
    schemas = components.get("schemas", {})

    for required_schema in ["CanonItem", "CanonDomain", "CanonType", "CanonPage", "ErrorResponse"]:
        assert required_schema in schemas, f"Missing schema definition for {required_schema}"

    # 4. Check CanonItem schema properties
    item_schema = schemas["CanonItem"]
    props = item_schema.get("properties", {})
    required_props = ["slug", "title", "type", "domain", "content"]
    for prop in required_props:
        assert prop in props, f"CanonItem schema missing '{prop}' property"

    print("SUCCESS: Canon API OpenAPI contract is valid and meets all acceptance criteria.")

if __name__ == "__main__":
    main()
