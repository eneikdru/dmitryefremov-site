# ADR-003: Headless API Security & Content Boundary Architecture

**Status**: APPROVED / DECISION RECORDED
**Owner Role**: BARCAN-TAG-09 (Delivery Management & Systems Analysis)
**Target Handoff Role**: BARCAN-TAG-07 (Security & Infrastructure / Backend Implementation)
**Date**: 2026-10-12

---

## 1. Context & Objective

The application supports autonomous content management by external AI agents (e.g. ChatGPT, Claude, custom agents) across public content channels (podcasts, articles, release topics, distribution links).

### Core Architectural Requirement
AI agent access must be strictly isolated to the **content domain** (`/api/v1/content/**`, `/api/v1/articles/**`, `/api/v1/podcasts/**`). External AI agents MUST NOT under any circumstances access, inspect, modify, or execute:
- Application codebase or source files
- UI templates, Svelte components, or CSS styles
- Underlying server infrastructure, environment secrets, or host filesystem

---

## 2. API Token Management & Revocation Strategy

To enforce least-privilege access for external AI agents, authentication and token lifecycle are defined as follows:

### 2.1 Token Format and Scoping
- **Token Format**: High-entropy bearer tokens with prefix (`df_agent_live_<secret>`).
- **Granular Scopes**: Tokens carry explicit content-only scopes:
  - `SCOPE_CONTENT_READ`: Read-only access to published content.
  - `SCOPE_CONTENT_WRITE`: Authoring/updating draft and published content (articles, podcast episodes, timecodes, schedule entries).
- **Scope Exclusion**: Agent tokens are explicitly forbidden from receiving administrative, user management, or infrastructure scopes (`SCOPE_ADMIN`, `SCOPE_SYSTEM`, `SCOPE_INFRA`).

### 2.2 Storage & Security
- **One-Way Hashing**: Secret token values are hashed using SHA-256 (or Argon2id) before persistence in the `agent_api_tokens` database table. Raw secrets are shown exactly once at generation time.
- **Metadata**: Each token entry stores `id`, `agent_name`, `token_hash`, `scopes`, `status` (`ACTIVE`, `SUSPENDED`, `REVOKED`), `created_at`, `expires_at`, `rate_limit_rpm`.

### 2.3 Expiration & Rotation
- Default TTL for AI agent tokens is 90 days.
- Automated expiration enforcement checks `expires_at` during bearer token validation.

### 2.4 Revocation Mechanism
- **Instant Database Revocation**: Status change (`UPDATE agent_api_tokens SET status = 'REVOKED' WHERE id = ? AND status = 'ACTIVE'`).
- **In-Memory Cache Invalidation**: Spring Security filter maintains an in-memory revoked token cache (or Caffeine/Redis LRU cache with TTL) to reject revoked tokens immediately with HTTP `401 Unauthorized` without database latency.

---

## 3. Payload Validation & Infrastructure Boundary Protection

To prevent payloads with malicious data (XSS, SSTI, SQL injection, path traversal, shell injection) from compromising the application or host environment, four validation layers are enforced:

```
[ External AI Agent ]
         │
         ▼
 ┌─────────────────────────────────────────────────────────────┐
 │ Layer 1: Schema & DTO Bean Validation                        │
 │  - Jackson strict mapping (FAIL_ON_UNKNOWN_PROPERTIES)      │
 │  - Spring @Valid, @NotNull, @Size, Regex constraints        │
 └──────────────────────────────┬──────────────────────────────┘
                                │
                                ▼
 ┌─────────────────────────────────────────────────────────────┐
 │ Layer 2: Content Sanitization & Safe Parsing                │
 │  - OWASP HTML / Jsoup Whitelist Sanitization                │
 │  - Markdown parsing with disabled raw HTML execution        │
 │  - SSTI / Script execution prevention                       │
 └──────────────────────────────┬──────────────────────────────┘
                                │
                                ▼
 ┌─────────────────────────────────────────────────────────────┐
 │ Layer 3: System Path & Resource Isolation                   │
 │  - Strict UUID / slug route identification (^[a-z0-9-]+$)   │
 │  - Path traversal rejection (../, %2e%2e blocking)          │
 │  - Zero host filesystem or process execution permissions    │
 └──────────────────────────────┬──────────────────────────────┘
                                │
                                ▼
 ┌─────────────────────────────────────────────────────────────┐
 │ Layer 4: Persistence Layer Isolation                        │
 │  - Spring Data JPA Parameterized Queries (Anti-SQLi)        │
 │  - Content entity scope boundary (Article, Podcast only)   │
 └─────────────────────────────────────────────────────────────┘
```

### Layer 1: Schema & DTO Validation
- All incoming JSON payloads map to strict Java DTOs.
- Jackson configuration enforces `DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES = true` to prevent mass-assignment or context injection.
- DTO fields are validated via Jakarta Bean Validation (`@NotBlank`, `@Size(max = ...)`).

### Layer 2: Content Sanitization & Safe Parsing
- Markdown bodies are passed through safe parsing pipelines (CommonMark / Flexmark).
- Raw HTML within Markdown is sanitized using OWASP HTML Sanitizer / Jsoup with a strict safe-list (p, b, i, code, pre, ul, ol, li, blockquote, a[href], img[src,alt]).
- All `<script>`, `<iframe>`, `<style>`, event handlers (`onload=`, `onerror=`), and `javascript:` URIs are completely stripped.
- Dynamic evaluation/SSTI syntax is strictly escaped.

### Layer 3: System Path & Resource Isolation
- Resource parameters (e.g. article slug or episode ID) are strictly validated against regex `^[a-z0-9-]+$` or UUID format.
- Path traversal sequences (`../`, `..\`, `%2e%2e`, `/etc/`, `/var/`) trigger instant HTTP `400 Bad Request` before route handling.
- Content APIs have no interface or authority to write to local system disks or spawn process handlers.

### Layer 4: Persistence Layer Isolation
- All database queries use Spring Data JPA ORM with parameterized SQL queries, eliminating SQL injection.
- Database credentials for the API runtime have access scoped exclusively to content tables (`articles`, `podcast_episodes`, `podcast_timecodes`, `publication_schedule`, `agent_api_tokens`).

---

## 4. Delivery Handoff Decision & Next Steps

### 4.1 Handoff Summary
- **Current Role**: BARCAN-TAG-09 (Delivery Management & Technical Product Manager)
- **Next Concrete Owner Role**: `BARCAN-TAG-07` (Security & Backend Engineering)
- **Target Deliverable**: Implementation of Headless API Security Filter and DTO Sanitization based on this ADR.

### 4.2 Concrete Implementation Tasks for BARCAN-TAG-07
1. Add `agent_api_tokens` Flyway schema migration script using reserved version `V20261009164806795`.
2. Implement `AgentApiKeyAuthenticationFilter` and Spring Security token verification for `/api/v1/content/**`.
3. Add `ContentSanitizerService` using Jsoup/OWASP HTML sanitizer for incoming Markdown/JSON content DTOs.
4. Add automated test coverage verifying valid token access, revoked token rejection (`401`), and malicious payload sanitization.
