export type CanonDomain =
  | 'AUTONOMOUS_ENGINEERING'
  | 'AGENTIC_ECONOMY'
  | 'PRAGMATIC_BUSINESS_STRATEGY'
  | 'PHILOSOPHY_OF_MIND';

export type CanonType =
  | 'MANIFESTO'
  | 'GLOSSARY_TERM'
  | 'ONTOLOGY';

export type CanonRelationshipType =
  | 'REFERENCES'
  | 'SUPERSEDES'
  | 'RELATED'
  | 'DEPENDS_ON'
  | 'EXTENDS';

export interface CanonCrossReference {
  id?: number;
  sourceSlug: string;
  sourceTitle?: string;
  targetSlug: string;
  targetTitle?: string;
  relationshipType: CanonRelationshipType;
  description?: string;
  createdAt?: string;
}

export interface CanonItem {
  id?: number;
  slug: string;
  title: string;
  type: CanonType;
  domain: CanonDomain;
  summary?: string;
  content: string;
  publishedAt?: string;
  createdAt?: string;
  updatedAt?: string;
  outgoingReferences?: CanonCrossReference[];
  incomingReferences?: CanonCrossReference[];
}

export interface CanonPage {
  content: CanonItem[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export const DOMAIN_LABELS: Record<CanonDomain, string> = {
  AUTONOMOUS_ENGINEERING: 'Autonomous Engineering',
  AGENTIC_ECONOMY: 'Agentic Economy (A2A)',
  PRAGMATIC_BUSINESS_STRATEGY: 'Pragmatic Business Strategy',
  PHILOSOPHY_OF_MIND: 'Philosophy of Mind'
};

export const TYPE_LABELS: Record<CanonType, string> = {
  MANIFESTO: 'Evergreen Manifesto',
  GLOSSARY_TERM: 'Glossary Term',
  ONTOLOGY: 'Ontology'
};

export const DEFAULT_CANON_ITEMS: CanonItem[] = [
  {
    id: 101,
    slug: 'architecture-of-autonomous-systems',
    title: 'The Architecture of Autonomous Systems',
    type: 'MANIFESTO',
    domain: 'AUTONOMOUS_ENGINEERING',
    summary: 'Evergreen principles for engineering self-governing software agents and deterministic control loops.',
    publishedAt: '2026-10-12T12:00:00Z',
    createdAt: '2026-10-12T12:00:00Z',
    content: `# The Architecture of Autonomous Systems

## Introduction

Autonomous software engineering requires deterministic verification, declarative state boundaries, and resilient fallback mechanisms. When software systems operate without continuous human intervention, the burden of safety and correctness shifts entirely onto normative state invariants.

> "Your code and your decisions are who you truly are."

## Core Principles

1. **Deterministic State Determinism**: Every agent iteration must yield identical results given identical state seeds and inputs.
2. **Deontic Boundaries**: Formal specification of obligations, permissions, and prohibitions before state transition commits.
3. **Black Box Isolation**: Internal factory or orchestration mechanics must remain isolated from client-facing facades.`,
    outgoingReferences: [
      {
        id: 1,
        sourceSlug: 'architecture-of-autonomous-systems',
        sourceTitle: 'The Architecture of Autonomous Systems',
        targetSlug: 'extended-mind-infrastructure',
        targetTitle: 'Extended Mind Infrastructure',
        relationshipType: 'REFERENCES',
        description: 'Autonomous systems rely on extended mind infrastructure for agent state persistence.'
      }
    ],
    incomingReferences: [
      {
        id: 2,
        sourceSlug: 'extended-cognition-deontic-action',
        sourceTitle: 'Extended Cognition & Deontic Action',
        targetSlug: 'architecture-of-autonomous-systems',
        targetTitle: 'The Architecture of Autonomous Systems',
        relationshipType: 'EXTENDS',
        description: 'Deontic philosophy provides the normative framework for autonomous engineering architecture.'
      }
    ]
  },
  {
    id: 102,
    slug: 'extended-mind-infrastructure',
    title: 'Extended Mind Infrastructure',
    type: 'GLOSSARY_TERM',
    domain: 'AUTONOMOUS_ENGINEERING',
    summary: 'Computational storage and state mechanisms operating as seamless cognitive extensions of autonomous software agents.',
    publishedAt: '2026-10-12T12:00:00Z',
    createdAt: '2026-10-12T12:00:00Z',
    content: `# Extended Mind Infrastructure

## Definition

Based on Clark & Chalmers (1998) extended mind thesis, extended mind infrastructure refers to computational storage, vector caches, and state channels that function as active constitutive parts of an autonomous cognitive agent.

## Key Characteristics

- **Zero-Latency State Access**: Instant retrieval of historical reasoning contexts.
- **Audited Provenance**: Epistemic traceability for all state mutations.
- **Isomorphic Mapping**: Direct structural alignment between external representation and internal belief states.`,
    outgoingReferences: [],
    incomingReferences: [
      {
        id: 1,
        sourceSlug: 'architecture-of-autonomous-systems',
        sourceTitle: 'The Architecture of Autonomous Systems',
        targetSlug: 'extended-mind-infrastructure',
        targetTitle: 'Extended Mind Infrastructure',
        relationshipType: 'REFERENCES',
        description: 'Autonomous systems rely on extended mind infrastructure for agent state persistence.'
      }
    ]
  },
  {
    id: 103,
    slug: 'agent-to-agent-economic-protocols',
    title: 'Agent-to-Agent Economic Protocols',
    type: 'MANIFESTO',
    domain: 'AGENTIC_ECONOMY',
    summary: 'Foundational economics governing automated transactions, resource exchange, and cryptographic trust between autonomous agents.',
    publishedAt: '2026-10-12T12:00:00Z',
    createdAt: '2026-10-12T12:00:00Z',
    content: `# Agent-to-Agent Economic Protocols

## Executive Summary

In an agentic economy (A2A), software agents act as autonomous economic actors. They negotiate service level agreements, evaluate computational cost functions, and transact value without human manual intervention.

## Architectural Pillars

- **Machine-Readable API Contracts**: High-density OpenAPI specifications and semantic schemas (\`llms.txt\`, \`ai-agent.json\`).
- **Verifiable SLAs**: Automated cryptographic proof of work and contract execution.
- **Trust-Minimized Exchange**: Idempotent settlement protocols with zero single-point-of-failure vulnerabilities.`,
    outgoingReferences: [
      {
        id: 3,
        sourceSlug: 'agent-to-agent-economic-protocols',
        sourceTitle: 'Agent-to-Agent Economic Protocols',
        targetSlug: 'pragmatic-business-strategy-manifesto',
        targetTitle: 'Pragmatic Business Strategy for the Agent Era',
        relationshipType: 'RELATED',
        description: 'Economic protocols directly inform strategic business alignment in automated markets.'
      }
    ],
    incomingReferences: []
  },
  {
    id: 104,
    slug: 'pragmatic-business-strategy-manifesto',
    title: 'Pragmatic Business Strategy for the Agent Era',
    type: 'MANIFESTO',
    domain: 'PRAGMATIC_BUSINESS_STRATEGY',
    summary: 'Navigating market transformation, throughput bottlenecks, and value creation when AI agents become primary business interfaces.',
    publishedAt: '2026-10-12T12:00:00Z',
    createdAt: '2026-10-12T12:00:00Z',
    content: `# Pragmatic Business Strategy for the Agent Era

## Strategic Thesis

Traditional business strategy focuses on human customer friction and sales funnels. In contrast, pragmatic business strategy in the agentic era prioritizes machine-consumable endpoints, deterministic SLA guarantees, and radical operational efficiency.

## Goldratt Theory of Constraints in Strategic Alignment

1. **Identify the Real Constraint**: Uncover bottlenecks in processing client requests and AI agent queries.
2. **Eliminate Cognitive Noise**: Ensure zero unnecessary steps in onboarding and execution.
3. **Elevate Pipeline Capacity**: Deploy high-throughput API layers for external machine callers.`,
    outgoingReferences: [],
    incomingReferences: [
      {
        id: 3,
        sourceSlug: 'agent-to-agent-economic-protocols',
        sourceTitle: 'Agent-to-Agent Economic Protocols',
        targetSlug: 'pragmatic-business-strategy-manifesto',
        targetTitle: 'Pragmatic Business Strategy for the Agent Era',
        relationshipType: 'RELATED',
        description: 'Economic protocols directly inform strategic business alignment in automated markets.'
      }
    ]
  },
  {
    id: 105,
    slug: 'extended-cognition-deontic-action',
    title: 'Extended Cognition & Deontic Action',
    type: 'MANIFESTO',
    domain: 'PHILOSOPHY_OF_MIND',
    summary: 'Philosophical framework connecting Andy Clark extended mind theory with deontic logic in software systems.',
    publishedAt: '2026-10-12T12:00:00Z',
    createdAt: '2026-10-12T12:00:00Z',
    content: `# Extended Cognition & Deontic Action

## Philosophical Foundation

Action in software systems is not mere execution; it carries normative weight. Deontic logic models the fundamental categories of obligation ($\mathcal{O}$), permission ($\mathcal{P}$), and prohibition ($\mathcal{F}$).

$$\mathcal{O}(\varphi) \iff \neg \mathcal{P}(\neg \varphi)$$

## Epistemic Supervenience

The internal mental and computational states of an agent supervene on physical and environmental invariants. Clean codebases exhibit moral and logical supervenience: no hidden side effects, no unhandled invariants.`,
    outgoingReferences: [
      {
        id: 2,
        sourceSlug: 'extended-cognition-deontic-action',
        sourceTitle: 'Extended Cognition & Deontic Action',
        targetSlug: 'architecture-of-autonomous-systems',
        targetTitle: 'The Architecture of Autonomous Systems',
        relationshipType: 'EXTENDS',
        description: 'Deontic philosophy provides the normative framework for autonomous engineering architecture.'
      }
    ],
    incomingReferences: []
  }
];

export async function fetchCanonItems(
  domain?: CanonDomain,
  type?: CanonType
): Promise<CanonItem[]> {
  try {
    const params = new URLSearchParams();
    if (domain) params.append('domain', domain);
    if (type) params.append('type', type);

    const url = `/api/v1/canon?${params.toString()}`;
    const response = await fetch(url);
    if (!response.ok) {
      return filterLocalCanonItems(domain, type);
    }
    const data: CanonPage = await response.json();
    if (!data || !Array.isArray(data.content) || data.content.length === 0) {
      return filterLocalCanonItems(domain, type);
    }
    return data.content;
  } catch {
    return filterLocalCanonItems(domain, type);
  }
}

export function filterLocalCanonItems(
  domain?: CanonDomain,
  type?: CanonType
): CanonItem[] {
  return DEFAULT_CANON_ITEMS.filter((item) => {
    if (domain && item.domain !== domain) return false;
    if (type && item.type !== type) return false;
    return true;
  });
}
