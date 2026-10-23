export type CanonDomain =
  | 'AUTONOMOUS_ENGINEERING'
  | 'AGENTIC_ECONOMY'
  | 'PRAGMATIC_BUSINESS_STRATEGY'
  | 'PHILOSOPHY_OF_MIND';

export type CanonType = 'MANIFESTO' | 'GLOSSARY_TERM' | 'ONTOLOGY';

export type CanonRelationshipType =
  | 'REFERENCES'
  | 'SUPERSEDES'
  | 'RELATED'
  | 'DEPENDS_ON'
  | 'EXTENDS';

export interface CanonCrossReference {
  id: number;
  sourceSlug: string;
  sourceTitle: string;
  targetSlug: string;
  targetTitle: string;
  relationshipType: CanonRelationshipType;
  description?: string | null;
  createdAt: string;
}

export interface CanonItem {
  id: number;
  slug: string;
  title: string;
  type: CanonType;
  domain: CanonDomain;
  summary?: string | null;
  content: string;
  publishedAt?: string | null;
  createdAt: string;
  updatedAt?: string | null;
  outgoingReferences: CanonCrossReference[];
  incomingReferences: CanonCrossReference[];
}

export interface CanonPage {
  content: CanonItem[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export const CANON_DOMAINS: { id: CanonDomain; label: string; description: string }[] = [
  {
    id: 'AUTONOMOUS_ENGINEERING',
    label: 'Autonomous Engineering',
    description: 'Deterministic process loops, self-healing state, and zero-leakage system architecture.'
  },
  {
    id: 'AGENTIC_ECONOMY',
    label: 'Agentic Economy (A2A)',
    description: 'Agent-to-agent value protocols, automated contracts, and machine settlement.'
  },
  {
    id: 'PRAGMATIC_BUSINESS_STRATEGY',
    label: 'Pragmatic Business Strategy',
    description: 'Theory of constraints, throughput optimization, and operational leverage.'
  },
  {
    id: 'PHILOSOPHY_OF_MIND',
    label: 'Philosophy of Mind',
    description: 'Extended mind hypothesis, deontic supervenience, and cognitive scaffolding.'
  }
];

export const CANON_TYPES: { id: CanonType; label: string }[] = [
  { id: 'MANIFESTO', label: 'Evergreen Manifestos' },
  { id: 'ONTOLOGY', label: 'System Ontology' },
  { id: 'GLOSSARY_TERM', label: 'Glossary of Terms' }
];

export const SEEDED_CANON_ITEMS: CanonItem[] = [
  {
    id: 1,
    slug: 'autonomy-manifesto',
    title: 'Manifesto for Autonomous Process Architecture',
    type: 'MANIFESTO',
    domain: 'AUTONOMOUS_ENGINEERING',
    summary: 'Foundational principles of self-governing software architectures with zero manual intervention requirements.',
    publishedAt: '2026-03-01T10:00:00Z',
    createdAt: '2026-03-01T10:00:00Z',
    updatedAt: null,
    content: `# Manifesto for Autonomous Process Architecture

## Axiom I: Zero-Human Operating Invariants

Software systems must be designed to execute continuously without requiring manual human maintenance, manual config reloads, or human-in-the-loop intervention during normal operation.

> "A system that requires human intervention for standard operating conditions is an uncompleted state machine."

### Core Commandments:
1. **Self-Healing State Transitions** — Any unexpected runtime state must be automatically isolated, logged, and reset to a known-safe invariant state.
2. **Deterministic Replayability** — Every autonomous state transition must be driven by deterministic inputs and seedable random states.
3. **Explicit Deontic Boundaries** — Systems must explicitly define Obligation, Permission, and Prohibition rules for state transitions.

$$
\\mathcal{O}(\\text{State Safety}) \\land \\neg \\mathcal{P}(\\text{Unsanitized Mutation})
$$

## Axiom II: Supervenience over Physical Hardware

The logical architecture of the autonomous loop supervenes strictly on verifiable software state contracts rather than environment artifacts.`,
    outgoingReferences: [
      {
        id: 101,
        sourceSlug: 'autonomy-manifesto',
        sourceTitle: 'Manifesto for Autonomous Process Architecture',
        targetSlug: 'deterministic-execution',
        targetTitle: 'Deterministic State Machines',
        relationshipType: 'DEPENDS_ON',
        description: 'Autonomous processes rely on deterministic state transitions.',
        createdAt: '2026-03-01T10:00:00Z'
      },
      {
        id: 102,
        sourceSlug: 'autonomy-manifesto',
        sourceTitle: 'Manifesto for Autonomous Process Architecture',
        targetSlug: 'deontic-supervenience',
        targetTitle: 'Deontic Supervenience',
        relationshipType: 'REFERENCES',
        description: 'Supervenience framework for logical safety.',
        createdAt: '2026-03-01T10:00:00Z'
      }
    ],
    incomingReferences: []
  },
  {
    id: 2,
    slug: 'deterministic-execution',
    title: 'Deterministic State Machines',
    type: 'ONTOLOGY',
    domain: 'AUTONOMOUS_ENGINEERING',
    summary: 'Ontological definition of pure state transitions and reproducible execution bounds.',
    publishedAt: '2026-03-02T11:00:00Z',
    createdAt: '2026-03-02T11:00:00Z',
    updatedAt: null,
    content: `# Deterministic State Machines

A state machine is defined as deterministic if for any given current state $S_t$ and input $I_t$, the next state $S_{t+1}$ is uniquely determined:

$$
S_{t+1} = f(S_t, I_t, \\sigma)
$$

Where $\\sigma$ represents an explicitly seeded pseudo-random context if non-determinism is required.

### Structural Requirements:
- No hidden global variables or unseeded time dependencies.
- Immutable state snapshots created at every state delta.
- Full cryptographic audit trails for all state transitions.`,
    outgoingReferences: [],
    incomingReferences: [
      {
        id: 101,
        sourceSlug: 'autonomy-manifesto',
        sourceTitle: 'Manifesto for Autonomous Process Architecture',
        targetSlug: 'deterministic-execution',
        targetTitle: 'Deterministic State Machines',
        relationshipType: 'DEPENDS_ON',
        description: 'Autonomous processes rely on deterministic state transitions.',
        createdAt: '2026-03-01T10:00:00Z'
      }
    ]
  },
  {
    id: 3,
    slug: 'a2a-economy-manifesto',
    title: 'Manifesto for Agent-to-Agent Economic Protocols',
    type: 'MANIFESTO',
    domain: 'AGENTIC_ECONOMY',
    summary: 'Architecture of machine-to-machine micro-transactions, automated negotiation, and autonomous resource allocation.',
    publishedAt: '2026-03-05T12:00:00Z',
    createdAt: '2026-03-05T12:00:00Z',
    updatedAt: null,
    content: `# Manifesto for Agent-to-Agent Economic Protocols

In a fully autonomous digital landscape, artificial agents negotiate compute, memory, data access, and API services directly with other artificial agents.

> "Future economic activity will predominantly occur between software agents operating on cryptographic proof systems."

### Primary Pillars:
1. **Micro-Settlement Mechanics** — Instant sub-cent payments with cryptographic proofs.
2. **Programmable SLA Enforcement** — Smart contracts automatically rebalancing payment based on response latency.
3. **Zero-Trust Reputation Nodes** — Agent credentials verified through mathematical proofs of work and past compliance.`,
    outgoingReferences: [
      {
        id: 103,
        sourceSlug: 'a2a-economy-manifesto',
        sourceTitle: 'Manifesto for Agent-to-Agent Economic Protocols',
        targetSlug: 'decentralized-settlement',
        targetTitle: 'Autonomous Machine Settlement',
        relationshipType: 'EXTENDS',
        description: 'Protocol mechanics built on top of settlement layer.',
        createdAt: '2026-03-05T12:00:00Z'
      }
    ],
    incomingReferences: []
  },
  {
    id: 4,
    slug: 'decentralized-settlement',
    title: 'Autonomous Machine Settlement',
    type: 'ONTOLOGY',
    domain: 'AGENTIC_ECONOMY',
    summary: 'Formalization of atomic cryptographic transaction clearing for autonomous agents.',
    publishedAt: '2026-03-06T14:00:00Z',
    createdAt: '2026-03-06T14:00:00Z',
    updatedAt: null,
    content: `# Autonomous Machine Settlement

Settlement between agents requires atomic execution guarantees: either both the resource transfer and financial transfer complete, or neither does.

$$
\\text{Transfer}(A \\to B, \\text{Data}) \\iff \\text{Settle}(B \\to A, \\text{Token})
$$

Eliminating counterparty risk enables frictionless agentic collaboration across open networks.`,
    outgoingReferences: [],
    incomingReferences: [
      {
        id: 103,
        sourceSlug: 'a2a-economy-manifesto',
        sourceTitle: 'Manifesto for Agent-to-Agent Economic Protocols',
        targetSlug: 'decentralized-settlement',
        targetTitle: 'Autonomous Machine Settlement',
        relationshipType: 'EXTENDS',
        description: 'Protocol mechanics built on top of settlement layer.',
        createdAt: '2026-03-05T12:00:00Z'
      }
    ]
  },
  {
    id: 5,
    slug: 'pragmatic-systems-manifesto',
    title: 'Manifesto for Systems-Driven Enterprise Strategy',
    type: 'MANIFESTO',
    domain: 'PRAGMATIC_BUSINESS_STRATEGY',
    summary: 'Applying engineering principles and bottleneck elimination to high-velocity business operations.',
    publishedAt: '2026-03-10T09:00:00Z',
    createdAt: '2026-03-10T09:00:00Z',
    updatedAt: null,
    content: `# Manifesto for Systems-Driven Enterprise Strategy

Business execution is fundamentally a pipeline engineering problem. Organizations fail not from lack of vision, but from unmanaged friction and unrecognized bottlenecks.

> "Local optimization of non-bottlenecks is an organizational delusion."

### Execution Directives:
- **Focus on Constraint Throughput** — Maximize flow at the singular rate-limiting stage.
- **Feedback Loop Compression** — Shorten the cycle time between market signal and operational adjustment.`,
    outgoingReferences: [
      {
        id: 104,
        sourceSlug: 'pragmatic-systems-manifesto',
        sourceTitle: 'Manifesto for Systems-Driven Enterprise Strategy',
        targetSlug: 'bottleneck-elimination',
        targetTitle: 'Theory of Constraints Bottleneck Elimination',
        relationshipType: 'REFERENCES',
        description: 'Methodology for systematic flow optimization.',
        createdAt: '2026-03-10T09:00:00Z'
      }
    ],
    incomingReferences: []
  },
  {
    id: 6,
    slug: 'bottleneck-elimination',
    title: 'Theory of Constraints Bottleneck Elimination',
    type: 'GLOSSARY_TERM',
    domain: 'PRAGMATIC_BUSINESS_STRATEGY',
    summary: 'Systematic 5-step framework for identifying and elevating system bottlenecks.',
    publishedAt: '2026-03-11T15:00:00Z',
    createdAt: '2026-03-11T15:00:00Z',
    updatedAt: null,
    content: `# Theory of Constraints Bottleneck Elimination

A formal term defining the five focusing steps of system throughput improvement:
1. Identify the system constraint.
2. Exploit the constraint.
3. Subordinate everything else to the constraint.
4. Elevate the constraint.
5. Prevent inertia from creating a new unmanaged constraint.`,
    outgoingReferences: [],
    incomingReferences: [
      {
        id: 104,
        sourceSlug: 'pragmatic-systems-manifesto',
        sourceTitle: 'Manifesto for Systems-Driven Enterprise Strategy',
        targetSlug: 'bottleneck-elimination',
        targetTitle: 'Theory of Constraints Bottleneck Elimination',
        relationshipType: 'REFERENCES',
        description: 'Methodology for systematic flow optimization.',
        createdAt: '2026-03-10T09:00:00Z'
      }
    ]
  },
  {
    id: 7,
    slug: 'extended-mind-manifesto',
    title: 'Manifesto for Extended Mind & Cognitive Scaffolding',
    type: 'MANIFESTO',
    domain: 'PHILOSOPHY_OF_MIND',
    summary: 'Philosophical foundation of human-AI cognitive integration, active externalism, and extended memory systems.',
    publishedAt: '2026-03-15T08:00:00Z',
    createdAt: '2026-03-15T08:00:00Z',
    updatedAt: null,
    content: `# Manifesto for Extended Mind & Cognitive Scaffolding

Following Clark and Chalmers' active externalism, human cognition does not stop at the skull or skin. Software tools, mathematical notation, and autonomous agents act as true extended cognitive components.

> "The human mind is an intrinsically hybrid system, continually coupling with environmental structures."

### Cognitive Invariants:
1. **Coupled System Reliability** — An extended cognitive tool must be as reliable as internal biological memory.
2. **Transparent Scaffolding** — Tools should integrate effortlessly into thought loops without perceptual friction.`,
    outgoingReferences: [
      {
        id: 105,
        sourceSlug: 'extended-mind-manifesto',
        sourceTitle: 'Manifesto for Extended Mind & Cognitive Scaffolding',
        targetSlug: 'deontic-supervenience',
        targetTitle: 'Deontic Supervenience',
        relationshipType: 'RELATED',
        description: 'Cognitive boundaries supervene on normative commitments.',
        createdAt: '2026-03-15T08:00:00Z'
      }
    ],
    incomingReferences: []
  },
  {
    id: 8,
    slug: 'deontic-supervenience',
    title: 'Deontic Supervenience',
    type: 'ONTOLOGY',
    domain: 'PHILOSOPHY_OF_MIND',
    summary: 'Ontological thesis that high-level normative states supervene on underlying physical state transitions.',
    publishedAt: '2026-03-16T16:00:00Z',
    createdAt: '2026-03-16T16:00:00Z',
    updatedAt: null,
    content: `# Deontic Supervenience

Supervenience defines a relational dependency between high-level properties (deontic normative safety) and low-level physical facts (binary state registers).

$$
A \\text{ supervenes on } B \\iff \\forall x, y \\ (B(x) = B(y) \\implies A(x) = A(y))
$$

No change in deontic obligation or permission can occur without a corresponding change in underlying system state.`,
    outgoingReferences: [],
    incomingReferences: [
      {
        id: 102,
        sourceSlug: 'autonomy-manifesto',
        sourceTitle: 'Manifesto for Autonomous Process Architecture',
        targetSlug: 'deontic-supervenience',
        targetTitle: 'Deontic Supervenience',
        relationshipType: 'REFERENCES',
        description: 'Supervenience framework for logical safety.',
        createdAt: '2026-03-01T10:00:00Z'
      },
      {
        id: 105,
        sourceSlug: 'extended-mind-manifesto',
        sourceTitle: 'Manifesto for Extended Mind & Cognitive Scaffolding',
        targetSlug: 'deontic-supervenience',
        targetTitle: 'Deontic Supervenience',
        relationshipType: 'RELATED',
        description: 'Cognitive boundaries supervene on normative commitments.',
        createdAt: '2026-03-15T08:00:00Z'
      }
    ]
  }
];

export async function fetchCanonItems(
  domain?: CanonDomain | string,
  type?: CanonType | string,
  page = 0,
  size = 20
): Promise<CanonPage> {
  try {
    const params = new URLSearchParams();
    if (domain) params.append('domain', domain);
    if (type) params.append('type', type);
    params.append('page', page.toString());
    params.append('size', size.toString());

    const response = await fetch(`/api/v1/canon?${params.toString()}`);
    if (response.ok) {
      return await response.json();
    }
  } catch {
    // Fall back to seed data if API is unavailable during static rendering or client tests
  }

  // Local filtering over seed dataset
  let items = [...SEEDED_CANON_ITEMS];
  if (domain && domain !== 'ALL') {
    items = items.filter(item => item.domain === domain);
  }
  if (type && type !== 'ALL') {
    items = items.filter(item => item.type === type);
  }

  const totalElements = items.length;
  const totalPages = Math.ceil(totalElements / size) || 1;
  const pagedItems = items.slice(page * size, (page + 1) * size);

  return {
    content: pagedItems,
    page,
    size,
    totalElements,
    totalPages
  };
}

export async function fetchCanonItemBySlug(slug: string): Promise<CanonItem | null> {
  try {
    const response = await fetch(`/api/v1/canon/${encodeURIComponent(slug)}`);
    if (response.ok) {
      return await response.json();
    }
  } catch {
    // Fall back to seed data
  }

  const item = SEEDED_CANON_ITEMS.find(i => i.slug === slug);
  return item || null;
}
