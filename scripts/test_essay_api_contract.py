#!/usr/bin/env python3
"""
Validation script for Essay API OpenAPI contract (docs/contracts/essay-api.yaml).
Verifies that:
1. File exists and is valid YAML.
2. OpenAPI version is 3.0.x.
3. Required endpoints (/api/v1/essays and /api/v1/essays/{slug}) are defined.
4. Schemas for Essay, EssayPage, and ErrorResponse are present.
5. Essay schema includes 'content' (raw Markdown), 'publishedAt', 'curatedBy', etc.
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
    contract_path = Path("docs/contracts/essay-api.yaml")
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
    assert "/api/v1/essays" in paths, "Missing /api/v1/essays endpoint"
    assert "get" in paths["/api/v1/essays"], "Missing GET operation on /api/v1/essays"
    assert "post" in paths["/api/v1/essays"], "Missing POST operation on /api/v1/essays for headless content ingestion"

    assert "/api/v1/essays/{slug}" in paths, "Missing /api/v1/essays/{slug} endpoint"
    assert "get" in paths["/api/v1/essays/{slug}"], "Missing GET operation on /api/v1/essays/{slug}"

    # 3. Check schemas
    components = spec.get("components", {})
    schemas = components.get("schemas", {})

    for required_schema in ["Essay", "CreateEssayRequest", "EssayPage", "ErrorResponse"]:
        assert required_schema in schemas, f"Missing schema definition for {required_schema}"

    # 4. Check Essay schema properties
    essay_schema = schemas["Essay"]
    props = essay_schema.get("properties", {})
    assert "content" in props, "Essay schema missing 'content' property for raw Markdown body"
    assert "publishedAt" in props, "Essay schema missing 'publishedAt' property"
    assert "slug" in props, "Essay schema missing 'slug' property"
    assert "title" in props, "Essay schema missing 'title' property"
    assert "curatedBy" in props, "Essay schema missing 'curatedBy' property"

    # 5. Strictly verify exclusion of teleprompter/telemetry/draft fields across all schemas
    spec_str = yaml.dump(spec)
    for forbidden in FORBIDDEN_FIELDS:
        for schema_name, schema_body in schemas.items():
            schema_props = schema_body.get("properties", {})
            assert forbidden not in schema_props, (
                f"Forbidden field '{forbidden}' found in schema '{schema_name}'"
            )

    print("SUCCESS: Essay API OpenAPI contract is valid and meets all acceptance criteria.")

if __name__ == "__main__":
    main()
