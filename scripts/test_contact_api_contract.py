#!/usr/bin/env python3
"""
Validation script for Contact API OpenAPI contract (docs/contracts/contact-api.yaml).
Verifies that:
1. File exists and is valid YAML.
2. OpenAPI version is 3.0.x.
3. Required endpoints (/api/v1/contact POST/GET and /api/v1/contact/{id} GET) are defined.
4. Schemas for ContactStatus, ContactSubmissionRequest, ContactSubmissionResponse,
   ContactMessagePage, ErrorResponse, and ValidationError are present.
5. ContactSubmissionRequest schema explicitly enforces validation rules.
"""

import sys
from pathlib import Path
import yaml

def main():
    contract_path = Path("docs/contracts/contact-api.yaml")
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
    assert "/api/v1/contact" in paths, "Missing /api/v1/contact endpoint"
    assert "post" in paths["/api/v1/contact"], "Missing POST operation on /api/v1/contact"
    assert "get" in paths["/api/v1/contact"], "Missing GET operation on /api/v1/contact"

    assert "/api/v1/contact/{id}" in paths, "Missing /api/v1/contact/{id} endpoint"
    assert "get" in paths["/api/v1/contact/{id}"], "Missing GET operation on /api/v1/contact/{id}"

    # 3. Check required schemas
    components = spec.get("components", {})
    schemas = components.get("schemas", {})

    required_schemas = [
        "ContactStatus",
        "ContactSubmissionRequest",
        "ContactSubmissionResponse",
        "ContactMessagePage",
        "ValidationError",
        "ErrorResponse"
    ]
    for req_schema in required_schemas:
        assert req_schema in schemas, f"Missing schema definition for {req_schema}"

    # 4. Check ContactStatus enum
    status_enum = schemas["ContactStatus"].get("enum", [])
    for expected_status in ["NEW", "IN_REVIEW", "PROCESSED", "ARCHIVED"]:
        assert expected_status in status_enum, f"ContactStatus enum missing '{expected_status}'"

    # 5. Check ContactSubmissionRequest validation rules
    contact_req = schemas["ContactSubmissionRequest"]
    required_fields = contact_req.get("required", [])
    for required_prop in ["senderName", "senderEmail", "content"]:
        assert required_prop in required_fields, f"ContactSubmissionRequest missing required field '{required_prop}'"

    props = contact_req.get("properties", {})

    name_prop = props.get("senderName", {})
    assert name_prop.get("minLength") == 2, f"Expected senderName minLength=2, got {name_prop.get('minLength')}"
    assert name_prop.get("maxLength") == 255, f"Expected senderName maxLength=255, got {name_prop.get('maxLength')}"

    email_prop = props.get("senderEmail", {})
    assert email_prop.get("format") == "email", f"Expected senderEmail format='email', got {email_prop.get('format')}"
    assert email_prop.get("maxLength") == 255, f"Expected senderEmail maxLength=255, got {email_prop.get('maxLength')}"

    content_prop = props.get("content", {})
    assert content_prop.get("minLength") == 5, f"Expected content minLength=5, got {content_prop.get('minLength')}"
    assert content_prop.get("maxLength") == 5000, f"Expected content maxLength=5000, got {content_prop.get('maxLength')}"

    print("SUCCESS: Direct Contact API OpenAPI contract is valid and meets all acceptance criteria.")

if __name__ == "__main__":
    main()
