export interface SparkItem {
  id: string;
  code: string;
  category: 'AI' | 'BIO' | 'SYSTEMS' | 'RAW' | 'PHILOSOPHY' | 'BUSINESS';
  domain: 'Autonomous Engineering' | 'Agentic Economy' | 'Pragmatic Business Strategy' | 'Philosophy of Mind';
  timeAgo: string;
  signalPercent?: number;
  content: string;
  progressPercent?: number;
  sparksCount: number;
  branchesCount: number;
  isBookmarked: boolean;
  tags?: string[];
  author?: {
    handle: string;
    avatarLetter: string;
    rep: string;
  };
  repliesCount?: number;
  isDraft?: boolean;
  tokenCount?: number;
  charCount?: number;
  hasAudioWaveform?: boolean;
}

export const INITIAL_SPARKS: SparkItem[] = [
  {
    id: 'spk-084',
    code: 'Q-084 // AI',
    category: 'AI',
    domain: 'Autonomous Engineering',
    timeAgo: '2m ago',
    signalPercent: 98,
    content: 'Context windows are not memory; they are working tables. Genuine agency requires state mutation outside the attention horizon.',
    progressPercent: 78,
    sparksCount: 42,
    branchesCount: 12,
    isBookmarked: false,
    tags: ['#ai', '#context-window', '#autonomy']
  },
  {
    id: 'spk-085',
    code: 'Q-085 // BIO',
    category: 'BIO',
    domain: 'Philosophy of Mind',
    timeAgo: '5m ago',
    content: 'Evolution never optimizes for truth, only fitness. Human cognitive biases are aerodynamic drag profiles tuned for survival, not clarity.',
    sparksCount: 89,
    branchesCount: 15,
    isBookmarked: true,
    tags: ['#epistemology', '#evolution', '#mind'],
    progressPercent: 94
  },
  {
    id: 'spk-086',
    code: 'Q-086 // SYSTEMS',
    category: 'SYSTEMS',
    domain: 'Agentic Economy',
    timeAgo: '8m ago',
    content: 'Latent space navigation is the new search paradigm. In 3 years, SQL queries will feel like punch cards.',
    progressPercent: 62,
    sparksCount: 34,
    branchesCount: 8,
    isBookmarked: false,
    hasAudioWaveform: true,
    author: {
      handle: '@kevin.eth',
      avatarLetter: 'K',
      rep: '4.8k'
    },
    repliesCount: 27
  },
  {
    id: 'spk-087',
    code: 'Q-087 // RAW',
    category: 'RAW',
    domain: 'Pragmatic Business Strategy',
    timeAgo: '14m ago',
    content: '“Attention is the only non-fungible asset.”',
    sparksCount: 19,
    branchesCount: 3,
    isBookmarked: false,
    isDraft: true,
    tokenCount: 1,
    charCount: 47
  }
];

export function toggleSparkCount(sparks: SparkItem[], id: string): SparkItem[] {
  return sparks.map(s => {
    if (s.id === id) {
      return { ...s, sparksCount: s.sparksCount + 1 };
    }
    return s;
  });
}

export function toggleBookmark(sparks: SparkItem[], id: string): SparkItem[] {
  return sparks.map(s => {
    if (s.id === id) {
      return { ...s, isBookmarked: !s.isBookmarked };
    }
    return s;
  });
}

export function addQuantizedThought(
  sparks: SparkItem[],
  text: string,
  category: SparkItem['category'] = 'RAW',
  domain: SparkItem['domain'] = 'Autonomous Engineering'
): SparkItem[] {
  const newId = `spk-${Date.now().toString().slice(-3)}`;
  const newSpark: SparkItem = {
    id: newId,
    code: `Q-${Math.floor(100 + Math.random() * 900)} // ${category}`,
    category,
    domain,
    timeAgo: 'just now',
    signalPercent: 99,
    content: text,
    progressPercent: 100,
    sparksCount: 1,
    branchesCount: 0,
    isBookmarked: false,
    tokenCount: Math.ceil(text.length / 4),
    charCount: text.length
  };
  return [newSpark, ...sparks];
}
