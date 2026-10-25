#!/usr/bin/env python3
"""
Direct Contact, Intake Message & Mini-CRM Service for Dmitry Efremov Personal Platform.
Persists incoming messages, records client interaction history, and stores agreements.
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

MESSAGES_FILE = os.path.join(SITE_DATA_DIR, "messages.jsonl")
NOTES_FILE = os.path.join(SITE_DATA_DIR, "crm_notes.jsonl")
CLIENTS_FILE = os.path.join(SITE_DATA_DIR, "crm_clients.json")

class ThreadedHTTPServer(ThreadingMixIn, HTTPServer):
    daemon_threads = True

def read_jsonl(filepath):
    items = []
    if os.path.exists(filepath):
        try:
            with open(filepath, "r", encoding="utf-8") as f:
                for line in f:
                    line = line.strip()
                    if line:
                        try:
                            items.append(json.loads(line))
                        except Exception:
                            pass
        except Exception as e:
            print(f"[ERROR] Reading {filepath}: {e}", file=sys.stderr)
    return items

def read_clients_meta():
    if os.path.exists(CLIENTS_FILE):
        try:
            with open(CLIENTS_FILE, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception:
            pass
    return {}

def save_clients_meta(meta):
    try:
        with open(CLIENTS_FILE, "w", encoding="utf-8") as f:
            json.dump(meta, f, ensure_ascii=False, indent=2)
    except Exception as e:
        print(f"[ERROR] Saving clients meta: {e}", file=sys.stderr)

def get_aggregated_crm_data():
    messages = read_jsonl(MESSAGES_FILE)
    notes = read_jsonl(NOTES_FILE)
    clients_meta = read_clients_meta()

    clients_dict = {}

    # Aggregate by clientEmail
    for msg in messages:
        email = (msg.get("clientEmail") or "anonymous").strip().lower()
        if email not in clients_dict:
            meta = clients_meta.get(email, {})
            clients_dict[email] = {
                "email": email,
                "name": meta.get("name") or msg.get("title") or email,
                "status": meta.get("status", "NEW"),
                "company": meta.get("company", ""),
                "lastActivity": msg.get("createdAt", ""),
                "messages": [],
                "notes": []
            }
        clients_dict[email]["messages"].append(msg)
        if msg.get("createdAt", "") > clients_dict[email]["lastActivity"]:
            clients_dict[email]["lastActivity"] = msg.get("createdAt", "")

    for note in notes:
        email = (note.get("clientEmail") or "anonymous").strip().lower()
        if email not in clients_dict:
            meta = clients_meta.get(email, {})
            clients_dict[email] = {
                "email": email,
                "name": meta.get("name") or email,
                "status": meta.get("status", "IN_DISCUSSION"),
                "company": meta.get("company", ""),
                "lastActivity": note.get("createdAt", ""),
                "messages": [],
                "notes": []
            }
        clients_dict[email]["notes"].append(note)
        if note.get("createdAt", "") > clients_dict[email]["lastActivity"]:
            clients_dict[email]["lastActivity"] = note.get("createdAt", "")

    # Sort clients by last activity descending
    clients_list = list(clients_dict.values())
    clients_list.sort(key=lambda c: c["lastActivity"], reverse=True)

    # Sort each client's internal messages & notes
    for c in clients_list:
        c["messages"].sort(key=lambda m: m.get("createdAt", ""), reverse=True)
        c["notes"].sort(key=lambda n: n.get("createdAt", ""), reverse=True)

    return {
        "total": len(messages),
        "messages": messages[-50:],
        "notes": notes[-100:],
        "clients": clients_list
    }

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
            self.wfile.write(b'{"status":"UP","service":"dmitryefremov-intake-crm"}')
        elif self.path in ("/api/v1/contact", "/api/intake", "/api/crm", "/api/crm/clients"):
            crm_data = get_aggregated_crm_data()
            self.send_response(200)
            self._send_cors_headers()
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.end_headers()
            self.wfile.write(json.dumps(crm_data, ensure_ascii=False).encode("utf-8"))
        else:
            self.send_response(404)
            self.end_headers()

    def do_POST(self):
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

        # 1. Add Note or Agreement
        if self.path == "/api/crm/note":
            client_email = (body.get("clientEmail") or "").strip().lower()
            text = (body.get("text") or "").strip()
            note_type = body.get("type") or "agreement"  # 'agreement' or 'note'

            if not text:
                self.send_response(400)
                self._send_cors_headers()
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.end_headers()
                self.wfile.write(b'{"error":"Note text is required"}')
                return

            note_record = {
                "id": int(time.time() * 1000),
                "clientEmail": client_email,
                "type": note_type,
                "text": text,
                "createdAt": now.isoformat()
            }

            try:
                with open(NOTES_FILE, "a", encoding="utf-8") as f:
                    f.write(json.dumps(note_record, ensure_ascii=False) + "\n")
            except Exception as e:
                print(f"[ERROR] Failed to write note: {e}", file=sys.stderr)

            print(f"[CRM] Note recorded for {client_email}: type={note_type}")

            self.send_response(201)
            self._send_cors_headers()
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.end_headers()
            self.wfile.write(json.dumps({"success": True, "note": note_record}, ensure_ascii=False).encode("utf-8"))
            return

        # 2. Update Client Status or Metadata
        elif self.path == "/api/crm/status":
            client_email = (body.get("clientEmail") or "").strip().lower()
            status = body.get("status") or "IN_DISCUSSION"
            name = (body.get("name") or "").strip()
            company = (body.get("company") or "").strip()

            if not client_email:
                self.send_response(400)
                self._send_cors_headers()
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.end_headers()
                self.wfile.write(b'{"error":"clientEmail is required"}')
                return

            meta = read_clients_meta()
            if client_email not in meta:
                meta[client_email] = {}
            meta[client_email]["status"] = status
            if name:
                meta[client_email]["name"] = name
            if company:
                meta[client_email]["company"] = company
            save_clients_meta(meta)

            print(f"[CRM] Updated status for {client_email} -> {status}")

            self.send_response(200)
            self._send_cors_headers()
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.end_headers()
            self.wfile.write(json.dumps({"success": True, "meta": meta[client_email]}, ensure_ascii=False).encode("utf-8"))
            return

        # 3. Standard Intake Submission (from contact form)
        elif self.path in ("/api/intake", "/api/v1/contact"):
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

            try:
                with open(MESSAGES_FILE, "a", encoding="utf-8") as f:
                    f.write(json.dumps(record, ensure_ascii=False) + "\n")
            except Exception as e:
                print(f"[ERROR] Failed to write to {MESSAGES_FILE}: {e}", file=sys.stderr)

            # Mirror to Cockpit
            cockpit_file = os.path.join(COCKPIT_DATA_DIR, "messages.jsonl")
            try:
                with open(cockpit_file, "a", encoding="utf-8") as f:
                    f.write(json.dumps(record, ensure_ascii=False) + "\n")
            except Exception:
                pass

            print(f"[INTAKE] New message: ticket={claim_ticket}, email={client_email}, title={title[:40]}")

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
            return

        else:
            self.send_response(404)
            self.end_headers()

def run():
    server_address = ("0.0.0.0", PORT)
    httpd = ThreadedHTTPServer(server_address, IntakeHandler)
    print(f"Dmitry Efremov Intake & Mini-CRM Service listening on 0.0.0.0:{PORT}")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
    httpd.server_close()

if __name__ == "__main__":
    run()
