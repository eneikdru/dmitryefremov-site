#!/usr/bin/env python3
"""
Validation script for infrastructure deployment configuration.
Verifies that:
1. docker-compose.yml exposes ports 80 and 443 for the frontend service.
2. Dockerfile.frontend builds frontend assets and configures SSL certificates.
3. nginx.conf includes target domain dmitryefremov.com, HTTP to HTTPS redirection, SSL termination on 443, and asset caching headers.
"""

import sys
from pathlib import Path
import yaml

def main():
    repo_root = Path(__file__).parent.parent

    # 1. Check docker-compose.yml
    compose_path = repo_root / "docker-compose.yml"
    assert compose_path.exists(), "docker-compose.yml missing"
    with open(compose_path, "r", encoding="utf-8") as f:
        compose_data = yaml.safe_load(f)

    frontend_service = compose_data.get("services", {}).get("frontend", {})
    ports = frontend_service.get("ports", [])
    assert "80:80" in ports or "80" in str(ports), "docker-compose.yml frontend service missing port 80"
    assert "443:443" in ports or "443" in str(ports), "docker-compose.yml frontend service missing port 443"

    # 2. Check Dockerfile.frontend
    dockerfile_path = repo_root / "Dockerfile.frontend"
    assert dockerfile_path.exists(), "Dockerfile.frontend missing"
    dockerfile_content = dockerfile_path.read_text(encoding="utf-8")

    assert "FROM node" in dockerfile_content or "npm run build" in dockerfile_content, "Dockerfile.frontend missing frontend build stage"
    assert "EXPOSE 80 443" in dockerfile_content or ("80" in dockerfile_content and "443" in dockerfile_content), "Dockerfile.frontend missing port exposures"
    assert "openssl" in dockerfile_content or "nginx.crt" in dockerfile_content, "Dockerfile.frontend missing SSL certificate generation"

    # 3. Check nginx.conf
    nginx_path = repo_root / "nginx.conf"
    assert nginx_path.exists(), "nginx.conf missing"
    nginx_content = nginx_path.read_text(encoding="utf-8")

    assert "dmitryefremov.com" in nginx_content, "nginx.conf missing target domain dmitryefremov.com"
    assert "listen 443 ssl" in nginx_content, "nginx.conf missing listen 443 ssl directive"
    assert "return 301 https://" in nginx_content, "nginx.conf missing HTTP-to-HTTPS redirect"
    assert "Cache-Control" in nginx_content, "nginx.conf missing static asset caching headers"

    print("SUCCESS: Deployment configuration validation passed successfully.")

if __name__ == "__main__":
    main()
