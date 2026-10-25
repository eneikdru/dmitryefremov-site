#!/usr/bin/env python3
"""
Validation script for Contact API OpenAPI contract (docs/contracts/contact-api.yaml).
Verifies that:
1. File exists and is valid YAML.
2. OpenAPI version is 3.0.x.
3. Required endpoints (/api/v1/contact, /api/contact, /api/v1/contact/{id}) are defined.
4. Schemas for ContactSubmissionRequest, ContactSubmissionResponse, and ErrorResponse are present.
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

    assert "/api/contact" in paths, "Missing /api/contact alias endpoint"
    assert "post" in paths["/api/contact"], "Missing POST operation on /api/contact"

    assert "/api/v1/contact/{id}" in paths, "Missing /api/v1/contact/{id} endpoint"
    assert "get" in paths["/api/v1/contact/{id}"], "Missing GET operation on /api/v1/contact/{id}"

    # 3. Check required schemas
    components = spec.get("components", {})
    schemas = components.get("schemas", {})

    required_schemas = [
        "ContactSubmissionRequest",
        "ContactSubmissionResponse",
        "ErrorResponse",
        "ValidationError"
    ]
    for req_schema in required_schemas:
        assert req_schema in schemas, f"Missing schema definition for {req_schema}"

    # 4. Check ContactSubmissionRequest validation rules
    contact_req = schemas["ContactSubmissionRequest"]
    required_fields = contact_req.get("required", [])
    for required_prop in ["senderName", "senderEmail", "content"]:
        assert required_prop in required_fields, f"ContactSubmissionRequest missing required field '{required_prop}'"

    props = contact_req.get("properties", {})
    email_prop = props.get("senderEmail", {})
    assert email_prop.get("format") == "email", f"Expected senderEmail format='email', got {email_prop.get('format')}"

    print("SUCCESS: Contact API OpenAPI contract is valid and meets all criteria.")

if __name__ == "__main__":
    main()
