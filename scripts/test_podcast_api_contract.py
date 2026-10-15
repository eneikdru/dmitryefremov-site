#!/usr/bin/env python3
"""
Validation script for Podcast API OpenAPI contract (docs/contracts/podcast-api.yaml).
Verifies that:
1. File exists and is valid YAML.
2. OpenAPI version is 3.0.x.
3. Required endpoints (/api/v1/podcasts and /api/v1/podcasts/{slug}) are defined.
4. Schemas for PodcastEpisode, PodcastEpisodePage, PodcastTimecode, and ErrorResponse are present.
5. PodcastEpisode schema includes episode metadata, audioUrl, and platform URLs (youtubeUrl, telegramUrl, spotifyUrl, applePodcastsUrl, platforms).
"""

import sys
from pathlib import Path
import yaml

def main():
    contract_path = Path("docs/contracts/podcast-api.yaml")
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
    assert "/api/v1/podcasts" in paths, "Missing /api/v1/podcasts endpoint"
    assert "get" in paths["/api/v1/podcasts"], "Missing GET operation on /api/v1/podcasts"

    assert "/api/v1/podcasts/{slug}" in paths, "Missing /api/v1/podcasts/{slug} endpoint"
    assert "get" in paths["/api/v1/podcasts/{slug}"], "Missing GET operation on /api/v1/podcasts/{slug}"

    # 3. Check schemas
    components = spec.get("components", {})
    schemas = components.get("schemas", {})

    for required_schema in ["PodcastEpisode", "PodcastEpisodePage", "PodcastTimecode", "PodcastTopic", "ErrorResponse"]:
        assert required_schema in schemas, f"Missing schema definition for {required_schema}"

    # 4. Check PodcastTopic schema properties
    topic_schema = schemas["PodcastTopic"]
    topic_props = topic_schema.get("properties", {})
    required_topic_props = ["id", "topicName", "startTimeSeconds", "endTimeSeconds", "description", "displayOrder"]
    for prop in required_topic_props:
        assert prop in topic_props, f"PodcastTopic schema missing '{prop}' property"

    # 5. Check PodcastEpisode schema properties
    episode_schema = schemas["PodcastEpisode"]
    props = episode_schema.get("properties", {})
    required_props = [
        "id", "slug", "title", "thesis", "description", "audioUrl", "audio_url", "mediaLink",
        "youtubeUrl", "telegramUrl", "spotifyUrl", "applePodcastsUrl", "platforms",
        "durationSeconds", "publishedAt", "createdAt", "timecodes", "topics"
    ]
    for prop in required_props:
        assert prop in props, f"PodcastEpisode schema missing '{prop}' property"

    print("SUCCESS: Podcast API OpenAPI contract is valid and meets all acceptance criteria.")

if __name__ == "__main__":
    main()
