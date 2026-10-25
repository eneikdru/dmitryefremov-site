#!/usr/bin/env python3
"""
Direct Contact & Intake Message Service for Dmitry Efremov Personal Platform.
Receives incoming messages from the website contact terminal (VaultIntakeView),
persists them to secure message stores, and returns cryptographic claim tickets.
"""

import os
import sys
import json
import time
import uuid
import datetime
from http.server import HTTPServer, BaseHTTPRequestHandler
from socketserver import ThreadingMixIn

PORT = 18086
SITE_DATA_DIR = "/opt/dmitryefremov-site/data"
COCKPIT_DATA_DIR = "/opt/cockpit/data"

os.makedirs(SITE_DATA_DIR, exist_ok=True)
os.makedirs(COCKPIT_DATA_DIR, exist_ok=True)

class ThreadedHTTPServer(ThreadingMixIn, HTTPServer):
    daemon_threads = True

class IntakeHandler(BaseHTTPRequestHandler):
    def _send_cors_headers(self):
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With")

    def do_OPTIONS(self):
        self.send_response(204)
        self._send_cors_headers()
        self.end_headers()

    def do_GET(self):
        if self.path in ("/health", "/api/health"):
            self.send_response(200)
            self._send_cors_headers()
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.end_headers()
            self.wfile.write(b'{"status":"UP","service":"dmitryefremov-intake"}')
        elif self.path in ("/api/v1/contact", "/api/intake"):
            # Return list of recent messages summary
            messages_file = os.path.join(SITE_DATA_DIR, "messages.jsonl")
            msgs = []
            if os.path.exists(messages_file):
                try:
                    with open(messages_file, "r", encoding="utf-8") as f:
                        for line in f:
                            if line.strip():
                                msgs.append(json.loads(line.strip()))
                except Exception:
                    pass
            self.send_response(200)
            self._send_cors_headers()
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.end_headers()
            self.wfile.write(json.dumps({"total": len(msgs), "messages": msgs[-20:]}, ensure_ascii=False).encode("utf-8"))
        else:
            self.send_response(404)
            self.end_headers()

    def do_POST(self):
        if self.path not in ("/api/intake", "/api/v1/contact"):
            self.send_response(404)
            self.end_headers()
            return

        content_length = int(self.headers.get("Content-Length", 0))
        post_data = self.rfile.read(content_length).decode("utf-8-sig", errors="replace").strip()

        try:
            body = json.loads(post_data) if post_data else {}
        except Exception as e:
            self.send_response(400)
            self._send_cors_headers()
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.end_headers()
            self.wfile.write(json.dumps({"error": f"Invalid JSON: {str(e)}"}).encode("utf-8"))
            return

        now = datetime.datetime.now(datetime.timezone.utc)
        claim_ticket = f"MSG-{now.strftime('%Y%m%d')}-{uuid.uuid4().hex[:8].upper()}"

        client_email = (body.get("clientEmail") or body.get("senderEmail") or body.get("email") or "").strip()
        title = (body.get("title") or body.get("subject") or body.get("senderName") or "Прямое обращение").strip()
        narrative = (body.get("narrative") or body.get("content") or body.get("message") or "").strip()
        classification = body.get("classification") or "general_inquiry"

        record = {
            "id": int(time.time() * 1000),
            "claimTicket": claim_ticket,
            "status": "NEW",
            "classification": classification,
            "title": title,
            "narrative": narrative,
            "clientEmail": client_email,
            "createdAt": now.isoformat(),
            "discardMetadata": body.get("discardMetadata", True),
            "requireRecoveryPhrase": body.get("requireRecoveryPhrase", False),
            "clientIp": self.client_address[0]
        }

        # Persist to site data store
        site_file = os.path.join(SITE_DATA_DIR, "messages.jsonl")
        try:
            with open(site_file, "a", encoding="utf-8") as f:
                f.write(json.dumps(record, ensure_ascii=False) + "\n")
        except Exception as e:
            print(f"[ERROR] Failed to write to {site_file}: {e}", file=sys.stderr)

        # Also mirror to Cockpit data store so Dmitry can review in Cockpit
        cockpit_file = os.path.join(COCKPIT_DATA_DIR, "messages.jsonl")
        try:
            with open(cockpit_file, "a", encoding="utf-8") as f:
                f.write(json.dumps(record, ensure_ascii=False) + "\n")
        except Exception:
            pass

        print(f"[INTAKE] New message received: ticket={claim_ticket}, email={client_email}, title={title[:40]}")

        response_payload = {
            "success": True,
            "claimTicket": claim_ticket,
            "id": record["id"],
            "status": "RECEIVED",
            "message": "Сообщение успешно доставлено и зарегистрировано в системе.",
            "senderEmail": client_email,
            "title": title,
            "createdAt": record["createdAt"]
        }

        self.send_response(201)
        self._send_cors_headers()
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.end_headers()
        self.wfile.write(json.dumps(response_payload, ensure_ascii=False).encode("utf-8"))

def run():
    server_address = ("0.0.0.0", PORT)
    httpd = ThreadedHTTPServer(server_address, IntakeHandler)
    print(f"Dmitry Efremov Intake Service listening on 0.0.0.0:{PORT}")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
    httpd.server_close()

if __name__ == "__main__":
    run()
