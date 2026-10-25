#!/usr/bin/env python3
"""
Automated GDPR Compliance Data Export & Erasure Script.

Fulfills statutory compliance requirements for processing data subject rights:
- Export: Retrieves all associated records (orders, messages, intake requests) as structured JSON.
- Erasure: Permanently deletes all personal data records for the given user ID or email address.

Usage:
  python3 scripts/compliance_gdpr_export_erasure.py --action export --email user@example.com
  python3 scripts/compliance_gdpr_export_erasure.py --action erasure --email user@example.com
  python3 scripts/compliance_gdpr_export_erasure.py --validate-contract
"""

import sys
import argparse
import json
import urllib.request
import urllib.parse
from pathlib import Path

try:
    import yaml
    HAS_YAML = True
except ImportError:
    HAS_YAML = False


def validate_contract():
    if not HAS_YAML:
        print("Error: PyYAML is required for contract validation.")
        sys.exit(1)

    contract_path = Path("docs/contracts/admin-dashboard-api.yaml")
    if not contract_path.exists():
        print(f"Error: Contract file {contract_path} does not exist.")
        sys.exit(1)

    with open(contract_path, "r", encoding="utf-8") as f:
        spec = yaml.safe_load(f)

    paths = spec.get("paths", {})
    assert "/api/v1/admin/gdpr/export/{userId}" in paths, "Missing export endpoint in admin contract"
    assert "/api/v1/admin/gdpr/erasure/{userId}" in paths, "Missing erasure endpoint in admin contract"

    schemas = spec.get("components", {}).get("schemas", {})
    assert "DataExportResponse" in schemas, "Missing DataExportResponse schema"
    assert "ErasureResponse" in schemas, "Missing ErasureResponse schema"

    print("SUCCESS: GDPR Compliance API contracts are valid.")


def perform_action(action, identifier, base_url):
    encoded_id = urllib.parse.quote(identifier)

    if action == "export":
        url = f"{base_url}/api/v1/compliance/export?email={encoded_id}"
        req = urllib.request.Request(url, method="GET")
    elif action == "erasure":
        url = f"{base_url}/api/v1/compliance/erasure?email={encoded_id}"
        req = urllib.request.Request(url, method="DELETE")
    else:
        print(f"Error: Unknown action '{action}'")
        sys.exit(1)

    try:
        with urllib.request.urlopen(req) as response:
            status_code = response.getcode()
            body = response.read().decode("utf-8")
            data = json.loads(body)
            print(f"HTTP {status_code}: Action '{action}' completed successfully for '{identifier}'.")
            print(json.dumps(data, indent=2))
            return data
    except urllib.error.HTTPError as e:
        body = e.read().decode("utf-8")
        print(f"HTTP Error {e.code}: {e.reason}\n{body}")
        sys.exit(1)
    except urllib.error.URLError as e:
        print(f"URL Error connecting to {base_url}: {e.reason}")
        sys.exit(1)


def main():
    parser = argparse.ArgumentParser(description="Automated GDPR Data Subject Rights Export & Erasure Tool")
    parser.add_argument("--action", choices=["export", "erasure"], help="Action to perform: export or erasure")
    parser.add_argument("--email", "--user-id", dest="identifier", help="Email address or User ID of data subject")
    parser.add_argument("--base-url", default="http://localhost:8080", help="Base URL of backend server")
    parser.add_argument("--validate-contract", action="store_true", help="Validate OpenAPI contract definitions for GDPR endpoints")

    args = parser.parse_args()

    if args.validate_contract:
        validate_contract()
        return

    if not args.action or not args.identifier:
        parser.print_help()
        sys.exit(0)

    perform_action(args.action, args.identifier, args.base_url)


if __name__ == "__main__":
    main()
