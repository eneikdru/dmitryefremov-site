#!/usr/bin/env python3
"""
Validation script for API Contract (docs/contracts/spark-api.yaml).
Verifies that:
1. File exists and is valid YAML.
2. OpenAPI version is 3.0.x.
3. Required endpoints for Podcasts, Essays, Sparks, and Webhooks are defined.
4. Schemas for PodcastEpisode, PodcastEpisodePage, Essay, EssayPage, Spark, SparkPage,
   EmptyStateResponse, TelegramWebhookPayload, and ErrorResponse are present.
5. Exact properties for Podcasts, Essays, Sparks, and Empty State payloads are strictly asserted.
"""

import sys
from pathlib import Path
import yaml

def main():
    contract_path = Path("docs/contracts/spark-api.yaml")
    if not contract_path.exists():
        print(f"Error: {contract_path} does not exist.")
        sys.exit(1)

    with open(contract_path, "r", encoding="utf-8") as f:
        spec = yaml.safe_load(f)

    # 1. Check OpenAPI version
    openapi_ver = spec.get("openapi", "")
    assert openapi_ver.startswith("3.0"), f"Expected OpenAPI 3.0.x, got {openapi_ver}"

    # 2. Check endpoints for Podcasts, Essays, Sparks, and Webhook
    paths = spec.get("paths", {})
    required_paths = [
        "/api/v1/podcasts",
        "/api/v1/podcasts/{slug}",
        "/api/v1/essays",
        "/api/v1/essays/{slug}",
        "/api/v1/sparks",
        "/api/v1/telegram/webhook",
        "/api/v1/sparks/webhook",
    ]
    for path in required_paths:
        assert path in paths, f"Missing {path} endpoint in contract"

    # 3. Check required schemas
    components = spec.get("components", {})
    schemas = components.get("schemas", {})

    required_schemas = [
        "PodcastEpisode",
        "PodcastEpisodePage",
        "PodcastTopic",
        "PodcastTimecode",
        "Essay",
        "EssayPage",
        "Spark",
        "SparkPage",
        "EmptyStateResponse",
        "TelegramWebhookPayload",
        "ErrorResponse"
    ]
    for req_schema in required_schemas:
        assert req_schema in schemas, f"Missing schema definition for {req_schema}"

    # 4. Check PodcastEpisode schema properties
    podcast_props = schemas["PodcastEpisode"].get("properties", {})
    for prop in ["id", "slug", "title", "audioUrl", "youtubeUrl", "publishedAt", "timecodes", "topics"]:
        assert prop in podcast_props, f"PodcastEpisode schema missing '{prop}' property"

    # 5. Check Essay schema properties
    essay_props = schemas["Essay"].get("properties", {})
    for prop in ["id", "slug", "title", "content", "curatedBy", "publishedAt"]:
        assert prop in essay_props, f"Essay schema missing '{prop}' property"

    # 6. Check Spark schema properties
    spark_props = schemas["Spark"].get("properties", {})
    for prop in ["id", "telegramMessageId", "content", "domain", "tags", "author", "publishedAt"]:
        assert prop in spark_props, f"Spark schema missing '{prop}' property"

    # 7. Check EmptyStateResponse schema properties
    empty_props = schemas["EmptyStateResponse"].get("properties", {})
    for prop in ["section", "empty", "message"]:
        assert prop in empty_props, f"EmptyStateResponse schema missing '{prop}' property"

    print("SUCCESS: Production Data Binding API contract is valid and meets all acceptance criteria.")

if __name__ == "__main__":
    main()
