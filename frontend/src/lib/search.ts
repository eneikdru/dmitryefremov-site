export interface CanonReference {
  id?: number | string;
  sourceSlug: string;
  sourceTitle: string;
  targetSlug: string;
  targetTitle: string;
  relationshipType: 'REFERENCES' | 'SUPERSEDES' | 'RELATED' | 'DEPENDS_ON' | 'EXTENDS';
  description?: string;
}

export interface SearchResult {
  id: string;
  title: string;
  type: 'concept' | 'podcast' | 'essay' | 'spark' | 'quick_command';
  typeLabel: string;
  breadcrumb: string;
  domain?: string;
  snippet: string;
  highlightedSnippet: string;
  incomingLinksCount: number;
  outgoingLinksCount: number;
  wikiLink: string;
  targetUrl: string;
  score: number;
  metadata?: string;
  badge?: string;
}

export interface CorpusData {
  canonItems: any[];
  podcasts: any[];
  essays: any[];
  sparks: any[];
}

export function parseWikiLinks(content: string): string[] {
  if (!content) return [];
  const regex = /\[\[([^\]]+)\]\]/g;
  const matches: string[] = [];
  let match: RegExpExecArray | null;
  while ((match = regex.exec(content)) !== null) {
    if (match[1]) {
      matches.push(match[1].trim());
    }
  }
  return matches;
}

export function highlightMatches(text: string, query: string): string {
  if (!text) return '';
  if (!query || !query.trim()) return escapeHtml(text);

  const cleanQuery = query.trim().replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  const regex = new RegExp(`(${cleanQuery})`, 'gi');
  const parts = text.split(regex);

  return parts
    .map(part => {
      if (part.toLowerCase() === query.trim().toLowerCase()) {
        return `<mark class="bg-primary/20 text-primary-fixed border border-primary/30 rounded px-1 py-0 font-code-inline">${escapeHtml(part)}</mark>`;
      }
      return escapeHtml(part);
    })
    .join('');
}

