#!/usr/bin/env python3
"""
Validation script for Schedule API OpenAPI contract (docs/contracts/schedule-api.yaml).
Verifies that:
1. File exists and is valid YAML.
2. OpenAPI version is 3.0.x.
3. Required endpoint (/api/v1/schedule) is defined with GET method.
4. Query parameter `platform` is defined.
5. Schemas for PublicationSchedule and ErrorResponse are present and correctly formatted.
"""

import sys
from pathlib import Path
import yaml

def main():
    contract_path = Path("docs/contracts/schedule-api.yaml")
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
    assert "/api/v1/schedule" in paths, "Missing /api/v1/schedule endpoint"
    assert "get" in paths["/api/v1/schedule"], "Missing GET operation on /api/v1/schedule"

    # 3. Check parameters
    get_op = paths["/api/v1/schedule"]["get"]
    params = get_op.get("parameters", [])
    param_names = [p.get("name") for p in params]
    assert "platform" in param_names, "Missing 'platform' query parameter in GET /api/v1/schedule"

    # 4. Check schemas
    components = spec.get("components", {})
    schemas = components.get("schemas", {})

    for required_schema in ["PublicationSchedule", "ErrorResponse"]:
        assert required_schema in schemas, f"Missing schema definition for {required_schema}"

    # 5. Check PublicationSchedule schema properties
    sched_schema = schemas["PublicationSchedule"]
    props = sched_schema.get("properties", {})
    required_props = [
        "id", "title", "platform", "status", "topicSummary", "scheduledAt", "createdAt"
    ]
    for prop in required_props:
        assert prop in props, f"PublicationSchedule schema missing '{prop}' property"

    print("SUCCESS: Schedule API OpenAPI contract is valid and meets all acceptance criteria.")

if __name__ == "__main__":
    main()
