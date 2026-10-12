export interface OpenGraphMeta {
  title: string;
  description: string;
  url: string;
  type: string;
  siteName: string;
  image?: string;
  tags: string[];
}

export interface JsonLdSchema {
  '@context': string;
  '@type': string;
  headline: string;
  description: string;
  author: {
    '@type': string;
    name: string;
  };
  datePublished: string;
  keywords: string[];
}

export interface LLMCrawlerStatus {
  id: string;
  botName: string;
  userAgent: string;
  status: 'Allowed 200 OK' | 'Verified 200 OK' | 'Blocked 403';
  lastFetch: string;
  rankOrToken: string;
  rankSublabel: string;
  telemetryMessage: string;
  frequency: string;
  latencyMs: number;
  tokensUsed?: number;
}

export interface SeoState {
  active: boolean;
  targetUrl: string;
  canonicalStatus: number;
  engineReadinessPercent: number;
  ogMeta: OpenGraphMeta;
  jsonLd: JsonLdSchema;
  crawlers: LLMCrawlerStatus[];
}

export const DEFAULT_SEO_STATE: SeoState = {
  active: true,
  targetUrl: '/blog/ai-search-synthesis',
  canonicalStatus: 200,
  engineReadinessPercent: 98,
  ogMeta: {
    title: 'Next-Gen Semantic Knowledge Engine',
    description: 'Structured metadata and entity definitions optimized for direct LLM synthesis and citation.',
    url: 'https://dmitryefremov.com/blog/ai-search-synthesis',
    type: 'article',
    siteName: 'Dmitry Efremov Engine',
    image: 'https://dmitryefremov.com/assets/og-cover.png',
    tags: ['AI Engineering', 'LLM Indexing', 'Schema.org']
  },
  jsonLd: {
    '@context': 'https://schema.org',
    '@type': 'TechArticle',
    headline: 'Next-Gen Semantic Knowledge Engine',
    description: 'Structured metadata and entity definitions optimized for direct LLM synthesis and citation.',
    author: {
      '@type': 'Person',
      name: 'Dmitry Efremov'
    },
    datePublished: '2026-03-15',
    keywords: ['AI Engineering', 'LLM Indexing', 'Schema.org', 'Deontic Logic']
  },
  crawlers: [
    {
      id: 'perplexity-bot',
      botName: 'PerplexityBot',
      userAgent: 'PerplexityBot/1.0',
      status: 'Allowed 200 OK',
      lastFetch: '12m ago',
      rankOrToken: 'RANK #3',
      rankSublabel: 'Top 3 Citations',
      telemetryMessage: 'Direct citation extraction active with semantic entity anchors.',
      frequency: 'High (Realtime)',
      latencyMs: 38
    },
    {
      id: 'gpt-bot',
      botName: 'GPTBot',
      userAgent: 'OpenAI Search / WebBrowse',
      status: 'Allowed 200 OK',
      lastFetch: '6 mins ago',
      rankOrToken: 'OPTIMIZED',
      rankSublabel: 'Token Budget',
      telemetryMessage: 'Vectorized contextual summary matched with 99.4% cosine similarity.',
      frequency: 'Periodic',
      latencyMs: 45,
      tokensUsed: 1420
    },
    {
      id: 'claude-bot',
      botName: 'ClaudeBot',
      userAgent: 'ClaudeBot/1.0 (Anthropic)',
      status: 'Allowed 200 OK',
      lastFetch: '1h ago',
      rankOrToken: 'INDEXED',
      rankSublabel: 'Extended Context',
      telemetryMessage: 'Deep semantic ontology parsed without DOM execution blocking.',
      frequency: 'Medium',
      latencyMs: 52,
      tokensUsed: 2180
    }
  ]
};

const OG_PROPERTY_PREFIX = 'og:';
const ARTICLE_PROPERTY_PREFIX = 'article:';

