#!/usr/bin/env python3
"""
Validation script for Shared AI Agent Capabilities API OpenAPI contract (docs/contracts/ai-agent-capabilities-api.yaml).
Verifies that:
1. File exists and is valid YAML.
2. OpenAPI version is 3.0.x.
3. Required content discovery endpoints (/.well-known/llms.txt, /.well-known/ai-agent.json) and agent submission endpoints (/api/v1/ai/orders, /api/v1/ai/messages, /api/v1/ai/docs) are defined.
4. Schemas for request and response DTOs are present and contain expected properties and validation rules.
"""

import sys
from pathlib import Path
import yaml

def main():
    contract_path = Path("docs/contracts/ai-agent-capabilities-api.yaml")
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
        "/.well-known/llms.txt",
        "/.well-known/ai-agent.json",
        "/api/v1/ai/docs",
        "/api/v1/ai/orders",
        "/api/v1/ai/messages"
    ]
    for path in required_paths:
        assert path in paths, f"Missing endpoint path '{path}'"

    assert "get" in paths["/.well-known/llms.txt"], "Missing GET on /.well-known/llms.txt"
    assert "get" in paths["/.well-known/ai-agent.json"], "Missing GET on /.well-known/ai-agent.json"
    assert "get" in paths["/api/v1/ai/docs"], "Missing GET on /api/v1/ai/docs"
    assert "post" in paths["/api/v1/ai/orders"], "Missing POST on /api/v1/ai/orders"
    assert "post" in paths["/api/v1/ai/messages"], "Missing POST on /api/v1/ai/messages"

    # 3. Check schemas
    components = spec.get("components", {})
    schemas = components.get("schemas", {})

    required_schemas = [
        "ContentReference",
        "AiAgentDiscoveryResponse",
        "AiServiceOrderRequest",
        "AiServiceOrderResponse",
        "AiDirectMessageRequest",
        "AiDirectMessageResponse",
        "ErrorResponse"
    ]
    for schema_name in required_schemas:
        assert schema_name in schemas, f"Missing schema definition for '{schema_name}'"

    # 4. Check AiServiceOrderRequest schema
    order_req = schemas["AiServiceOrderRequest"]
    order_req_fields = order_req.get("required", [])
    for field in ["userName", "userEmail", "serviceType"]:
        assert field in order_req_fields, f"AiServiceOrderRequest missing required field '{field}'"

    # 5. Check AiDirectMessageRequest schema
    msg_req = schemas["AiDirectMessageRequest"]
    msg_req_fields = msg_req.get("required", [])
    for field in ["senderName", "senderEmail", "content"]:
        assert field in msg_req_fields, f"AiDirectMessageRequest missing required field '{field}'"

    print("SUCCESS: AI Agent Capabilities API OpenAPI contract is valid and meets all acceptance criteria.")

if __name__ == "__main__":
    main()
