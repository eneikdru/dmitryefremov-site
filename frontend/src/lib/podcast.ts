export interface PodcastTimecode {
  id: number | string;
  timeOffsetSeconds: number;
  title: string;
  description?: string;
  displayOrder?: number;
}

export interface PodcastCoreThesis {
  id: number | string;
  timestampSeconds: number;
  title: string;
  quote: string;
  description: string;
  category?: string;
  sectionTitle?: string;
}

export interface PodcastEpisode {
  id: number | string;
  slug: string;
  title: string;
  thesis: string;
  description: string;
  audioUrl: string;
  mediaLink?: string;
  youtubeUrl?: string;
  telegramUrl?: string;
  durationSeconds: number;
  publishedAt: string;
  authorName?: string;
  narratorName?: string;
  issueTag?: string;
  timecodes: PodcastTimecode[];
  coreTheses?: PodcastCoreThesis[];
}

export const PODCAST_EPISODES: PodcastEpisode[] = [
  {
    id: 1,
    slug: 'ep-01-systems-and-reality',
    title: 'Выпуск 01: Системы и реальность — Введение в системное мышление',
    thesis: 'Frictionless consumption suppresses associative memory formation and conceptual depth.',
    description: 'A deep dive into cognitive dynamics, synthetic fluency, and how deliberate friction preserves associative reasoning in high-velocity media environments.',
    audioUrl: 'https://cdn.dmitryefremov.com/audio/podcast-ep01.mp3',
    mediaLink: 'https://youtube.com/@efremov_dmitriy',
    youtubeUrl: 'https://youtube.com/@efremov_dmitriy',
    telegramUrl: 'https://t.me/efremov_mvp',
    durationSeconds: 2535, // 42:15
    publishedAt: '2026-03-01',
    authorName: 'Elena Vance',
    narratorName: 'Marcus Sterling',
    issueTag: 'ISSUE 48 · THE ALGORITHMIC SUBLIME',
    timecodes: [
      { id: 101, timeOffsetSeconds: 0, title: 'Introduction: The Disappearance of Cognitive Resistance', description: 'Context and foundations of modern cognitive processing', displayOrder: 1 },
      { id: 102, timeOffsetSeconds: 195, title: 'Part I: Speed vs. Comprehension in the Generative Era', description: 'Comparing processing rates against long-term conceptual retention', displayOrder: 2 },
      { id: 103, timeOffsetSeconds: 525, title: 'Part II: The Atrophy of Friction', description: 'How automated summaries remove necessary cognitive labor', displayOrder: 3 },
      { id: 104, timeOffsetSeconds: 880, title: 'Part III: Epistemic Debt and the New Illiteracy', description: 'Structural logical omissions disguised as fluent output', displayOrder: 4 },
      { id: 105, timeOffsetSeconds: 1120, title: 'Conclusion: Designing for Deliberate Contemplation', description: 'Principles for building deep reading and listening interfaces', displayOrder: 5 }
    ],
    coreTheses: [
      {
        id: 't1-1',
        timestampSeconds: 525, // 08:45
        title: 'Frictionless Consumption & Retention',
        category: 'CENTRAL THESIS',
        quote: '“Frictionless consumption suppresses associative memory formation.”',
        description: 'When generative summarizers eliminate the cognitive labour of parsing, neural retention rates plummet by 37% across repeated trials.',
        sectionTitle: 'Part II: The Atrophy of Friction'
      },
      {
        id: 't1-2',
        timestampSeconds: 880, // 14:40
        title: 'Illusion of Cognitive Mastery',
        category: 'CORE PREMISE II',
        quote: '“Synthetic fluency creates an illusion of cognitive mastery without conceptual scaffolding.”',
        description: 'Polished syntax is mistaken for verified truth, leaving the reader disarmed against structural logical omissions.',
        sectionTitle: 'Part III: Epistemic Debt'
      },
      {
        id: 't1-3',
        timestampSeconds: 1120, // 18:40
        title: 'Biological Speed of Original Synthesis',
        category: 'SYNTHESIS CONCLUSION',
        quote: '“Slow retrieval is not latency; it is the biological mechanism of original synthesis.”',
        description: 'Delay produces combinatorial collisions that real-time predictive feeds intentionally suppress for speed.',
        sectionTitle: 'Conclusion: Deliberation'
      }
    ]
  },
  {
    id: 2,
    slug: 'ep-02-deontic-invariants',
    title: 'Выпуск 02: Деонтическая логика и нормативные инварианты',
    thesis: 'Mathematical rigor of Obligation, Permission, and Prohibition in distributed systems.',
    description: 'Formalizing modal operators O(phi), P(phi), F(phi) into runtime safety guarantees and atomic state transition guards.',
    audioUrl: 'https://cdn.dmitryefremov.com/audio/podcast-ep02.mp3',
    mediaLink: 'https://t.me/efremov_mvp',
    youtubeUrl: 'https://youtube.com/@efremov_dmitriy',
    telegramUrl: 'https://t.me/efremov_mvp',
    durationSeconds: 980,
    publishedAt: '2026-03-15',
    authorName: 'Dmitry Efremov',
    narratorName: 'Dmitry Efremov',
    issueTag: 'ISSUE 49 · DEONTIC INVARIANTS',
    timecodes: [
      { id: 201, timeOffsetSeconds: 0, title: 'Вводный обзор модальной логики', description: 'Истоки деонтических систем', displayOrder: 1 },
      { id: 202, timeOffsetSeconds: 260, title: 'Операторы O, P и F', description: 'Связь Долженствования, Разрешения и Запрета', displayOrder: 2 },
      { id: 203, timeOffsetSeconds: 610, title: 'Атомарность и супервентность', description: 'Гарантии от гонок состояний', displayOrder: 3 },
      { id: 204, timeOffsetSeconds: 840, title: 'Вопросы и ответы', description: 'Разбор реальных сценариев из архитектуры', displayOrder: 4 }
    ],
    coreTheses: [
      {
        id: 't2-1',
        timestampSeconds: 260,
        title: 'Normative Operators as Safety Invariants',
        category: 'PRIMARY THESIS',
        quote: '“Modal operators convert implicit rules into explicit architectural guarantees.”',
        description: 'System safety is preserved through mathematical constraints over state transitions rather than ad-hoc defensive patches.',
        sectionTitle: 'Операторы O, P и F'
      },
      {
        id: 't2-2',
        timestampSeconds: 610,
        title: 'Atomic Supervenience',
        category: 'THEORETICAL BASIS',
        quote: '“Supervenience prevents race conditions at the physical layer.”',
        description: 'Optimistic updates must be guarded by atomic state transition rules to avoid concurrent state resurrection.',
        sectionTitle: 'Атомарность и супервентность'
      }
    ]
  }
];

export function formatTime(seconds: number): string {
  if (isNaN(seconds) || seconds < 0) return '00:00';
  const rounded = Math.floor(seconds);
  const mins = Math.floor(rounded / 60);
  const secs = rounded % 60;
  const hrs = Math.floor(mins / 60);
  const remMins = mins % 60;

  if (hrs > 0) {
    return `${hrs.toString().padStart(2, '0')}:${remMins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }
  return `${remMins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
}

export function generateWaveformHeights(count: number, seed: string = 'default'): number[] {
  let hash = 0;
  for (let i = 0; i < seed.length; i++) {
    hash = (hash << 5) - hash + seed.charCodeAt(i);
    hash |= 0;
  }

  const heights: number[] = [];
  for (let i = 0; i < count; i++) {
    // Deterministic trigonometric pseudo-random generator
    const val = Math.abs(Math.sin(hash + (i + 1) * 2.3) * Math.cos(i * 0.7));
    // Scale height between 15% and 100%
    const heightPercent = Math.max(15, Math.min(100, Math.round(val * 100)));
    heights.push(heightPercent);
  }
  return heights;
}
