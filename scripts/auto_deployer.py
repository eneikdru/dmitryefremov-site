#!/usr/bin/env python3
"""
Automated Preview Deployment Engine (Auto-Deployer) for Dmitry Efremov Architectural Gateway.
Zero-touch deployment for client applications committed via /api/v1/bridge/commit.
"""

import os
import sys
import json
import time
import socket
import hashlib
import secrets
import subprocess

DATA_DIR = "/opt/dmitryefremov-site/data"
PORTS_FILE = os.path.join(DATA_DIR, "preview_ports.json")
RUNTIMES_DIR = "/opt/client-runtimes"
SHARED_VENV = "/opt/client-runtimes/venv"
PORT_START = 18100
PORT_END = 18200

os.makedirs(DATA_DIR, exist_ok=True)
os.makedirs(RUNTIMES_DIR, exist_ok=True)

def read_ports():
    if os.path.exists(PORTS_FILE):
        try:
            with open(PORTS_FILE, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception:
            pass
    return {"3d21f94c": 18095}

def save_ports(ports):
    try:
        with open(PORTS_FILE, "w", encoding="utf-8") as f:
            json.dump(ports, f, indent=2, ensure_ascii=False)
    except Exception as e:
        print(f"[AUTO-DEPLOY] Error saving ports: {e}", file=sys.stderr)

def is_port_in_use(port: int) -> bool:
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.settimeout(0.5)
        return s.connect_ex(("127.0.0.1", port)) == 0

def get_preview_port(slug: str) -> int:
    ports = read_ports()
    return ports.get(slug.lower())

def get_or_allocate_port(slug: str) -> int:
    slug = slug.lower()
    ports = read_ports()
    if slug in ports:
        return ports[slug]
    
    used_ports = set(ports.values())
    for p in range(PORT_START, PORT_END):
        if p not in used_ports and not is_port_in_use(p):
            ports[slug] = p
            save_ports(ports)
            print(f"[AUTO-DEPLOY] Allocated port {p} for slug '{slug}'")
            return p
            
    raise RuntimeError(f"No available ports in range {PORT_START}-{PORT_END}")

def deploy_client_project(slug: str, files: list) -> dict:
    slug = slug.lower()
    port = get_or_allocate_port(slug)
    target_dir = os.path.join(RUNTIMES_DIR, f"client-{slug}")
    os.makedirs(target_dir, exist_ok=True)
    
    print(f"[AUTO-DEPLOY] Starting zero-touch deploy for client-{slug} on port {port}...")
    
    # 1. Sync all incoming files to disk
    for item in files:
        rel_path = item.get("path", "").lstrip("/\\")
        content = item.get("content", "")
        if not rel_path or content is None:
            continue
        dest_path = os.path.join(target_dir, rel_path)
        os.makedirs(os.path.dirname(dest_path), exist_ok=True)
        with open(dest_path, "w", encoding="utf-8") as f:
            f.write(content)
            
    # 2. Check and seed database if catalog + manage.py exist
    manage_py = os.path.join(target_dir, "manage.py")
    catalog_json = os.path.join(target_dir, "catalog.example.json")
    booking_db = os.path.join(target_dir, "booking.db")
    
    if os.path.exists(manage_py) and os.path.exists(catalog_json) and not os.path.exists(booking_db):
        try:
            print(f"[AUTO-DEPLOY] Initializing SQLite catalog for {slug}...")
            subprocess.run(
                [f"{SHARED_VENV}/bin/python", "manage.py", "catalog.example.json"],
                cwd=target_dir,
                capture_output=True,
                timeout=10,
                check=False
            )
        except Exception as e:
            print(f"[AUTO-DEPLOY] Database seeding warning: {e}", file=sys.stderr)
            
    # 3. Seed preview bearer token in SQLite
    if os.path.exists(booking_db):
        try:
            import sqlite3
            token = f"client-preview-token-{slug}"
            token_hash = hashlib.sha256(token.encode()).hexdigest()
            user = f"client-{slug}-user"
            expires = int(time.time()) + 86400 * 365
            con = sqlite3.connect(booking_db)
            with con:
                con.execute("INSERT OR IGNORE INTO users VALUES (?)", (user,))
                con.execute("DELETE FROM tokens WHERE hash=?", (token_hash,))
                con.execute("INSERT INTO tokens VALUES (?,?,?)", (token_hash, user, expires))
            con.close()
            print(f"[AUTO-DEPLOY] Seeded preview bearer token for {slug}")
        except Exception as e:
            print(f"[AUTO-DEPLOY] Warning seeding token in sqlite: {e}", file=sys.stderr)

    # 4. Detect entrypoint
    entrypoint = None
    if os.path.exists(os.path.join(target_dir, "booking.py")):
        entrypoint = "booking:create_app --factory"
    elif os.path.exists(os.path.join(target_dir, "main.py")):
        entrypoint = "main:app"
    elif os.path.exists(os.path.join(target_dir, "app.py")):
        entrypoint = "app:app"
        
    if not entrypoint:
        return {
            "success": True,
            "deployed": False,
            "reason": "No web entrypoint found (booking.py / main.py / app.py)",
            "port": port
        }
        
    secret = secrets.token_hex(32)
    service_name = f"client-{slug}.service"
    service_path = f"/etc/systemd/system/{service_name}"
    
    service_unit = f"""[Unit]
Description=Autonomous Client Preview Runtime — {slug}
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory={target_dir}
Environment=PORT={port}
Environment=SESSION_SECRET={secret}
Environment=BASE_URL=https://dmitryefremov.com/api/preview/{slug}
Environment=ROOT_PATH=/api/preview/{slug}
ExecStart={SHARED_VENV}/bin/python -m uvicorn {entrypoint} --port {port} --host 127.0.0.1 --root-path /api/preview/{slug}
Restart=always
RestartSec=3

[Install]
WantedBy=multi-user.target
"""
    
    with open(service_path, "w", encoding="utf-8") as f:
        f.write(service_unit)
        
    # 5. Reload systemd and start service
    try:
        subprocess.run(["systemctl", "daemon-reload"], check=True)
        subprocess.run(["systemctl", "enable", "--now", service_name], check=True)
        subprocess.run(["systemctl", "restart", service_name], check=True)
        time.sleep(0.5)
        print(f"[AUTO-DEPLOY] Successfully started {service_name} on port {port}")
    except Exception as e:
        print(f"[AUTO-DEPLOY] Error starting systemd service: {e}", file=sys.stderr)
        return {"success": False, "error": str(e), "port": port}
        
    live_url = f"https://dmitryefremov.com/api/preview/{slug}/"
    return {
        "success": True,
        "deployed": True,
        "port": port,
        "service": service_name,
        "live_url": live_url
    }

if __name__ == "__main__":
    print("Auto-Deployer Module ready.")
    p = get_or_allocate_port("3d21f94c")
    print(f"Slug 3d21f94c port: {p}")
