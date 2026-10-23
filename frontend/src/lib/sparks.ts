export interface FrameworkStep {
  id: number;
  text: string;
  action?: string;
  isEliminated?: boolean;
}

export interface AudioSnippet {
  duration: string;
  transcript: string;
}

export interface TheoremContrast {
  linear: string;
  nonLinear: string;
}

export type SparkType = 'insight' | 'framework' | 'quote' | 'audio' | 'theorem';

export interface SparkItem {
  id: string;
  author: string;
  authorInitial: string;
  timestamp: string;
  tag: string;
  type: SparkType;
  sparksCount: number;
  readTime?: string;
  body?: string;
  title?: string;
  frameworkSteps?: FrameworkStep[];
  audioSnippet?: AudioSnippet;
  theoremContrast?: TheoremContrast;
  isFired?: boolean;
  isBookmarked?: boolean;
}

export type StreamMode = 'HYPER' | 'FOCUS';

export const SPARK_CATEGORIES = [
  'All Sparks',
  '#mental-models',
  '#system-design',
  '#ai-futures',
  '#philosophy',
  '#product-craft',
  '#heuristics'
] as const;

export function getInitialSparks(): SparkItem[] {
  return [
    {
      id: 'spk-1',
      author: '@alexp',
      authorInitial: 'A',
      timestamp: '14m ago',
      tag: '#architecture',
      type: 'insight',
      sparksCount: 1400,
      readTime: '3s',
      body: '"Most software complexity isn\'t accidental or essential; it\'s social structure fossilized in code."',
      isFired: false,
      isBookmarked: false
    },
    {
      id: 'spk-2',
      author: '@rachel.v',
      authorInitial: 'R',
      timestamp: '48m ago',
      tag: '#psychology',
      type: 'framework',
      sparksCount: 890,
      title: 'Atomic Feedback Loop:',
      frameworkSteps: [
        { id: 1, text: 'Trigger' },
        { id: 2, text: '2. Frictionless action', action: 'Eliminate', isEliminated: true },
        { id: 3, text: 'Instant dopamine marker.' }
      ],
      body: '"Remove step 2. Collapse decision latencies into zero."',
      isFired: false,
      isBookmarked: false
    },
    {
      id: 'spk-3',
      author: '@neuro_dr',
      authorInitial: 'N',
      timestamp: '1h ago',
      tag: '#cognition',
      type: 'quote',
      sparksCount: 2100,
      body: '"Speed of thought is bounded by the speed of retrieval, not processing power."',
      isFired: true,
      isBookmarked: false
    },
    {
      id: 'spk-4',
      author: '@dan_k',
      authorInitial: 'D',
      timestamp: '2h ago',
      tag: '#ux-heuristics',
      type: 'audio',
      sparksCount: 640,
      audioSnippet: {
        duration: '0:12',
        transcript: '"The best UX is an invisible state transition."'
      },
      isFired: false,
      isBookmarked: false
    },
    {
      id: 'spk-5',
      author: '@strat_core',
      authorInitial: 'S',
      timestamp: '3h ago',
      tag: '#strategy',
      type: 'theorem',
      sparksCount: 3400,
      theoremContrast: {
        linear: 'Rewards consensus and known metrics.',
        nonLinear: 'Rewards pure asymmetric conviction.'
      },
      isFired: false,
      isBookmarked: false
    }
  ];
}

export function filterSparks(sparks: SparkItem[], selectedCategory: string, searchQuery: string = ''): SparkItem[] {
  let result = [...sparks];

  if (selectedCategory && selectedCategory !== 'All Sparks') {
    const cleanCategory = selectedCategory.replace(/^#/, '').toLowerCase();
    result = result.filter(spark => {
      const sparkTag = spark.tag.replace(/^#/, '').toLowerCase();
      return sparkTag === cleanCategory || spark.tag.toLowerCase() === selectedCategory.toLowerCase();
    });
  }

  if (searchQuery.trim()) {
    const q = searchQuery.toLowerCase().trim();
    result = result.filter(spark => {
      const bodyText = spark.body || '';
      const titleText = spark.title || '';
      const authorText = spark.author || '';
      const tagText = spark.tag || '';
      const transcriptText = spark.audioSnippet?.transcript || '';
      const linearText = spark.theoremContrast?.linear || '';
      const nonLinearText = spark.theoremContrast?.nonLinear || '';

      return (
        bodyText.toLowerCase().includes(q) ||
        titleText.toLowerCase().includes(q) ||
        authorText.toLowerCase().includes(q) ||
        tagText.toLowerCase().includes(q) ||
        transcriptText.toLowerCase().includes(q) ||
        linearText.toLowerCase().includes(q) ||
        nonLinearText.toLowerCase().includes(q)
      );
    });
  }

  return result;
}

export function createSpark(text: string, author: string = '@dmitryefremov', tag: string = '#heuristics'): SparkItem {
  const trimmed = text.trim().slice(0, 180);
  const initial = author.replace(/^@/, '').charAt(0).toUpperCase() || 'D';

  return {
    id: `spk-user-${Date.now()}`,
    author,
    authorInitial: initial,
    timestamp: 'Just now',
    tag: tag.startsWith('#') ? tag : `#${tag}`,
    type: 'insight',
    sparksCount: 1,
    readTime: '1s',
    body: `"${trimmed}"`,
    isFired: true,
    isBookmarked: false
  };
}

export function toggleSparkFire(sparks: SparkItem[], id: string): SparkItem[] {
  return sparks.map(spark => {
    if (spark.id === id) {
      const isFired = !spark.isFired;
      return {
        ...spark,
        isFired,
        sparksCount: isFired ? spark.sparksCount + 1 : spark.sparksCount - 1
      };
    }
    return spark;
  });
}

export function toggleSparkBookmark(sparks: SparkItem[], id: string): SparkItem[] {
  return sparks.map(spark => {
    if (spark.id === id) {
      return {
        ...spark,
        isBookmarked: !spark.isBookmarked
      };
    }
    return spark;
  });
}

export function calculateTelemetry(sparks: SparkItem[]) {
  const totalCount = sparks.length;
  const totalSparksAccumulated = sparks.reduce((acc, s) => acc + s.sparksCount, 0);

  return {
    sparksPerMinute: 42,
    throughputWpm: 142,
    consumedToday: 84 + (totalCount - 5),
    totalCount,
    totalSparksAccumulated
  };
}
