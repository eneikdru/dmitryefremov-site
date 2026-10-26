#!/usr/bin/env python3
"""
Direct Contact, Intake Message & Mini-CRM Service for Dmitry Efremov Personal Platform.
Secures CRM data with authentication restricted to dmitrii.eneik@gmail.com.
"""

import os
import sys
import json
import time
import uuid
import datetime
import urllib.parse
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
AUTH_FILE = os.path.join(SITE_DATA_DIR, "crm_auth.json")

# Master password for Dmitry (can be overridden via env)
DEFAULT_MASTER_KEY = os.environ.get("DMITRY_CRM_KEY", "efremov2026")

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

def get_active_tokens():
    if os.path.exists(AUTH_FILE):
        try:
            with open(AUTH_FILE, "r", encoding="utf-8") as f:
                return json.load(f)
        except Exception:
            pass
    return {}

def save_active_token(token, email="dmitrii.eneik@gmail.com"):
    tokens = get_active_tokens()
    tokens[token] = {
        "email": email,
        "createdAt": datetime.datetime.now(datetime.timezone.utc).isoformat()
    }
    try:
        with open(AUTH_FILE, "w", encoding="utf-8") as f:
            json.dump(tokens, f, ensure_ascii=False, indent=2)
    except Exception as e:
        print(f"[ERROR] Saving auth token: {e}", file=sys.stderr)

def is_authorized(headers, client_ip):
    # Allow localhost test runs
    if client_ip in ("127.0.0.1", "::1", "localhost"):
        return True

    auth_header = headers.get("Authorization", "")
    token = ""
    if auth_header.startswith("Bearer "):
        token = auth_header[7:].strip()
    elif "X-CRM-Key" in headers:
        key = headers.get("X-CRM-Key", "").strip()
        if key == DEFAULT_MASTER_KEY:
            return True

    # Check cookies
    cookie_header = headers.get("Cookie", "")
    if "crm_token=" in cookie_header:
        for c in cookie_header.split(";"):
            c = c.strip()
            if c.startswith("crm_token="):
                token = c.split("=", 1)[1].strip()

    if token:
        tokens = get_active_tokens()
        if token in tokens:
            return True
        # If token is the master key itself
        if token == DEFAULT_MASTER_KEY:
            return True

    return False

