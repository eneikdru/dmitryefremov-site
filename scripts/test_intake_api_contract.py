#!/usr/bin/env python3
"""
Validation script for Client Intake API OpenAPI contract (docs/contracts/intake-api.yaml).
Verifies that:
1. File exists and is valid YAML.
2. OpenAPI version is 3.0.x.
3. Required endpoint (/api/v1/intake/wishlists) is defined with POST operation.
4. Schemas for ClientIntakeRequest, ClientIntakeResponse, and ErrorResponse are present.
5. ClientIntakeRequest schema requires clientName, clientEmail, and wishlistPayload.
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

    # 2. Check endpoint
    paths = spec.get("paths", {})
    endpoint = "/api/v1/intake/wishlists"
    assert endpoint in paths, f"Missing {endpoint} endpoint"
    assert "post" in paths[endpoint], f"Missing POST operation on {endpoint}"

    # 3. Check schemas
    components = spec.get("components", {})
    schemas = components.get("schemas", {})

    for required_schema in ["ClientIntakeRequest", "ClientIntakeResponse", "ErrorResponse"]:
        assert required_schema in schemas, f"Missing schema definition for {required_schema}"

    # 4. Check ClientIntakeRequest schema properties and required fields
    request_schema = schemas["ClientIntakeRequest"]
    required_fields = request_schema.get("required", [])
    for field in ["clientName", "clientEmail", "wishlistPayload"]:
        assert field in required_fields, f"ClientIntakeRequest schema missing required field '{field}'"

    print("SUCCESS: Client Intake API OpenAPI contract is valid and meets all acceptance criteria.")

if __name__ == "__main__":
    main()
