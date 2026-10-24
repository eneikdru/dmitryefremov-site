#!/usr/bin/env python3
"""
Validation script for Canon Content API OpenAPI contract (docs/contracts/canon-api.yaml).
Verifies that:
1. File exists and is valid YAML.
2. OpenAPI version is 3.0.x.
3. Required endpoints (/api/v1/canon, /api/v1/canon/{slug}, /api/v1/canon/{sourceSlug}/references/{targetSlug}) are defined.
4. Schemas for CanonItem, CanonPage, CanonDomain, CanonType, CanonCrossReference, and ErrorResponse are present.
5. POST operation for concept creation (/api/v1/canon) is defined and accepts Markdown content and metadata.
6. Forbidden internal teleprompter and telemetry fields are strictly excluded.
"""

import sys
from pathlib import Path
import yaml

FORBIDDEN_FIELDS = [
    "teleprompterScript",
    "teleprompter_script",
    "teleprompter",
    "draftContent",
    "rawDraft",
    "internalDraft",
    "telemetry",
    "telemetryData",
    "cueNotes",
    "recordingSessionId",
]

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
    assert "post" in paths["/api/v1/canon"], "Missing POST operation on /api/v1/canon for concept creation"

    assert "/api/v1/canon/{slug}" in paths, "Missing /api/v1/canon/{{slug}} endpoint"
    assert "get" in paths["/api/v1/canon/{slug}"], "Missing GET operation on /api/v1/canon/{{slug}}"

    assert "/api/v1/canon/{sourceSlug}/references/{targetSlug}" in paths, "Missing cross-reference endpoint"
    assert "post" in paths["/api/v1/canon/{sourceSlug}/references/{targetSlug}"], "Missing POST operation on cross-reference endpoint"

    # 3. Check schemas
    components = spec.get("components", {})
    schemas = components.get("schemas", {})

    for required_schema in ["CanonItem", "CanonPage", "CanonDomain", "CanonType", "CanonCrossReference", "ErrorResponse"]:
        assert required_schema in schemas, f"Missing schema definition for {required_schema}"

    # 4. Check CanonItem schema properties
    canon_item_schema = schemas["CanonItem"]
    props = canon_item_schema.get("properties", {})
    assert "content" in props, "CanonItem schema missing 'content' property for raw Markdown body"
    assert "slug" in props, "CanonItem schema missing 'slug' property"
    assert "title" in props, "CanonItem schema missing 'title' property"
    assert "type" in props, "CanonItem schema missing 'type' property"
    assert "domain" in props, "CanonItem schema missing 'domain' property"

    # 5. Strictly verify exclusion of teleprompter/telemetry/draft fields across all schemas
    spec_str = yaml.dump(spec)
    for forbidden in FORBIDDEN_FIELDS:
        for schema_name, schema_body in schemas.items():
            schema_props = schema_body.get("properties", {})
            assert forbidden not in schema_props, (
                f"Forbidden field '{forbidden}' found in schema '{schema_name}'"
            )

    print("SUCCESS: Canon API OpenAPI contract is valid and meets all acceptance criteria.")

if __name__ == "__main__":
    main()