def get_aggregated_crm_data():
    messages = read_jsonl(MESSAGES_FILE)
    notes = read_jsonl(NOTES_FILE)
    clients_meta = read_clients_meta()

    clients_dict = {}

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

    clients_list = list(clients_dict.values())
    clients_list.sort(key=lambda c: c["lastActivity"], reverse=True)

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
        self.send_header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, X-CRM-Key")

    def do_OPTIONS(self):
        self.send_response(204)
        self._send_cors_headers()
        self.end_headers()

    def do_GET(self):
        parsed = urllib.parse.urlparse(self.path)
        path = parsed.path
        query_params = urllib.parse.parse_qs(parsed.query)

        if path in ("/api/v1/canon/context", "/api/v1/agent/slice", "/api/canon/context"):
            task_query = query_params.get("task", [""])[0] or query_params.get("q", ["general architectural task"])[0]
            budget_str = query_params.get("budget", query_params.get("token_budget", ["1500"]))[0]
            try:
                budget = int(budget_str)
            except Exception:
                budget = 1500

            format_type = query_params.get("format", ["markdown"])[0].lower()

            try:
                from philosopher_synthesizer import synthesizer
                result = synthesizer.synthesize_context(task_query, token_budget=budget)
                self.send_response(200)
                self._send_cors_headers()

                if format_type == "json":
                    self.send_header("Content-Type", "application/json; charset=utf-8")
                    self.end_headers()
                    self.wfile.write(json.dumps(result, ensure_ascii=False).encode("utf-8"))
                else:
                    self.send_header("Content-Type", "text/markdown; charset=utf-8; variant=efremov")
                    self.end_headers()
                    self.wfile.write(result["markdown"].encode("utf-8"))
            except Exception as e:
                self.send_response(500)
                self._send_cors_headers()
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.end_headers()
                self.wfile.write(json.dumps({"error": str(e)}).encode("utf-8"))
            return

        if path.rstrip("/") in ("/api", "/api/v1", "/api/docs", "/api/spec") or path in ("/api/openapi.json",):
            openapi_spec = {
                "openapi": "3.0.3",
                "info": {
                    "title": "Dmitry Efremov Systems Architecture Hub & Agent Gateway API",
                    "version": "1.2.0",
                    "description": "Canonical machine-readable interfaces for AI agents (Google Jules, Claude Code, Cursor, Codex) to synthesize tailored architectural invariants via Spreading Activation and submit inquiries."
                },
                "servers": [
                    {"url": "https://dmitryefremov.com", "description": "Production Gateway"}
                ],
                "paths": {
                    "/api/v1/canon/context": {
                        "get": {
                            "summary": "Synthesize tailored architectural invariants via Spreading Activation (lambda=0.70)",
                            "parameters": [
                                {
                                    "name": "task",
                                    "in": "query",
                                    "required": True,
                                    "schema": {"type": "string"},
                                    "description": "Technical task description (e.g. 'stripe payments webhook idempotency' or 'master schedule in tbilisi tz')"
                                },
                                {
                                    "name": "budget",
                                    "in": "query",
                                    "required": False,
                                    "schema": {"type": "integer", "default": 1500},
                                    "description": "Token budget cap for invariant slice"
                                },
                                {
                                    "name": "format",
                                    "in": "query",
                                    "required": False,
                                    "schema": {"type": "string", "enum": ["markdown", "json"], "default": "markdown"},
                                    "description": "Response format: 'markdown' (ready for agent prompt) or 'json' (with node weights)"
                                }
                            ],
                            "responses": {
                                "200": {
                                    "description": "Tailored Invariant Slice with Deontic Gates and Verification Obligations"
                                }
                            }
                        }
                    },
                    "/api/intake": {
                        "post": {
                            "summary": "Submit inquiry or architectural consulting request to Dmitry Efremov",
                            "requestBody": {
                                "required": True,
                                "content": {
                                    "application/json": {
                                        "schema": {
                                            "type": "object",
                                            "properties": {
                                                "name": {"type": "string"},
                                                "clientEmail": {"type": "string"},
                                                "title": {"type": "string"},
                                                "narrative": {"type": "string"}
                                            },
                                            "required": ["narrative"]
                                        }
                                    }
                                }
                            },
                            "responses": {
                                "201": {
                                    "description": "Message registered and claim ticket generated"
                                }
                            }
                        }
                    },
                    "/health": {
                        "get": {
                            "summary": "Service health check",
                            "responses": {
                                "200": {"description": "Service is UP"}
                            }
                        }
                    }
                }
            }
            self.send_response(200)
            self._send_cors_headers()
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.end_headers()
            self.wfile.write(json.dumps(openapi_spec, ensure_ascii=False, indent=2).encode("utf-8"))
            return

        if self.path in ("/health", "/api/health"):
            self.send_response(200)
            self._send_cors_headers()
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.end_headers()
            self.wfile.write(b'{"status":"UP","service":"dmitryefremov-intake-crm"}')
        elif self.path in ("/api/v1/contact", "/api/intake"):
            # Public summary for tests / intake status (limited summary)
            messages = read_jsonl(MESSAGES_FILE)
            self.send_response(200)
            self._send_cors_headers()
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.end_headers()
            self.wfile.write(json.dumps({"total": len(messages), "messages": messages[-20:]}, ensure_ascii=False).encode("utf-8"))
        elif self.path in ("/api/crm", "/api/crm/clients"):
            # Strictly protected CRM endpoint
            if not is_authorized(self.headers, self.client_address[0]):
                self.send_response(401)
                self._send_cors_headers()
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.end_headers()
                self.wfile.write(b'{"error":"Unauthorized: Access restricted to dmitrii.eneik@gmail.com"}')
                return

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

        # 0. CRM Login / Authentication
        if self.path == "/api/crm/login":
            key = (body.get("key") or body.get("password") or "").strip()
            if key == DEFAULT_MASTER_KEY:
                token = uuid.uuid4().hex
                save_active_token(token, "dmitrii.eneik@gmail.com")
                self.send_response(200)
                self._send_cors_headers()
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.send_header("Set-Cookie", f"crm_token={token}; Path=/; Max-Age=2592000; SameSite=Lax")
                self.end_headers()
                self.wfile.write(json.dumps({"success": True, "token": token, "email": "dmitrii.eneik@gmail.com"}).encode("utf-8"))
            else:
                self.send_response(403)
                self._send_cors_headers()
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.end_headers()
                self.wfile.write(b'{"error":"Invalid access key"}')
            return

        # 1. Add Note or Agreement (Protected)
        elif self.path == "/api/crm/note":
            if not is_authorized(self.headers, self.client_address[0]):
                self.send_response(401)
                self._send_cors_headers()
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.end_headers()
                self.wfile.write(b'{"error":"Unauthorized: Access restricted to dmitrii.eneik@gmail.com"}')
                return

            client_email = (body.get("clientEmail") or "").strip().lower()
            text = (body.get("text") or "").strip()
            note_type = body.get("type") or "agreement"

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

        # 2. Update Client Status or Metadata (Protected)
        elif self.path == "/api/crm/status":
            if not is_authorized(self.headers, self.client_address[0]):
                self.send_response(401)
                self._send_cors_headers()
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.end_headers()
                self.wfile.write(b'{"error":"Unauthorized: Access restricted to dmitrii.eneik@gmail.com"}')
                return

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

        # 2b. Delete Client or Purge All Clients (Protected)
        elif self.path in ("/api/crm/delete_client", "/api/crm/client/delete"):
            if not is_authorized(self.headers, self.client_address[0]):
                self.send_response(401)
                self._send_cors_headers()
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.end_headers()
                self.wfile.write(b'{"error":"Unauthorized: Access restricted to dmitrii.eneik@gmail.com"}')
                return

            client_email = (body.get("clientEmail") or "").strip().lower()
            purge_all = bool(body.get("purgeAll", False))

            if purge_all:
                with open(MESSAGES_FILE, "w", encoding="utf-8") as f:
                    pass
                with open(NOTES_FILE, "w", encoding="utf-8") as f:
                    pass
                save_clients_meta({})
                print("[CRM] Purged all clients, messages, and notes")
                self.send_response(200)
                self._send_cors_headers()
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.end_headers()
                self.wfile.write(json.dumps({"success": True, "message": "Все клиенты и переписки очищены"}, ensure_ascii=False).encode("utf-8"))
                return

            if not client_email:
                self.send_response(400)
                self._send_cors_headers()
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.end_headers()
                self.wfile.write(b'{"error":"clientEmail is required"}')
                return

            all_msgs = read_jsonl(MESSAGES_FILE)
            kept_msgs = [m for m in all_msgs if (m.get("clientEmail") or "anonymous").strip().lower() != client_email]
            with open(MESSAGES_FILE, "w", encoding="utf-8") as f:
                for m in kept_msgs:
                    f.write(json.dumps(m, ensure_ascii=False) + "\n")

            all_notes = read_jsonl(NOTES_FILE)
            kept_notes = [n for n in all_notes if (n.get("clientEmail") or "anonymous").strip().lower() != client_email]
            with open(NOTES_FILE, "w", encoding="utf-8") as f:
                for n in kept_notes:
                    f.write(json.dumps(n, ensure_ascii=False) + "\n")

            meta = read_clients_meta()
            if client_email in meta:
                del meta[client_email]
                save_clients_meta(meta)

            print(f"[CRM] Deleted client {client_email}")

            self.send_response(200)
            self._send_cors_headers()
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.end_headers()
            self.wfile.write(json.dumps({"success": True, "deleted": client_email}, ensure_ascii=False).encode("utf-8"))
            return

        # 2c. Smart CRM Client Summary (Protected)
        elif self.path == "/api/crm/summary":
            if not is_authorized(self.headers, self.client_address[0]):
                self.send_response(401)
                self._send_cors_headers()
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.end_headers()
                self.wfile.write(b'{"error":"Unauthorized: Access restricted to dmitrii.eneik@gmail.com"}')
                return

            client_email = (body.get("clientEmail") or "").strip().lower()
            if not client_email:
                self.send_response(400)
                self._send_cors_headers()
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.end_headers()
                self.wfile.write(b'{"error":"clientEmail is required"}')
                return

            crm_data = get_aggregated_crm_data()
            client = None
            for c in crm_data.get("clients", []):
                if c["email"].lower() == client_email:
                    client = c
                    break

            if not client:
                self.send_response(404)
                self._send_cors_headers()
                self.send_header("Content-Type", "application/json; charset=utf-8")
                self.end_headers()
                self.wfile.write(b'{"error":"Client not found"}')
                return

            msgs = client.get("messages", [])
            notes = client.get("notes", [])
            agreements = [n for n in notes if n.get("type") == "agreement"]
            internal_notes = [n for n in notes if n.get("type") == "note"]

            status_map = {
                "NEW": "Новый клиент (требует первичного ответа)",
                "IN_DISCUSSION": "В активном обсуждении",
                "AGREED": "Договорённость достигнута (в работе)",
                "ARCHIVED": "В архиве (завершён или отложен)"
            }
            status_text = status_map.get(client.get("status"), client.get("status"))

            summary_parts = []
            summary_parts.append(f"📌 **Текущий статус:** {status_text}")

            if msgs:
                topics = []
                for m in msgs:
                    t = m.get("title") or "Обращение с сайта"
                    desc = m.get("narrative") or ""
                    snippet = f"«{t}»" + (f": {desc[:120]}..." if len(desc) > 120 else f": {desc}" if desc else "")
                    topics.append(snippet)
                summary_parts.append(f"📬 **Суть обращений ({len(msgs)} сообщений):**\n" + "\n".join(f"• {t}" for t in topics))
            else:
                summary_parts.append("📬 **Обращения:** Прямых сообщений с формы не зафиксировано.")

            if agreements:
                summary_parts.append(f"🤝 **Зафиксированные договорённости ({len(agreements)}):**\n" + "\n".join(f"• {a.get('text')} ({a.get('createdAt', '')[:10]})" for a in agreements))
            else:
                summary_parts.append("🤝 **Договорённости:** Пока нет зафиксированных договорённостей. Нажмите кнопку «🤝 Договорённость», чтобы закрепить условия.")

            if internal_notes:
                summary_parts.append(f"📝 **Личные заметки ({len(internal_notes)}):**\n" + "\n".join(f"• {n.get('text')}" for n in internal_notes))

            if client.get("status") == "NEW":
                next_step = "Ответить клиенту по email и согласовать детали."
            elif client.get("status") == "IN_DISCUSSION":
                next_step = "Согласовать условия и зафиксировать договорённость."
            elif client.get("status") == "AGREED":
                next_step = "Выполнение проекта / обязательств."
            else:
                next_step = "Клиент находится в архиве."
            summary_parts.append(f"🎯 **Рекомендуемый следующий шаг:** {next_step}")

            full_summary = "\n\n".join(summary_parts)

            self.send_response(200)
            self._send_cors_headers()
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.end_headers()
            self.wfile.write(json.dumps({
                "success": True,
                "clientEmail": client_email,
                "summary": full_summary,
                "totalMessages": len(msgs),
                "totalAgreements": len(agreements),
                "status": client.get("status")
            }, ensure_ascii=False).encode("utf-8"))
            return

        # 3. Standard Intake Submission (from contact form - Public)
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
    print(f"Dmitry Efremov Intake & Secured Mini-CRM Service listening on 0.0.0.0:{PORT}")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
    httpd.server_close()

if __name__ == "__main__":
    run()
