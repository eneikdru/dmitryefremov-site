export interface Footnote {
  id: number;
  term: string;
  category: string;
  definition: string;
}

export interface Scholia {
  id: string;
  title: string;
  author: string;
  text: string;
}

export interface CanonChapter {
  id: string;
  title: string;
  subtitle: string;
  translator: string;
  epigraph: {
    text: string;
    source: string;
  };
  scholia: Scholia;
  footnotes: Record<number, Footnote>;
  estimatedMinutes: number;
}

export const DEFAULT_CANON_CHAPTER: CanonChapter = {
  id: 'meditations-book-4',
  title: 'BOOK IV: MEDITATIONS',
  subtitle: 'On the Soul & Transitory Nature',
  translator: 'Translation by Gregory Hays, with scholarly apparatus',
  estimatedMinutes: 18,
  epigraph: {
    text: '“Omnia fluxit: nihil in perpetuum manet, sed mens solum quietem praestat.”',
    source: '— Medit. Graecae IV.3'
  },
  scholia: {
    id: 'inner-citadel',
    title: 'SCHOLIA · THE INNER CITADEL',
    author: 'P. Hadot',
    text: "Pierre Hadot identifies this sanctuary as the unbreachable fortress of the mind: unaffected by outside impressions unless sanctioned by one's own judgment."
  },
  footnotes: {
    1: {
      id: 1,
      term: 'Hēgemonikon (ἡγεμονικόν)',
      category: 'Philosophical Term',
      definition: 'Literally, "The Ruling Center" or directive faculty in Stoic psychology. The intellectual nucleus of the soul capable of assenting to or denying presentations, entirely immune to bodily illness or external ruin unless it surrenders its sovereign reason.'
    }
  }
};

export function calculateReadProgress(scrollTop: number, scrollHeight: number, clientHeight: number): number {
  if (scrollHeight <= clientHeight) return 100;
  const progress = (scrollTop / (scrollHeight - clientHeight)) * 100;
  return Math.min(Math.max(Math.round(progress), 0), 100);
}
