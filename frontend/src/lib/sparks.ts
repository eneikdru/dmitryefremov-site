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
  return [];
}

export async function fetchSparksFromApi(customFetch = fetch): Promise<SparkItem[]> {
  try {
    const res = await customFetch('/api/v1/sparks');
    if (!res.ok) return [];
    const data = await res.json();
    const rawList = Array.isArray(data) ? data : (data?.content || []);
    return rawList.map((spk: any) => {
      const author = spk.author || '';
      const authorInitial = author ? author.replace(/^@/, '').charAt(0).toUpperCase() : '';
      return {
        id: String(spk.id || spk.telegramMessageId || `spk-${Math.random()}`),
        telegramMessageId: spk.telegramMessageId,
        author,
        authorInitial,
        timestamp: spk.publishedAt ? String(spk.publishedAt).slice(0, 16).replace('T', ' ') : 'Запись',
        tag: spk.tags ? (spk.tags.startsWith('#') ? spk.tags : `#${spk.tags}`) : '#sparks',
        type: 'insight' as SparkType,
        sparksCount: 1,
        readTime: '1s',
        body: spk.content || '',
        domain: spk.domain || '',
        publishedAt: spk.publishedAt ? String(spk.publishedAt).slice(0, 16).replace('T', ' ') : '',
        isFired: false,
        isBookmarked: false
      };
    });
  } catch {
    return [];
  }
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

export function createSpark(text: string, author: string = '', tag: string = '#heuristics'): SparkItem {
  const trimmed = text.trim().slice(0, 180);
  const initial = author ? author.replace(/^@/, '').charAt(0).toUpperCase() : '';

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
  const totalSparksAccumulated = sparks.reduce((acc, s) => acc + (s.sparksCount || 1), 0);

  return {
    sparksPerMinute: totalCount > 0 ? Math.min(totalCount, 12) : 0,
    throughputWpm: totalCount > 0 ? totalCount * 18 : 0,
    consumedToday: totalCount,
    totalCount,
    totalSparksAccumulated
  };
}
