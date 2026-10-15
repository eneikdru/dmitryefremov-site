#!/usr/bin/env python3
"""
Validation script for Client Intake API OpenAPI contract (docs/contracts/intake-api.yaml).
Verifies that:
1. File exists and is valid YAML.
2. OpenAPI version is 3.0.x.
3. Required endpoints (/api/intake and /api/intake/{intakeId}) are defined.
4. Schemas for ClientType, IntakeStatus, AgentMetadata, ClientIntakeRequest,
   ClientIntakeResponse, IntakePage, and ErrorResponse are present.
5. ClientIntakeRequest schema explicitly enforces validation rules for both human and agentic requests.
"""

import sys
from pathlib import Path
import yaml

def main():
    contract_path = Path("docs/contracts/intake-api.yaml")
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
    assert "/api/intake" in paths, "Missing /api/intake endpoint"
    assert "post" in paths["/api/intake"], "Missing POST operation on /api/intake"
    assert "get" in paths["/api/intake"], "Missing GET operation on /api/intake"

    assert "/api/intake/{intakeId}" in paths, "Missing /api/intake/{intakeId} endpoint"
    assert "get" in paths["/api/intake/{intakeId}"], "Missing GET operation on /api/intake/{intakeId}"

    # 3. Check required schemas
    components = spec.get("components", {})
    schemas = components.get("schemas", {})

    required_schemas = [
        "ClientType",
        "IntakeStatus",
        "AgentMetadata",
        "ClientIntakeRequest",
        "ClientIntakeResponse",
        "IntakePage",
        "ErrorResponse",
        "ValidationError"
    ]
    for req_schema in required_schemas:
        assert req_schema in schemas, f"Missing schema definition for {req_schema}"

    # 4. Check ClientType enum
    client_type_enum = schemas["ClientType"].get("enum", [])
    assert "HUMAN" in client_type_enum, "ClientType enum missing 'HUMAN'"
    assert "AGENT" in client_type_enum, "ClientType enum missing 'AGENT'"

    # 5. Check ClientIntakeRequest validation rules
    intake_req = schemas["ClientIntakeRequest"]
    required_fields = intake_req.get("required", [])
    for required_prop in ["clientName", "clientEmail", "wishlistPayload"]:
        assert required_prop in required_fields, f"ClientIntakeRequest missing required field '{required_prop}'"

    props = intake_req.get("properties", {})
    assert "clientType" in props, "ClientIntakeRequest missing 'clientType' property"
    assert "agentMetadata" in props, "ClientIntakeRequest missing 'agentMetadata' property"

    name_prop = props.get("clientName", {})
    assert name_prop.get("minLength") == 2, f"Expected clientName minLength=2, got {name_prop.get('minLength')}"
    assert name_prop.get("maxLength") == 255, f"Expected clientName maxLength=255, got {name_prop.get('maxLength')}"

    email_prop = props.get("clientEmail", {})
    assert email_prop.get("format") == "email", f"Expected clientEmail format='email', got {email_prop.get('format')}"
    assert email_prop.get("maxLength") == 255, f"Expected clientEmail maxLength=255, got {email_prop.get('maxLength')}"

    payload_prop = props.get("wishlistPayload", {})
    assert payload_prop.get("minLength") == 10, f"Expected wishlistPayload minLength=10, got {payload_prop.get('minLength')}"
    assert payload_prop.get("maxLength") == 10000, f"Expected wishlistPayload maxLength=10000, got {payload_prop.get('maxLength')}"

    print("SUCCESS: Client Intake API OpenAPI contract is valid and meets all acceptance criteria.")

if __name__ == "__main__":
    main()
