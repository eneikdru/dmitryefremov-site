#!/usr/bin/env python3
"""
Validation script for Admin Dashboard API OpenAPI contract (docs/contracts/admin-dashboard-api.yaml).
Verifies that:
1. File exists and is valid YAML.
2. OpenAPI version is 3.0.x.
3. Required endpoints are defined:
   - Telemetry Ingestion: /api/v1/telemetry (POST)
   - Admin Telemetry Querying: /api/v1/admin/telemetry (GET)
   - Admin Authentication: /api/v1/admin/auth/login (POST), /api/v1/admin/auth/me (GET), /api/v1/admin/auth/logout (POST)
   - GDPR Data Subject Rights: /api/v1/admin/gdpr/export/{userId} (GET), /api/v1/admin/gdpr/erasure/{userId} (DELETE), /api/v1/admin/gdpr/consent/{userId} (GET)
4. Required Schemas are present:
   - LoginRequest, AuthResponse, AdminUserResponse
   - TelemetryIngestRequest, TelemetryLog, TelemetryLogPage
   - ConsentRecord, DataExportResponse, ErasureResponse, ErrorResponse
"""

import sys
from pathlib import Path
import yaml

def main():
    contract_path = Path("docs/contracts/admin-dashboard-api.yaml")
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
    required_paths = {
        "/api/v1/telemetry": ["post"],
        "/api/v1/admin/auth/login": ["post"],
        "/api/v1/admin/auth/me": ["get"],
        "/api/v1/admin/auth/logout": ["post"],
        "/api/v1/admin/telemetry": ["get"],
        "/api/v1/admin/gdpr/export/{userId}": ["get"],
        "/api/v1/admin/gdpr/erasure/{userId}": ["delete"],
        "/api/v1/admin/gdpr/consent/{userId}": ["get"],
    }

    for path, methods in required_paths.items():
        assert path in paths, f"Missing path: {path}"
        for method in methods:
            assert method in paths[path], f"Missing operation '{method}' for path '{path}'"

    # 3. Check schemas
    components = spec.get("components", {})
    schemas = components.get("schemas", {})

    required_schemas = [
        "LoginRequest",
        "AuthResponse",
        "AdminUserResponse",
        "TelemetryIngestRequest",
        "TelemetryLog",
        "TelemetryLogPage",
        "ConsentRecord",
        "DataExportResponse",
        "ErasureResponse",
        "ErrorResponse",
    ]

    for req_schema in required_schemas:
        assert req_schema in schemas, f"Missing schema definition for {req_schema}"

    # 4. Check schema details
    # AdminUserResponse
    admin_user_props = schemas["AdminUserResponse"].get("properties", {})
    for prop in ["id", "username", "email", "role", "createdAt"]:
        assert prop in admin_user_props, f"AdminUserResponse missing '{prop}'"

    # TelemetryLog
    telemetry_props = schemas["TelemetryLog"].get("properties", {})
    for prop in ["id", "eventType", "payload", "source", "createdAt"]:
        assert prop in telemetry_props, f"TelemetryLog missing '{prop}'"

    # ConsentRecord
    consent_props = schemas["ConsentRecord"].get("properties", {})
    for prop in ["id", "userId", "consentType", "granted", "ipAddress", "createdAt"]:
        assert prop in consent_props, f"ConsentRecord missing '{prop}'"

    # DataExportResponse
    export_props = schemas["DataExportResponse"].get("properties", {})
    for prop in ["userId", "exportedAt", "consentRecords", "telemetryLogs"]:
        assert prop in export_props, f"DataExportResponse missing '{prop}'"

    # ErasureResponse
    erasure_props = schemas["ErasureResponse"].get("properties", {})
    for prop in ["userId", "erasedAt", "recordsErased", "status"]:
        assert prop in erasure_props, f"ErasureResponse missing '{prop}'"

    print("SUCCESS: Admin Dashboard API OpenAPI contract is valid and meets all acceptance criteria.")

if __name__ == "__main__":
    main()