function escapeHtml(str: string): string {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

export function searchCorpus(
  query: string,
  categoryFilter: string = 'All',
  corpusData: CorpusData
): SearchResult[] {
  const cleanQuery = query.toLowerCase().trim();
  const results: SearchResult[] = [];

  // Helper to check match & calculate score
  const checkMatch = (title: string, body: string, domain: string = '', tags: string = '') => {
    if (!cleanQuery) return 1;
    let score = 0;
    const t = title.toLowerCase();
    const b = body.toLowerCase();
    const d = domain.toLowerCase();
    const tg = tags.toLowerCase();

    if (t.includes(cleanQuery)) score += 10;
    if (t.startsWith(cleanQuery)) score += 5;
    if (b.includes(cleanQuery)) score += 3;
    if (d.includes(cleanQuery)) score += 2;
    if (tg.includes(cleanQuery)) score += 2;

    return score;
  };

  // 1. Canon Items
  if (categoryFilter === 'All' || categoryFilter === 'Concepts') {
    (corpusData.canonItems || []).forEach((item: any) => {
      const title = item.title || item.slug || 'Понятие Канона';
      const body = item.content || item.summary || '';
      const domain = item.domain || 'AUTONOMOUS_ENGINEERING';
      const score = checkMatch(title, body, domain);

      if (score > 0 || !cleanQuery) {
        const incomingCount = Array.isArray(item.incomingReferences) ? item.incomingReferences.length : 8;
        const outgoingCount = Array.isArray(item.outgoingReferences) ? item.outgoingReferences.length : 3;
        results.push({
          id: `canon-${item.slug || item.id}`,
          title,
          type: 'concept',
          typeLabel: 'Concept',
          breadcrumb: `Канон > ${domain.replace(/_/g, ' ')}`,
          domain,
          snippet: body.slice(0, 160) + (body.length > 160 ? '...' : ''),
          highlightedSnippet: highlightMatches(body.slice(0, 160) + (body.length > 160 ? '...' : ''), cleanQuery),
          incomingLinksCount: incomingCount,
          outgoingLinksCount: outgoingCount,
          wikiLink: `[[${title}]]`,
          targetUrl: `/canon/${item.slug || item.id}`,
          score,
          badge: item.type || 'ONTOLOGY',
          metadata: `Domain: ${domain} • ${incomingCount} incoming`
        });
      }
    });
  }

  // 2. Podcasts
  if (categoryFilter === 'All' || categoryFilter === 'Code References' || categoryFilter === 'Podcasts') {
    (corpusData.podcasts || []).forEach((ep: any) => {
      const title = ep.title || 'Выпуск подкаста';
      const body = ep.thesis || ep.description || '';
      const philosopher = ep.philosopher || 'Дмитрий Ефремов';
      const score = checkMatch(title, body, philosopher);

      if (score > 0 || !cleanQuery) {
        results.push({
          id: `podcast-${ep.id || ep.slug}`,
          title,
          type: 'podcast',
          typeLabel: 'Podcast',
          breadcrumb: `Подкаст > ${philosopher}`,
          snippet: body.slice(0, 160) + (body.length > 160 ? '...' : ''),
          highlightedSnippet: highlightMatches(body.slice(0, 160) + (body.length > 160 ? '...' : ''), cleanQuery),
          incomingLinksCount: 5,
          outgoingLinksCount: 2,
          wikiLink: `[[${title}]]`,
          targetUrl: ep.youtubeUrl || ep.mediaLink || '/podcast',
          score,
          badge: 'Audio Episode',
          metadata: `${ep.durationSeconds ? Math.floor(ep.durationSeconds / 60) + ' min' : '45 min'} • ${philosopher}`
        });
      }
    });
  }

  // 3. Essays
  if (categoryFilter === 'All' || categoryFilter === 'Essays' || categoryFilter === 'Concepts') {
    (corpusData.essays || []).forEach((essay: any) => {
      const title = essay.title || 'Эссе';
      const body = essay.summary || essay.contentMarkdown || essay.content || '';
      const topic = essay.topic || 'systems';
      const score = checkMatch(title, body, topic);

      if (score > 0 || !cleanQuery) {
        results.push({
          id: `essay-${essay.slug || essay.id}`,
          title,
          type: 'essay',
          typeLabel: 'Essay',
          breadcrumb: `Эссе > ${topic}`,
          snippet: body.slice(0, 160) + (body.length > 160 ? '...' : ''),
          highlightedSnippet: highlightMatches(body.slice(0, 160) + (body.length > 160 ? '...' : ''), cleanQuery),
          incomingLinksCount: 4,
          outgoingLinksCount: 4,
          wikiLink: `[[${title}]]`,
          targetUrl: `/essays/${essay.slug || essay.id}`,
          score,
          badge: essay.readTime || '5 min read',
          metadata: `Topic: ${topic} • ${essay.readTime || '5 min'}`
        });
      }
    });
  }

  // 4. Sparks
  if (categoryFilter === 'All' || categoryFilter === 'Backlinks' || categoryFilter === 'Sparks') {
    (corpusData.sparks || []).forEach((spark: any) => {
      const title = spark.tag || spark.title || 'Spark';
      const body = spark.body || spark.content || '';
      const tag = spark.tag || '#heuristics';
      const score = checkMatch(title, body, '', tag);

      if (score > 0 || !cleanQuery) {
        results.push({
          id: `spark-${spark.id}`,
          title: `Spark ${tag}`,
          type: 'spark',
          typeLabel: 'Spark',
          breadcrumb: `Sparks > ${tag}`,
          snippet: body.slice(0, 160) + (body.length > 160 ? '...' : ''),
          highlightedSnippet: highlightMatches(body.slice(0, 160) + (body.length > 160 ? '...' : ''), cleanQuery),
          incomingLinksCount: spark.sparksCount || 3,
          outgoingLinksCount: 1,
          wikiLink: `[[${tag}]]`,
          targetUrl: `/sparks`,
          score,
          badge: tag,
          metadata: `${spark.author || '@efremov_mvp'} • ${spark.timestamp || 'Recent'}`
        });
      }
    });
  }

  // Sort by score descending, then title
  return results.sort((a, b) => b.score - a.score || a.title.localeCompare(b.title));
}

export const DEMO_CORPUS_FALLBACK: CorpusData = {
  canonItems: [
    {
      id: 1,
      slug: 'leader-election-term-logic',
      title: 'Leader Election & Term Logic',
      type: 'ONTOLOGY',
      domain: 'AUTONOMOUS_ENGINEERING',
      summary: 'When a follower detects election timeout, it increments term and transitions to candidate, requesting votes from peer nodes.',
      content: 'When a follower detects election timeout, it increments term and transitions to candidate, requesting votes from peer nodes. Randomized election timeouts prevent split-brain scenarios.',
      incomingReferences: [{ sourceTitle: 'Consensus Raft', relationshipType: 'REFERENCES' }],
      outgoingReferences: [{ targetTitle: 'Term Safety Invariant', relationshipType: 'DEPENDS_ON' }]
    },
    {
      id: 2,
      slug: 'raft-split-vote-recovery',
      title: 'Raft Split Vote Recovery',
      type: 'GLOSSARY_TERM',
      domain: 'AUTONOMOUS_ENGINEERING',
      summary: 'Randomized election timeouts prevent split vote split-brain scenarios by ensuring one node initiates candidacy first.',
      content: 'Randomized election timeouts prevent split vote split-brain scenarios by ensuring one node initiates candidacy first.',
      incomingReferences: [],
      outgoingReferences: []
    },
    {
      id: 3,
      slug: 'deontic-logic-autonomous-agents',
      title: 'Deontic Logic & Autonomous Agent Safety',
      type: 'MANIFESTO',
      domain: 'PHILOSOPHY_OF_MIND',
      summary: 'Formal deontic constraints (Obligatory, Forbidden, Permitted) govern autonomous agent transitions under complete uncertainty.',
      content: 'Formal deontic constraints (Obligatory, Forbidden, Permitted) govern autonomous agent transitions under complete uncertainty.',
      incomingReferences: [],
      outgoingReferences: []
    }
  ],
  podcasts: [
    {
      id: 101,
      slug: 'ep-01-raft-consensus',
      title: 'Выпуск 1: Алгоритмы консенсуса и распределённый лидер',
      philosopher: 'Дмитрий Ефремов',
      thesis: 'Разбор теории выбора лидера в Raft, таймаутов голосования и инварианта единого лидера в рамках термина.',
      durationSeconds: 2700,
      youtubeUrl: 'https://youtube.com/@dmitrii.efremov'
    }
  ],
  essays: [
    {
      id: 'essay-1',
      slug: 'a2a-economy-and-autonomy',
      title: 'A2A-Экономика и автономия агентов',
      topic: 'autonomous',
      readTime: '7 min read',
      summary: 'Принципы экономически независимых автономных агентов и их онтологические границы.'
    }
  ],
  sparks: [
    {
      id: 'spk-1',
      tag: '#system-design',
      body: 'Randomized election timeouts prevent split vote split-brain scenarios.',
      author: '@efremov_mvp',
      timestamp: 'Today',
      sparksCount: 8
    }
  ]
};

export async function fetchCorpusData(customFetch = fetch): Promise<CorpusData> {
  try {
    const [canonRes, podcastRes, essayRes, sparkRes] = await Promise.allSettled([
      customFetch('/api/v1/canon'),
      customFetch('/api/v1/podcasts'),
      customFetch('/api/v1/essays'),
      customFetch('/api/v1/sparks')
    ]);

    const canonItems = canonRes.status === 'fulfilled' && canonRes.value.ok ? await canonRes.value.json().then(d => Array.isArray(d) ? d : (d?.content || [])) : [];
    const podcasts = podcastRes.status === 'fulfilled' && podcastRes.value.ok ? await podcastRes.value.json().then(d => Array.isArray(d) ? d : (d?.content || [])) : [];
    const essays = essayRes.status === 'fulfilled' && essayRes.value.ok ? await essayRes.value.json().then(d => Array.isArray(d) ? d : (d?.content || [])) : [];
    const sparks = sparkRes.status === 'fulfilled' && sparkRes.value.ok ? await sparkRes.value.json().then(d => Array.isArray(d) ? d : (d?.content || [])) : [];

    const hasData = (canonItems.length + podcasts.length + essays.length + sparks.length) > 0;

    if (!hasData) {
      return DEMO_CORPUS_FALLBACK;
    }

    return {
      canonItems: canonItems.length > 0 ? canonItems : DEMO_CORPUS_FALLBACK.canonItems,
      podcasts: podcasts.length > 0 ? podcasts : DEMO_CORPUS_FALLBACK.podcasts,
      essays: essays.length > 0 ? essays : DEMO_CORPUS_FALLBACK.essays,
      sparks: sparks.length > 0 ? sparks : DEMO_CORPUS_FALLBACK.sparks
    };
  } catch {
    return DEMO_CORPUS_FALLBACK;
  }
}
