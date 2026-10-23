export type DomainCategory =
  | 'Autonomous Engineering'
  | 'Agentic Economy (A2A)'
  | 'Pragmatic Business Strategy'
  | 'Philosophy of Mind';

export interface ScholiumNote {
  title: string;
  author: string;
  content: string;
}

export interface TermAnnotation {
  id: number;
  term: string;
  transliteration?: string;
  category: string;
  definition: string;
}

export interface CanonManifesto {
  id: string;
  domain: DomainCategory;
  chapterNumber: string;
  chapterTitle: string;
  subtitle: string;
  translator: string;
  epigraph: {
    quote: string;
    citation: string;
  };
  estimatedReadTimeMinutes: number;
  author: string;
  sections: {
    sectionNumber: string;
    sectionTitle: string;
    dropCapText?: string;
    bodyParagraphs: string[];
    highlightedPassage?: {
      text: string;
      scholium?: ScholiumNote;
    };
    pullQuote?: {
      quote: string;
      citation: string;
    };
    annotation?: TermAnnotation;
  }[];
  colophon: string;
  location: string;
  narration: {
    narrator: string;
    durationMinutes: number;
    currentTimeSeconds: number;
  };
}

export const CANON_MANIFESTOS: CanonManifesto[] = [
  {
    id: 'meditations-book-4-mind',
    domain: 'Philosophy of Mind',
    chapterNumber: 'CHAPTER IV',
    chapterTitle: 'On the Soul & Transitory Nature',
    subtitle: 'Translation by Gregory Hays, with scholarly apparatus',
    translator: 'Gregory Hays',
    epigraph: {
      quote: '“Omnia fluxit: nihil in perpetuum manet, sed mens solum quietem praestat.”',
      citation: '— Medit. Graecae IV.3'
    },
    estimatedReadTimeMinutes: 18,
    author: 'Marcus Aurelius',
    sections: [
      {
        sectionNumber: '§ 3',
        sectionTitle: 'RETREAT INTO THE INNER CITADEL',
        dropCapText: 'They seek out retreats for themselves in the countryside, by the seashore, or in the mountains, and you yourself are particularly given to craving such solitude. But all this is completely unphilosophical, when it is open to you at any moment to retreat into yourself.',
        bodyParagraphs: [
          'For nowhere can a man find a retreat more peaceful or more free from trouble than in his own soul, especially if he has within himself the kind of thoughts which, when he contemplates them, immediately bring about complete equanimity—and by equanimity I mean nothing other than a well-ordered mind.'
        ],
        highlightedPassage: {
          text: 'Constantly grant yourself this retreat, then, and renew yourself. Let your basic principles be brief and fundamental, the kind that will at once, on close acquaintance, purge all sorrow and send you back without grievance',
          scholium: {
            title: 'SCHOLIA · THE INNER CITADEL',
            author: 'P. Hadot',
            content: "Pierre Hadot identifies this sanctuary as the unbreachable fortress of the mind: unaffected by outside impressions unless sanctioned by one's own judgment."
          }
        },
        pullQuote: {
          quote: '“The universe is change; our life is what our thoughts make it.”',
          citation: '— Meditations IV.3.iv'
        }
      },
      {
        sectionNumber: '§ 4',
        sectionTitle: 'THE RULING GOVERNING PRINCIPLE',
        bodyParagraphs: [
          'If the power of thought is common to us all, then reason, by which we are rational beings, is also common. If so, then too the reason that tells us what to do and what not to do is common. If so, there is a common law; and if so, we are fellow citizens.',
          'Therefore the cosmos is like a single commonwealth. Whence else could our common intellectual capacity derive, or our reason, or our reverence for law? Just as the terrestrial part of my nature is an offshoot of some earth, so my cognitive faculty stems from the sovereign center.'
        ],
        annotation: {
          id: 1,
          term: 'Hēgemonikon',
          transliteration: 'ἡγεμονικόν',
          category: 'Philosophical Term',
          definition: 'Literally, "The Ruling Center" or directive faculty in Stoic psychology. The intellectual nucleus of the soul capable of assenting to or denying presentations, entirely immune to bodily illness or external ruin unless it surrenders its sovereign reason.'
        }
      }
    ],
    colophon: 'End of Section IV.4 · 44 Sections Remaining in Book IV',
    location: 'Recorded at Sirmium and Carnuntum on the northern Danube frontier',
    narration: {
      narrator: 'Michael H.',
      durationMinutes: 18,
      currentTimeSeconds: 252
    }
  },
  {
    id: 'autonomous-engineering-manifesto',
    domain: 'Autonomous Engineering',
    chapterNumber: 'CHAPTER I',
    chapterTitle: 'Determinism & Formal Invariants',
    subtitle: 'Principles of Machine Self-Governance & Verification',
    translator: 'Dmitry Efremov',
    epigraph: {
      quote: '“Non-determinism in autonomous loops is not a feature; it is an unhedged systematic liability.”',
      citation: '— Autonomous Process Invariants §1.2'
    },
    estimatedReadTimeMinutes: 14,
    author: 'Dmitry Efremov',
    sections: [
      {
        sectionNumber: '§ 1',
        sectionTitle: 'SUPERVENIENCE OF STATE SPACE',
        dropCapText: 'In autonomous software engineering, every runtime state must supervene deterministically upon explicit inputs, environment seeds, and formal rules. Without zero-side-effect isolation, software factories collapse under emergent ambient noise.',
        bodyParagraphs: [
          'System integrity is preserved not through continuous runtime monkey-patching, but through mathematical supervenience over bounded state spaces. State transitions must satisfy formal deontic obligations before state mutation is committed.'
        ],
        highlightedPassage: {
          text: 'Any non-deterministic source (timestamps, entropy generators, external API payloads) crossing an isolation boundary must be explicitly seeded and injected.',
          scholium: {
            title: 'SCHOLIA · REPRODUCIBILITY INVARIANT',
            author: 'Eneik Architecture Review',
            content: 'Deterministic replayability allows automated factory auditors to reconstruct exact agent decision trees post-facto without side-effect leakage.'
          }
        },
        pullQuote: {
          quote: '“Your code and your decisions are who you truly are.”',
          citation: '— Manifest of Autonomous Engineering'
        }
      },
      {
        sectionNumber: '§ 2',
        sectionTitle: 'CONDITIONAL ATOMIC GUARANTEES',
        bodyParagraphs: [
          'When updating state variables with terminal values, database and in-memory writes must utilize conditionally guarded atomic updates (UPDATE ... WHERE id = ? AND status = ?).',
          'A plain read-then-save leaves a race-condition window where concurrent processes can silently resurrect or duplicate finalized work.'
        ],
        annotation: {
          id: 2,
          term: 'Optimistic Guard',
          transliteration: 'Atomic CAS',
          category: 'Engineering Term',
          definition: 'A Compare-And-Swap database or memory invariant requiring that state transitions explicitly check initial state expectations before executing write side-effects.'
        }
      }
    ],
    colophon: 'End of Section I.2 · Autonomous Engineering Manifest',
    location: 'Dmitry Efremov Autonomous Lab · Zurich / Remote',
    narration: {
      narrator: 'Elena V.',
      durationMinutes: 14,
      currentTimeSeconds: 120
    }
  },
  {
    id: 'agentic-economy-a2a',
    domain: 'Agentic Economy (A2A)',
    chapterNumber: 'CHAPTER II',
    chapterTitle: 'Zero-Trust Protocol & Agent Market Dynamics',
    subtitle: 'The Economics of Autonomous Agent Interaction',
    translator: 'Dmitry Efremov',
    epigraph: {
      quote: '“Agents do not trust promises; agents verify cryptographic signatures and invariant proofs.”',
      citation: '— A2A Protocol Whitepaper §3'
    },
    estimatedReadTimeMinutes: 16,
    author: 'Dmitry Efremov',
    sections: [
      {
        sectionNumber: '§ 1',
        sectionTitle: 'MACHINE-TO-MACHINE VALUE FLOWS',
        dropCapText: 'The transition from human-driven API requests to high-density machine transactions requires machine-readable semantic indexes, zero-cookie telemetry, and standardized open contracts.',
        bodyParagraphs: [
          'Providing machine endpoints like /.well-known/llms.txt and /.well-known/ai-agent.json guarantees that autonomous agents can consume domain ontologies without DOM/JS blocking overhead.'
        ],
        highlightedPassage: {
          text: 'High-density text representations allow crawler bots (PerplexityBot, GPTBot, ClaudeBot) to index concept trees instantly.',
          scholium: {
            title: 'SCHOLIA · SEMANTIC SEO',
            author: 'A2A Indexing Standard',
            content: 'Structured JSON-LD schema paired with pristine Markdown index files reduces LLM web crawling latency by up to 85%.'
          }
        }
      }
    ],
    colophon: 'End of Section II.1 · A2A Protocol Architecture',
    location: 'Autonomous Telemetry Hub',
    narration: {
      narrator: 'Alexander R.',
      durationMinutes: 16,
      currentTimeSeconds: 300
    }
  },
  {
    id: 'pragmatic-business-strategy',
    domain: 'Pragmatic Business Strategy',
    chapterNumber: 'CHAPTER III',
    chapterTitle: 'Theory of Constraints & Bottleneck Elevation',
    subtitle: 'Systemic Maximization of Output Invariants',
    translator: 'Dmitry Efremov',
    epigraph: {
      quote: '“Local optimization at non-bottlenecks is an organizational illusion.”',
      citation: '— TOC Business Strategy §4'
    },
    estimatedReadTimeMinutes: 15,
    author: 'Dmitry Efremov',
    sections: [
      {
        sectionNumber: '§ 1',
        sectionTitle: 'THE FIVE FOCUSING STEPS',
        dropCapText: 'Any manageable system is limited in achieving its throughput goals by a tiny number of critical constraints. Identifying and elevating the primary bottleneck governs true business velocity.',
        bodyParagraphs: [
          'Subordinating all secondary activities to the throughput capacity of the system constraint ensures that capital and cognitive bandwidth are never wasted on false local optimizations.'
        ]
      }
    ],
    colophon: 'End of Section III.1 · Strategic Constraints',
    location: 'Executive Strategy Vault',
    narration: {
      narrator: 'Michael H.',
      durationMinutes: 15,
      currentTimeSeconds: 180
    }
  }
];

export function getManifestosByDomain(domain: DomainCategory): CanonManifesto[] {
  return CANON_MANIFESTOS.filter(m => m.domain === domain);
}

export function formatTimeSeconds(seconds: number): string {
  const m = Math.floor(seconds / 60);
  const s = Math.floor(seconds % 60);
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
}