export function injectOpenGraphMeta(og: OpenGraphMeta, doc: Document = document): void {
  const head = doc.head || doc.getElementsByTagName('head')[0];
  if (!head) return;

  // Helper to set or create meta tag by property
  const setMetaProperty = (prop: string, content: string) => {
    let meta = head.querySelector(`meta[property="${prop}"]`) as HTMLMetaElement;
    if (!meta) {
      meta = doc.createElement('meta');
      meta.setAttribute('property', prop);
      head.appendChild(meta);
    }
    meta.setAttribute('content', content);
  };

  setMetaProperty('og:title', og.title);
  setMetaProperty('og:description', og.description);
  setMetaProperty('og:url', og.url);
  setMetaProperty('og:type', og.type);
  setMetaProperty('og:site_name', og.siteName);
  if (og.image) {
    setMetaProperty('og:image', og.image);
  }

  // Remove existing article:tag meta tags before appending current tags
  const existingArticleTags = head.querySelectorAll('meta[property="article:tag"]');
  existingArticleTags.forEach(el => el.remove());

  og.tags.forEach(tag => {
    const meta = doc.createElement('meta');
    meta.setAttribute('property', 'article:tag');
    meta.setAttribute('content', tag);
    head.appendChild(meta);
  });
}

export function injectJsonLdSchema(schema: JsonLdSchema, doc: Document = document): void {
  const head = doc.head || doc.getElementsByTagName('head')[0];
  if (!head) return;

  let script = head.querySelector('script[type="application/ld+json"]') as HTMLScriptElement;
  if (!script) {
    script = doc.createElement('script');
    script.setAttribute('type', 'application/ld+json');
    head.appendChild(script);
  }
  script.textContent = JSON.stringify(schema, null, 2);
}

export function removeOpenGraphMeta(doc: Document = document): void {
  const head = doc.head || doc.getElementsByTagName('head')[0];
  if (!head) return;

  const ogTags = head.querySelectorAll('meta[property^="og:"]');
  ogTags.forEach(tag => tag.remove());

  const articleTags = head.querySelectorAll('meta[property^="article:"]');
  articleTags.forEach(tag => tag.remove());
}

export function removeJsonLdSchema(doc: Document = document): void {
  const head = doc.head || doc.getElementsByTagName('head')[0];
  if (!head) return;

  const script = head.querySelector('script[type="application/ld+json"]');
  if (script) {
    script.remove();
  }
}

export function validateMetaTags(doc: Document = document): { valid: boolean; errors: string[] } {
  const errors: string[] = [];
  const head = doc.head || doc.getElementsByTagName('head')[0];

  if (!head) {
    return { valid: false, errors: ['Document head element is missing.'] };
  }

  const requiredOgProperties = ['og:title', 'og:description', 'og:url', 'og:type'];
  requiredOgProperties.forEach(prop => {
    const el = head.querySelector(`meta[property="${prop}"]`);
    if (!el || !el.getAttribute('content')?.trim()) {
      errors.push(`Missing or empty required meta property: ${prop}`);
    }
  });

  const jsonLdScript = head.querySelector('script[type="application/ld+json"]');
  if (!jsonLdScript || !jsonLdScript.textContent?.trim()) {
    errors.push('Missing or empty application/ld+json script tag.');
  } else {
    try {
      const parsed = JSON.parse(jsonLdScript.textContent);
      if (!parsed['@context'] || !parsed['@type']) {
        errors.push('JSON-LD schema must contain @context and @type properties.');
      }
    } catch {
      errors.push('Invalid JSON formatting in application/ld+json script tag.');
    }
  }

  return {
    valid: errors.length === 0,
    errors
  };
}

/**
 * Seedable pseudo-random generator for reproducible bot simulation tests.
 */
function pseudoRandom(seed: number): () => number {
  let s = seed % 2147483647;
  if (s <= 0) s += 2147483646;
  return () => {
    s = (s * 16807) % 2147483647;
    return (s - 1) / 2147483646;
  };
}

export function simulateBotCrawl(
  botName: string,
  seed: number = 42
): { botName: string; status: string; httpStatus: number; latencyMs: number; message: string } {
  const rand = pseudoRandom(seed);
  const latencyMs = Math.floor(20 + rand() * 40); // 20ms - 60ms
  return {
    botName,
    status: 'Verified 200 OK',
    httpStatus: 200,
    latencyMs,
    message: `Bot crawl simulation for ${botName} succeeded with 200 OK in ${latencyMs}ms.`
  };
}
