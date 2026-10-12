# High-End Dark UI/UX Design System Specification

## Overview

This document specifies the deterministic UI/UX Design System generated via Stitch for Dmitry Efremov's personal platform (`dmitryefremov.com`). The design system establishes a Modern Swiss Editorial / Technical Brutalism aesthetic with High-End Dark framing, precise typography, and zero-bloat UI components.

---

## 1. Visual Identity & Color Palette

### 1.1 Dark Void Palette (Default Theme)
- **Background Root (`dark-void`):** `#090a0f`
- **Surface / Card Background (`surface-dark`):** `#12151e`
- **Border / Divider (`border-dark`):** `#1f2433`
- **Primary Text (`text-dark-primary`):** `#e2e8f0`
- **Secondary Text / Muted (`text-dark-muted`):** `#94a3b8`

### 1.2 Light Monochrome Palette (High-Contrast Alternative)
- **Background Root (`light-monochrome`):** `#f8fafc`
- **Surface / Card Background (`surface-light`):** `#f1f5f9`
- **Border / Divider (`border-light`):** `#cbd5e1`
- **Primary Text (`text-light-primary`):** `#0f172a`
- **Secondary Text / Muted (`text-light-muted`):** `#64748b`

### 1.3 System Accents & Signals
- **Cyan Accent (Core / Active States):** `#38bdf8`
- **Emerald Accent (Success / Active Status):** `#34d399`
- **Rose Accent (Terminal / High Priority):** `#f43f5e`

---

## 2. Typography System

### 2.1 Grotesque Display Headers
- **Font Family:** Modern Sans-Serif Grotesque (`Inter`, `Geist Display`, `system-ui`, `-apple-system`, `BlinkMacSystemFont`, `sans-serif`)
- **Weights:** Bold (700), Extra Bold (800)
- **Tracking:** Tight (`letter-spacing: -0.025em`)
- **Usage:** Main hero title, article headers, section headings (`H1`, `H2`, `H3`).

### 2.2 Comfortable Essay Typography (Reader View)
- **Font Family:** High-Legibility Serif (`Georgia`, `Cambria`, `"Times New Roman"`, `Times`, `serif`)
- **Font Size:** `16px` (`1rem`) on mobile, `18px` (`1.125rem`) on desktop.
- **Line Height:** `1.65` to `1.75` for relaxed, low-eyestrain continuous reading.
- **Color:** `#cbd5e1` on `#12151e` background (Contrast ratio > 7:1, exceeds WCAG 2.1 AA).
- **Blockquotes:** Left-bordered with `#38bdf8` accent, italicized serif text, padded surface box.

### 2.3 System Monospace Badges & Telemetry Tags
- **Font Family:** Monospace (`JetBrains Mono`, `ui-monospace`, `SFMono-Regular`, `Menlo`, `Monaco`, `Consolas`, `monospace`)
- **Font Size:** `11px` to `12px` (`0.75rem` to `0.875rem`)
- **Usage:** Telemetry tags (`[SYS_CORE]`, `[THOUGHT_STREAM]`, `[PODCAST_MEDIA]`, `[SYS_TERMINAL]`), platform badges (`[TG_CORE]`, `[YT_MEDIA]`, `[GH_SRC]`), timestamps, and status indicators.

---

## 3. Aesthetic Contact Card Architecture

The Contact & Communications Terminal is designed as a structured matrix card:
- **Card Container:** `#12151e` surface background with `#1f2433` border and rounded corners.
- **Platform Badge:** Monospace tag with distinct color accent (`[TG_CORE]`, `[YT_MEDIA]`, `[GH_SRC]`, `[LI_PROF]`, `[EM_DIRECT]`).
- **Interactive States:** Subtle hover transition expanding border glow to `#38bdf8` or `#f43f5e`.
- **Fitts's Law Compliance:** Touch target dimensions on mobile meet or exceed `44px` height with explicit spacing (`gap: 0.75rem`).
- **Outbound Click Indicator:** Visual directional arrow (`↗`) and real-time privacy-preserving telemetry tracker.

---

## 4. Stitch Mockup Reference File

The deterministic HTML/CSS mockup reference file generated for developer implementation is committed at:
`frontend/public/stitch-mockup.html`

This reference file contains the complete, self-contained layout representing all UI states, grotesque headers, essay reader typography, podcast player section, and aesthetic contact card.
